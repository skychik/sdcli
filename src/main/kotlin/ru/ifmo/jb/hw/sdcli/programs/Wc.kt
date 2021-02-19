package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException

class Wc(private val filePath: Token) : Program {
    override fun execute() {
        try {
            val inputStream = File(filePath).inputStream()
            val str = inputStream.bufferedReader().use { it.readText() }

            val lines = (str.split("\n").size - 1)//.toString()
            val words = (str.replace("\n", " ").split("\\s+".toRegex()).size - 1)//.toString()
            val bytes = File(filePath).readBytes().size//.toString()

            val format = {num: Int -> "%7d".format(num)}
            println(" ${format(lines)} ${format(words)} ${format(bytes)} $filePath")
        } catch (e: FileNotFoundException) {
            println("cat: $filePath: open: No such file or directory")
        }
    }
}
