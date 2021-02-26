package ru.ifmo.jb.hw.sdcli

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

class BushScannerTests {
    lateinit var input: PipedInputStream
    lateinit var sc: BushScanner
    lateinit var console: PrintStream
    val env = System.getenv("USER")
    val cmnd = "echo   dd  'dfdf  \"f   \$USER ' \"dfdf  f   \$USER \""
    val cmndResult = listOf("echo", "dd", "dfdf  \"f   \$USER ", "dfdf  f   $env ")

    @BeforeEach
    fun init() {
        input = PipedInputStream()
        sc = BushScanner(input)
        console = PrintStream(PipedOutputStream(input))
    }

    @Test
    fun testSingleCommand() {
        for (i in 1..3) {
            console.println(cmnd)
            assertEquals(listOf(cmndResult), sc.readCommands())
        }
    }

    @Test
    fun testPiping() {
        console.println("$cmnd|cat")
        assertEquals(listOf(cmndResult, listOf("cat")), sc.readCommands())
        console.println("$cmnd |cat")
        assertEquals(listOf(cmndResult, listOf("cat")), sc.readCommands())
        console.println("$cmnd| cat")
        assertEquals(listOf(cmndResult, listOf("cat")), sc.readCommands())
        console.println("$cmnd | cat")
        assertEquals(listOf(cmndResult, listOf("cat")), sc.readCommands())

        console.println("$cmnd|$cmnd")
        assertEquals(listOf(cmndResult, cmndResult), sc.readCommands())
    }
}