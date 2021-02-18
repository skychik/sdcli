@file:JvmName("Main")
package ru.ifmo.jb.hw.sdcli

import ru.ifmo.jb.hw.sdcli.programs.*

class ConsoleInput {
    private var tokens: List<Token> = emptyList()

    fun getNextToken() : Token {
        while (tokens.isEmpty()) {
            val rdln = readLine()
            tokens = rdln!!.split(' ')
        }
        val retToken = tokens.first()
        tokens = tokens.subList(1, tokens.size)
        return retToken
    }

    fun clearInput() {
        tokens = emptyList()
    }
}

fun main() {
    while (true) {
        print("boi$ ")
        var tokens: List<Token> = emptyList()
        while (tokens.isEmpty()) {
            tokens = readLine()!!.split("\\s+".toRegex())
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
                break
            }
            else -> {
                println("${tokens.first()}: command not found")
            }
        }
    }
}