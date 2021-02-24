package ru.ifmo.jb.hw.sdcli.programs

import java.io.IOException
import java.io.PrintStream

/**
 * Any other real bash program is called via this class
 */
class OuterProgram : Program() {
    override fun execute() {
        try {
            val pb = ProcessBuilder()
            pb.inheritIO()
            pb.command(args)
            pb.redirectErrorStream(true)
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT)
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT)
            val process: Process = pb.start()
            process.waitFor()
        } catch (e: IOException) {
            PrintStream(output).print("bush: ${args[0]}: command not found")
        } finally {
            close()
        }
    }
}
