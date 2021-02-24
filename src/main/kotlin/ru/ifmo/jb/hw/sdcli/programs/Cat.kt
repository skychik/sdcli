package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Cat analogue
 */
class Cat : Program() {

    override fun execute() {
        if (args.isEmpty()) {
            output.write(input.readAllBytes())
        } else try {
            val inputStream: InputStream = File(args[0]).inputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }
            print(inputString)
        } catch (e: FileNotFoundException) {
            print("cat: ${args[0]}: No such file or directory")
        }
        close()
    }
}
