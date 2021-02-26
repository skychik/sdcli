package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.PrintStream
import java.lang.NumberFormatException
import java.util.Scanner

/**
 * Grep analogue
 */
class GrepProgram : Program() {
    private var didPrintAnything = false
    private var isCaseSensitive = true
    private var isFindingWholeWord = false
    private var linesCountAfterFounded = 0
    private var nextArgsIndex = 0

    override fun executeImpl() {
        // no args
        if (args.isEmpty()) {
            printUsage()
            return
        }

        // parsing
        try {
            parseArgs()
        } catch (e: NumberFormatException) {
            PrintStream(output).print("grep: Invalid argument")
            return
        }

        // only options in args
        if (nextArgsIndex >= args.size) {
            printUsage()
            return
        }

        val pattern = args[nextArgsIndex]
        nextArgsIndex++

        // if no file name provided
        if (nextArgsIndex == args.size) {
            // not supported
            return
        }

        didPrintAnything = false

        // if one file name provided
        if (nextArgsIndex == args.size - 1) {
            grep(args[nextArgsIndex], pattern)
            return
        }
        // for each file name
        for (i in nextArgsIndex until args.size) {
            grep(args[i], pattern, addPrefix = true)
        }
    }

    private fun parseArgs() {
        while (nextArgsIndex < args.size - 1) {
            val arg = args[nextArgsIndex]
            if (arg == "--") {
                nextArgsIndex++
                break
            }
            else if (arg == "-A") {
                nextArgsIndex++
                linesCountAfterFounded = args[nextArgsIndex].toInt()
            }
            else if (arg.startsWith("-A"))
                linesCountAfterFounded = arg.drop("-A".length).toInt()
            else if (arg.startsWith("--after-context="))
                linesCountAfterFounded = arg.drop("--after-context=".length).toInt()
            else if (arg == "-i" || arg == "--ignore-case") isCaseSensitive = false
            else if (arg == "-w" || arg == "--word-regexp") isFindingWholeWord = true
            else break
            nextArgsIndex++
        }
    }

    private fun grep(fileName: String,
                     pattern: String,
                     addPrefix: Boolean = false) {
        val prefix = if (addPrefix) fileName else ""
        val regexString = if (isFindingWholeWord) "\\b${pattern}\\b" else pattern
        val regex = if (!isCaseSensitive) regexString.toRegex(RegexOption.IGNORE_CASE) else regexString.toRegex()

        val inputStream = File(fileName).inputStream()
        val sc = Scanner(inputStream)
        val ps = PrintStream(output)
        while (sc.hasNext()) {
            var line = sc.nextLine()
            if (line.contains(regex)) {
                if (didPrintAnything && linesCountAfterFounded > 0) {
                    ps.println()
                    ps.print("--")
                }
                if (didPrintAnything) ps.println() else didPrintAnything = true
                var sep = ""
                if (addPrefix) {
                    sep = ":"
                }
                ps.print("$prefix$sep$line")
                var linesPrinted = 0
                while (linesPrinted < linesCountAfterFounded) {
                    if (!sc.hasNext()) break

                    linesPrinted++
                    if (didPrintAnything) ps.println()
                    line = sc.nextLine()
                    if (line.contains(regex)) linesPrinted = 0
                    sep = if (addPrefix) {
                        if (line.contains(regex)) ":" else "-"
                    } else ""
                    ps.print(prefix + sep + line)
                }
            }
        }
    }

    private fun printUsage() {
        PrintStream(output).print("usage: grep [-iw] [-A num] [pattern] [file ...]")
    }

}
