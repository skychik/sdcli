package ru.ifmo.jb.hw.sdcli.programs

import java.io.PrintStream

/**
 * Exit analogue.
 */
class ExitProgram : Program() {
    override fun executeImpl() {
        PrintStream(output).print("exit")
    }
}
