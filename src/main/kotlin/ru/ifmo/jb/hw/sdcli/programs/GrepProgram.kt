package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.PrintStream
import java.lang.NumberFormatException
import java.util.*

/**
 * Grep analogue
 */
class GrepProgram : Program() {
    private var didPrintAnything = false

    override fun executeImpl() {
        // no args
        if (args.isEmpty()) {
            printUsage()
            return
        }

        // parse options
        var isCaseSensitive = true
        var isFindingWholeWord = false
        var linesCountAfterFounded = 0
        var nextArgsIndex = 0
        try {
            while (nextArgsIndex < args.size - 1) {
                val arg = args[nextArgsIndex]
                if (arg == "--") {
                    nextArgsIndex++
                    break
                }
                else if (arg.startsWith("-A")) linesCountAfterFounded = arg.drop(2).toInt()
                else if (arg.startsWith("--after-context=")) linesCountAfterFounded = arg.drop(16).toInt()
                else if (arg == "-i" || arg == "--ignore-case") isCaseSensitive = false
                else if (arg == "-w" || arg == "--word-regexp") isFindingWholeWord = true
                else break
                nextArgsIndex++
            }
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

            return
        }

        didPrintAnything = false

        // if one file name provided
        if (nextArgsIndex == args.size - 1) {
            grep(args[nextArgsIndex], pattern, isCaseSensitive, isFindingWholeWord, linesCountAfterFounded)
            return
        }

        // for each file name
        for (i in nextArgsIndex until args.size) {
            grep(args[i], pattern, isCaseSensitive, isFindingWholeWord, linesCountAfterFounded, addPrefix = true)
        }
    }

    private fun grep(fileName: String,
                     pattern: String,
                     isCaseSensitive: Boolean,
                     isFindingWholeWord: Boolean,
                     linesCountAfterFounded: Int,
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
                ps.print("$prefix:$line")
                var linesPrinted = 0
                while (linesPrinted < linesCountAfterFounded) {
                    if (!sc.hasNext()) break

                    linesPrinted++
                    if (didPrintAnything) ps.println()
                    line = sc.nextLine()
                    var sep = "-"
                    if (line.contains(regex)) {
                        sep = ":"
                        linesPrinted = 0
                    }
                    ps.print(prefix + sep + line)
                }
            }
        }
    }

    private fun printUsage() {
        PrintStream(output).print("usage: grep [-iw] [-A num] [pattern] [file ...]")
    }

}
