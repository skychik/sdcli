package ru.ifmo.jb.hw.sdcli.programs

import java.io.File
import java.io.PrintStream
import java.nio.file.InvalidPathException
import java.nio.file.Paths

class CdProgram : Program() {

    companion object Dir {
        private const val pwdProperty = "user.dir"
        private val sysSeparator = File.separator
        private val userHomeFile = Paths.get(System.getProperty("user.home")).toAbsolutePath().toFile()

        fun getCurrentDir():String {
            val currDir = Paths.get(System.getProperty(pwdProperty)).toAbsolutePath().toFile()
            if (currDir == userHomeFile) {
                return ""
            }
            return currDir.canonicalPath
        }
    }

    override fun executeImpl() {
        if (args.isNotEmpty() && args.first() == "..") {
            val currDir = Paths.get(System.getProperty(pwdProperty)).toAbsolutePath().toFile()
            System.setProperty(pwdProperty, currDir.parent)
            return
        }

        try {
            if (args.isEmpty()) { // can set to as is
                System.setProperty(pwdProperty, userHomeFile.absolutePath)
                return
            }
            var parsedArgs = Paths.get(args.first()).toFile()

            if (args.first().startsWith(sysSeparator)) {
                val diskRoot = parsedArgs.walkBottomUp().last()
                parsedArgs = Paths.get(diskRoot.absolutePath + sysSeparator + args.first().drop(1)).toFile()
            }

            if (!parsedArgs.exists()) {
                parsedArgs = Paths.get(System.getProperty(pwdProperty) + sysSeparator + args.first()).toFile()
            }

            if (parsedArgs.exists() && parsedArgs.isDirectory) {
                val pwd = Paths.get(System.getProperty(pwdProperty)).toAbsolutePath().toFile()
                var argumentDiskRoot = parsedArgs
                var newParent = parsedArgs.parentFile
                while (argumentDiskRoot != newParent && newParent != null) {
                    argumentDiskRoot = newParent
                    newParent = newParent.parentFile
                }

                var pwdDiskRoot = pwd
                newParent = pwd.parentFile
                while (pwdDiskRoot != newParent && newParent != null) {
                    pwdDiskRoot = newParent
                    newParent = newParent.parentFile
                }

                System.setProperty(pwdProperty, parsedArgs.absolutePath)
            } else {
                throw InvalidPathException(args.first(), "Invalid location provided")
            }

        } catch (ex: Exception) {
            PrintStream(output).print("cd: " + ex.message)
        }

    }
}