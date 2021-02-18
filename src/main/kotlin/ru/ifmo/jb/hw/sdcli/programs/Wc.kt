package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException

class Wc(private val filePath: Token) : Program {
    override fun execute() {
        try {
            val inputStream = File(filePath).inputStream()
            val str = inputStream.bufferedReader().use { it.readText() }

            var lines = (str.split("\n").size - 1).toString()

            var words = (str
                .replace("\n", " ")
                .split("\\s+".toRegex()).size - 1).toString()

            var bytes = File(filePath).readBytes().size.toString()

            for (i in 1..(8 - lines.length)) {
                lines = " $lines"
            }
            for (i in 1..(8 - words.length)) {
                words = " $words"
            }
            for (i in 1..(8 - bytes.length)) {
                bytes = " $bytes"
            }

            println("$lines$words$bytes $filePath")
        } catch (e: FileNotFoundException) {
            println("cat: $filePath: open: No such file or directory")
        }
    }
}