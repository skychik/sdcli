package ru.ifmo.jb.hw.sdcli.programs

import java.io.PrintStream
/**
 * Pwd analogue
 */
class Pwd : Program() {
    override fun execute() {
        PrintStream(output).print(System.getProperty("user.dir"))
        close()
    }
}
