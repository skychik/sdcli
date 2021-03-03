package ru.ifmo.jb.hw.sdcli

import java.io.InputStream
import java.lang.StringBuilder
import java.util.Scanner
import kotlin.collections.ArrayDeque

/**
 * Used instead of Scanner, because we can't simply read until the end of line,
 * i.e. "echo '\n' f" shouldn't stop reading after \n
 * Has parsing methods to separate commands and their arguments while piping.
 * Works fine with single quote and double quote separation, and also with inlining of variables
 */
// TODO: maybe need to use StringBuilder
class BushScanner(inputStream: InputStream) {
    private var sc = Scanner(inputStream) // TODO: should close it?
    private var line: MutableList<Char> = ArrayDeque()
    private val vars: MutableMap<String, String> = mutableMapOf()

    /**
     * BushScanner takes char by char with this method
     */
    private fun nextChar(): Char {
        if (line.isEmpty()) {
            line.addAll(sc.nextLine().asSequence())
            line.add('\n')
        }
        val c = line.first()
        line.removeFirst()
        return c
    }

    /**
     * The only public method. Returns List of tokens of piped commands.
     * @return all tokens without variables
     */
    fun readCommands(): List<List<Token>> {
        val commands: MutableList<MutableList<Token>> = readTokens()
        val vars = mutableListOf<Pair<String, String>>()
        var isOnlyVariables = commands.size == 1
        for (tokens in commands) {
            for (token in tokens) {
                val variable = parseVarAssignment(token)
                if (variable == null) {
                    isOnlyVariables = false
                    break
                }
                tokens.removeFirst()
                if (isOnlyVariables) {
                    vars.add(variable)
                }
            }
        }
        if (isOnlyVariables) {
            for (p in vars) {
                this.vars[p.first] = p.second
            }
        }
        return commands
    }

    private fun parseVarAssignment(token: Token): Pair<String, String>? {
        if (token[0] == '=' || !token.contains('=')) {
            return null
        }
        val sb = StringBuilder()
        var name = ""
        for (c in token) {
            if (c == '=') {
                name = sb.toString()
                sb.clear()
                continue
            } else {
                sb.append(c)
            }
        }
        return Pair(name, sb.toString())
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
    private fun readTokens(
        cmnds: MutableList<MutableList<Token>> = mutableListOf(mutableListOf("")),
        c: Char = this.nextChar()
    ): MutableList<MutableList<Token>> {
        return when (c) {
            '\n' -> {
                cmnds.removeLastEmptyToken()
                cmnds
            }
            '\'' -> {
                cmnds.addToken(readSingleQuoteString())
                readTokens(cmnds)
            }
            '\"' -> {
                cmnds.addToken(readDoubleQuoteString())
                readTokens(cmnds)
            }
            '|' -> {
                // TODO: обрабатывать подряд идущие |
                cmnds.removeLastEmptyToken()
                cmnds.addNewCommand()
                readTokens(cmnds)
            }
            '$' -> {
                val variable = readVariable()
                
                // was it a variable?
                if (variable.length > 1) {
                    cmnds.addToken(variable.dropLast(1))
                } else {
                    cmnds.appendToken("$")
                }
                readTokens(cmnds, c = variable.last())
            }
            ' ' -> {
                cmnds.removeLastEmptyToken()
                cmnds.addToken("")
                readTokens(cmnds)
            }
            else -> {
                // TODO: maybe use Scanner.useDelimiter()
                cmnds.appendToken("$c")
                readTokens(cmnds)
            }
        }
    }

    /**
     * Is called to parse single quoted expressions
     */
    private fun readSingleQuoteString(head: String = "", c: Char = this.nextChar()): String {
        return when (c) {
            '\'' -> head
            else -> readSingleQuoteString(head + c)
        }
    }

    /**
     * Is called to parse double quoted expressions
     */
    private fun readDoubleQuoteString(head: String = "", c: Char = this.nextChar()): String {
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
    private fun readVariable(head: String = ""): String {
        val c = this.nextChar()
        return if (c.isLetter() || c == '_') {
            readVariable(head + c)
        } else if (head.isEmpty()) {
            c.toString()
        } else {
            if (vars.containsKey(head)) {
                vars[head] + c
            } else {
                // TODO: catch exceptions
                System.getenv(head) ?: "" + c
            }
        }
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
