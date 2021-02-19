@file:JvmName("Main")
package ru.ifmo.jb.hw.sdcli

import ru.ifmo.jb.hw.sdcli.programs.*

object Console {
    var line = ""

    fun nextChar(): Char {
        while (line.isEmpty()) {
            line = readLine() ?: fail()
            line += '\n'
        }
        val c = line.first()
        line = line.drop(1)
        return c
    }

    private fun fail(): Nothing {
        throw IllegalArgumentException()
    }
}

fun main() {
    while (true) {
        print("bush$ ")
        var tokens: List<Token>
        try {
            tokens = parse()
        } catch (e: IllegalArgumentException) {
            break
        }
        when (tokens.first()) {
            "cat" -> {
                if (tokens.size < 2) {
                    println("cat: file path required")
                } else {
                    Cat(tokens[1]).execute()
                }
            }
            "pwd" -> {
                Pwd().execute()
            }
            "echo" -> {
                Echo(tokens.drop(1)).execute()
            }
            "wc" -> {
                if (tokens.size < 2) {
                    println("wc: file path required")
                } else {
                    Wc(tokens[1]).execute()
                }
            }
            "exit" -> {
                Exit().execute()
                break
            }
            else -> {
                println("${tokens.first()}: command not found")
            }
        }
    }
}

// TODO: doesnt work properly with \n. Ex:
//  echo "
//  sds
//  "
fun parse(): List<Token> = parseStr().split("\\s+".toRegex())

fun parseStr(head: String = "", c: Char = Console.nextChar()): String {
    return when (c) {
        '\n' -> head
        '\'' -> parseStr(head + parseSingleQuote())
        '\"' -> parseStr(head + parseDoubleQuote())
        '$' -> {
            val res = parseVariable()
            if (res.length == 1) {
                return parseStr("$head$", c = res.last())
            }
            return parseStr(head + res.dropLast(1), c = res.last())
        }
        else -> parseStr(head + c)
    }
}

fun parseSingleQuote(head: String = "", c: Char = Console.nextChar()): String {
    return when (c) {
        '\'' -> head
        else -> parseSingleQuote(head + c)
    }
}

fun parseDoubleQuote(head: String = "", c: Char = Console.nextChar()): String {
    when (c) {
        '\"' -> {
            if (head.isNotEmpty() && head.last() == '\\') {
                return parseDoubleQuote(head + c)
            }
            return head
        }
        '$' -> {
            val res = parseVariable()
            if (res.length == 1) {
                return parseDoubleQuote("$head$", c = res.last())
            }
            return parseDoubleQuote(head + res.dropLast(1), c = res.last())
        }
        else -> return parseDoubleQuote(head + c)
    }
}

fun parseVariable(head: String = ""): String {
    val c = Console.nextChar()
    return if (c.isLetter() || c == '_') {
        parseVariable(head + c)
    } else if (head.isEmpty()) {
        c.toString()
    } else {
        System.getenv(head) + c
    }
}