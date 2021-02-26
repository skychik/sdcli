package ru.ifmo.jb.hw.sdcli

import java.io.Closeable
import java.io.InputStream
import java.util.Scanner

/**
 * Used instead of Scanner, because we can't simply read until the end of line,
 * i.e. "echo '\n' f" shouldn't stop reading after \n
 * Has parsing methods to separate commands and their arguments while piping.
 * Works fine with single quote and double quote separation, and also with inlining of variables
 */
// TODO: maybe need to use StringBuilder
class BushScanner(private val inputStream: InputStream) : Closeable {
    private var sc = Scanner(inputStream) // TODO: should close it?
    private var line = ""

    fun nextChar(): Char {
        while (line.isEmpty()) {
            line = sc.nextLine()
            line += '\n'
        }
        val c = line.first()
        line = line.drop(1)
        return c
    }

    // TODO: doesnt work properly with \n. Ex:
    //  echo "
    //  sds
    //  "
    /**
     * Main parsing function. Recursive string parsing is used as a more suited thing for this purpose
     * Reads input from stdin symbol by symbol
     * @param cmnds - is a list of piped programs to return: if there was no piping - only one string in list returned
     * @param c - to read a char from Console object. But we also can pass a previous character here
     */
    fun readCommands(
        cmnds: MutableList<MutableList<Token>> = mutableListOf(mutableListOf("")),
        c: Char = this.nextChar()
    ): List<List<Token>> {
        return when (c) {
            '\n' -> {
                cmnds.removeLastEmptyToken()
                cmnds
            }
            '\'' -> {
                cmnds.addToken(readSingleQuoteString())
                readCommands(cmnds)
            }
            '\"' -> {
                cmnds.addToken(readDoubleQuoteString())
                readCommands(cmnds)
            }
            '|' -> {
                // TODO: обрабатывать подряд идущие |
                cmnds.removeLastEmptyToken()
                cmnds.addNewCommand()
                readCommands(cmnds)
            }
            '$' -> {
                val variable = readVariable()
                
                // was it a variable?
                if (variable.length > 1) {
                    cmnds.addToken(variable.dropLast(1))
                } else {
                    cmnds.appendToken("$")
                }
                readCommands(cmnds, c = variable.last())
            }
            ' ' -> {
                cmnds.removeLastEmptyToken()
                cmnds.addToken("")
                readCommands(cmnds)
            }
            else -> {
                // TODO: maybe use Scanner.useDelimiter()
                cmnds.appendToken("$c")
                readCommands(cmnds)
            }
        }
    }

    /**
     * Is called to parse single quoted expressions
     */
    fun readSingleQuoteString(head: String = "", c: Char = this.nextChar()): String {
        return when (c) {
            '\'' -> head
            else -> readSingleQuoteString(head + c)
        }
    }

    /**
     * Is called to parse double quoted expressions
     */
    fun readDoubleQuoteString(head: String = "", c: Char = this.nextChar()): String {
        return when (c) {
            // TODO: экранирование
            '\"' -> {
                if (head.isNotEmpty() && head.last() == '\\') {
                    readDoubleQuoteString(head + c)
                }
                head
            }
            '$' -> {
                val res = readVariable()
                if (res.length == 1) {
                    readDoubleQuoteString("${head}$", c = res.last())
                }
                readDoubleQuoteString(head + res.dropLast(1), c = res.last())
            }
            else -> readDoubleQuoteString(head + c)
        }
    }

    /**
     * Is called to parse variable, started with "$" character.
     * When the full name of a variable was read, calling "System.getenv"
     */
    fun readVariable(head: String = ""): String {
        val c = this.nextChar()
        return if (c.isLetter() || c == '_') {
            readVariable(head + c)
        } else if (head.isEmpty()) {
            c.toString()
        } else {
            // TODO: catch exceptions
            System.getenv(head) + c
        }
    }

    override fun close() {
        sc.close()
    }


    /**
     * helpers
     */

    private fun MutableList<MutableList<Token>>.removeLastEmptyToken() {
        if (this.last().isNotEmpty() && this.last().last() == "") {
            this.last().removeAt(this.last().size - 1)
        }
    }

    private fun MutableList<MutableList<Token>>.addNewCommand() {
        if (this.last().isEmpty()) {
            this.removeAt(this.size - 1)
        }
        this.add(mutableListOf(""))
    }

    private fun MutableList<MutableList<Token>>.addToken(token: Token) {
        this.removeLastEmptyToken()
        this.last().add(token)
    }

    private fun MutableList<MutableList<Token>>.appendToken(str: String) {
        this.last()[this.last().size - 1] += str
    }

}