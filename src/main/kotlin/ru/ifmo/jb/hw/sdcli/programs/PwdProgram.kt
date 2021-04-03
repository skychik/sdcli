package ru.ifmo.jb.hw.sdcli.programs

import java.io.PrintStream
/**
 * Pwd analogue
 */
class PwdProgram : Program() {
    override fun executeImpl() {
        PrintStream(output).print(System.getProperty("user.dir"))
    }
}
