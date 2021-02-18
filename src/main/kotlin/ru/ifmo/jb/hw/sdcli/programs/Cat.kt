package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class Cat(private val filePath: Token) : Program {
    override fun execute() {
        try {
            val inputStream: InputStream = File(filePath).inputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }
            println(inputString)
        } catch (e: FileNotFoundException) {
            println("cat: $filePath: No such file or directory")
        }
    }
}