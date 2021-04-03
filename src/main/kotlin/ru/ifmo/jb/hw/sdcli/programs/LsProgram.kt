package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.PrintStream
import java.nio.file.Paths

class LsProgram : Program() {
    override fun executeImpl() {
        val currentDirAsFile = if (args.isEmpty()) Paths.get(System.getProperty("user.dir")).toAbsolutePath().toFile()
                    else Paths.get(args.first()).toFile()

        val ps = PrintStream(output)
        currentDirAsFile.walkTopDown().maxDepth(1)
            .filter { !it.isHidden && it != currentDirAsFile && !it.name.startsWith('.') }
            .sortedBy {
                it.isDirectory
            }
            .forEach {
                if (it.isDirectory) {
                    ps.println(it.name + File.separator)
                } else {
                    ps.println(it.name)
                }
             }
    }
}