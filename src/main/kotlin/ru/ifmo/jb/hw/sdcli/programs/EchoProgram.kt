package ru.ifmo.jb.hw.sdcli.programs

import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

/**
 * Echo analogue
 */
class EchoProgram : Program() {
    override fun executeImpl() {
        val ps = PrintStream(output)
        var didPrint = false
        for (arg in args) {
            if (didPrint) ps.print(" ") else didPrint = true
            ps.print(arg)
        }
    }
}
