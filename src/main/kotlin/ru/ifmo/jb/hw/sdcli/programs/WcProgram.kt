package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintStream


/**
 * Wc analogue
 */
class WcProgram : Program() {
    override fun execute() {
        if (args.isEmpty()) {
            val bytes = input.readAllBytes()
            PrintStream(output).print(fromStrToAnswer(bytes.toString(), bytes))
            close()
        } else {
            try {
                val inputStream = File(args[0]).inputStream()
                val str = inputStream.bufferedReader().use { it.readText() }
                val bytes = File(args[0]).readBytes()
                print(fromStrToAnswer(str, bytes) + args[0])
            } catch (e: FileNotFoundException) {
                print("cat: ${args[0]}: open: No such file or directory")
            } finally {
                close()
            }
        }
    }

    private fun fromStrToAnswer(str: String, bytes: ByteArray): String {
        val lines = (str.split("\n").size - 1)
        val words = (str.replace("\n", " ").split("\\s+".toRegex()).size - 1)

        val format = {num: Int -> "%7d".format(num)}
        return " ${format(lines)} ${format(words)} ${format(bytes.size)}"
    }
}
