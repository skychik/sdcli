package ru.ifmo.jb.hw.sdcli.programs

import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

/**
 * Echo analogue
 */
class Echo : Program() {
    override fun execute() {
        val ps = PrintStream(output)
        for (arg in args) {
            ps.print("$arg ")
        }
        close()
    }
}
