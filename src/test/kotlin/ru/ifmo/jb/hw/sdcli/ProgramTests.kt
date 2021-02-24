package ru.ifmo.jb.hw.sdcli

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import ru.ifmo.jb.hw.sdcli.programs.CatProgram
import ru.ifmo.jb.hw.sdcli.programs.EchoProgram
import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

import java.io.ByteArrayOutputStream




@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramTests {
    lateinit var input: PipedInputStream
    lateinit var output: PipedOutputStream
    lateinit var file: File

    val data = "sdfsdfhgsarweyhdgf54tgf"
    val fileName = "src/test/resources/data.txt"
    val args = listOf("dd", "dfdf  \"f   \$USER ", "dfdf  f ")

    val outContent = ByteArrayOutputStream()
    val originalOut = System.out

    @BeforeAll
    fun init() {
        file = File(fileName)
        file.createNewFile()
        file.writeText(data)
        System.setOut(PrintStream(outContent))
    }

    @AfterAll
    fun deleteFile() {
        file.delete()
        System.setOut(originalOut)
    }

    @BeforeEach
    fun initStreams() {
        output = PipedOutputStream()
        input = PipedInputStream(output)
    }

    @AfterEach
    fun closeStreams() {
        output.close()
        input.close()
    }

    @Test
    fun catWithArgsTest() {
        val cat = CatProgram()
        cat.args = listOf(fileName)
        cat.execute()
        assertEquals(file.readText(), outContent.toString())
    }

    @Test
    fun catWithoutArgsTest() {
        val echo = EchoProgram()
        echo.args = args
        val cat = CatProgram()
        echo.linkTo(cat)
        echo.execute()
        cat.execute()
        val res1 = outContent.toString()

        outContent.reset()
        val echo2 = EchoProgram()
        echo2.args = args
        echo2.execute()
        val res2 = outContent.toString()
        assertEquals(res1, res2)
    }

}