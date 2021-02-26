package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintStream


/**
 * Wc analogue
 */
class WcProgram : Program() {
    override fun executeImpl() {
        if (args.isEmpty()) {
            val bytes = input.readAllBytes()
            PrintStream(output).print(format(fromDataToCounts(bytes.toString(), bytes)))
            return
        }
        var total = Triple(0, 0, 0)
        val ps = PrintStream(output)
        var didPrint = false
        for (fileName in args) {
            try {
                val inputStream = File(fileName).inputStream()
                val str = inputStream.bufferedReader().use { it.readText() }
                val bytes = File(fileName).readBytes()
                val res = fromDataToCounts(str, bytes)
                total = Triple(total.first + res.first, total.second + res.second, total.third + res.third)
                if (didPrint) ps.println() else didPrint = true
                ps.print(format(res) + " $fileName")
            } catch (e: FileNotFoundException) {
                if (didPrint) ps.println() else didPrint = true
                ps.print("wc: $fileName: open: No such file or directory")
            }
        }
        if (args.size > 1) {
            ps.println()
            ps.print(format(total) + " total")
        }
    }

    private fun fromDataToCounts(str: String, bytes: ByteArray): Triple<Int, Int, Int> {
        val lines = (str.split("\n").size - 1)
        val words = (str.replace("\n", " ").split("\\s+".toRegex()).size - 1)
        return Triple(lines, words, bytes.size)
    }

    private fun format(triple: Triple<Int, Int, Int>): String {
        val format = {num: Int -> "%7d".format(num)}
        return " ${format(triple.first)} ${format(triple.second)} ${format(triple.third)}"
    }
}
