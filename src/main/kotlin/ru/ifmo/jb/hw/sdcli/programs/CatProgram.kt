package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner

/**
 * Cat analogue
 */
class CatProgram : Program() {
    override fun executeImpl() {
        if (args.isEmpty()) {
            // TODO: breaks the System.in
            val printer = PrintStream(output)
            val sc = Scanner(input)
            while (sc.hasNext()) {
                printer.println(sc.nextLine())
            }
            return
        }
        try {
            val inputStream: InputStream = File(args[0]).inputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }
            print(inputString)
        } catch (e: FileNotFoundException) {
            print("cat: ${args[0]}: No such file or directory")
        }
    }
}
