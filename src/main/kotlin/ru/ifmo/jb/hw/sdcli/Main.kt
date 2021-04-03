@file:JvmName("Main")
package ru.ifmo.jb.hw.sdcli

import ru.ifmo.jb.hw.sdcli.programs.*


/**
 * Starting point of a program
 */
fun main() {
    val sc = BushScanner(System.`in`)

    while (true) {
        print("bush$ " + CdProgram.getCurrentDir() + ":") // kek

        // reading the command/pipe of commands
        var commands: List<List<Token>>
        try {
            commands = sc.readCommands()
        } catch (e: NoSuchElementException) {
            break
        }

        // Actual list of Programs to execute
        val progs: List<Program> = commands.map { c -> listToProgram(c) }

        // checks if we have to do piping
        if (progs.size > 1) {
            // Can't stop the while loop inside execute() method of a Program, so have to do this
            // Real bash doesn't exit if exit is not the last program in pipe. I guess this is not that important
            if (progs.filterIsInstance<ExitProgram>().isNotEmpty()) {
                break
            }
            // piping
            for (i in 1 until progs.size) {
                progs[i-1].linkTo(progs[i])
            }
            // execute consistently
            for (p in progs) {
                p.execute()
            }
            if (progs.filterIsInstance<NoneProgram>().size != progs.size) {
                println()
            }
        } else {
            progs[0].execute()
            if (progs[0] is ExitProgram) {
                break
            }
            if (progs[0] !is NoneProgram) {
                println()
            }
        }
    }
}

/**
 * When we split the input into strings of piped programs, they have to be turned into Programs via this method
 */
fun listToProgram(tokens: List<Token>): Program {
    if (tokens.isEmpty()) {
        return NoneProgram()
    }
    val prog: Program = when (tokens.first()) {
        "cat" -> CatProgram()
        "pwd" -> PwdProgram()
        "echo" -> EchoProgram()
        "wc" -> WcProgram()
        "exit" -> ExitProgram()
        "grep" -> GrepProgram()
        "ls" -> LsProgram()
        "cd" -> CdProgram()
        else -> OuterProgram()
    }
    if (prog is OuterProgram) {
        prog.args = tokens
    } else {
        prog.args = tokens.drop(1)
    }
    return prog
}