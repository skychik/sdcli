package ru.ifmo.jb.hw.sdcli.programs

import java.io.PrintStream

/**
 * Exit analogue.
 */
class Exit : Program() {
    override fun execute() {
        PrintStream(output).print("exit")
        close()
    }
}
