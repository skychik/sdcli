package ru.ifmo.jb.hw.sdcli

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import ru.ifmo.jb.hw.sdcli.programs.*
import java.io.File
import java.io.PrintStream

import java.io.ByteArrayOutputStream




@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramTests {
    var files = mutableMapOf<String, File>()
    val smallFileName = "src/test/resources/test_data_created.txt"
    val bigFileName = "src/test/resources/test_data.txt"

    val data = "sdfsdfhgsarweyhdgf54tgf\ndd"
    val args = listOf("dd", "dfdf  \"f   \$USER ", "dfdf  f ")

    val outContent = ByteArrayOutputStream()
    val originalOut = System.out

    @BeforeAll
    fun init() {
        val file1 = File(smallFileName)
        file1.createNewFile()
        file1.writeText(data)
        files[smallFileName] = file1

        files[bigFileName] = File(bigFileName)

        System.setOut(PrintStream(outContent))
    }

    @AfterAll
    fun end() {
        System.setOut(originalOut)
    }

    @Test
    fun exitTest() {
        val exit = ExitProgram()
        exit.execute()
        assertEquals("exit", outContent.toString())
    }

    @Test
    fun echoTest() {
        val echo = EchoProgram()
        echo.args = args
        echo.execute()
        var expected = ""
        args.forEach {arg -> expected += " $arg" }
        if (expected.isNotEmpty()) expected = expected.drop(1)
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun catWithArgsTest() {
        for ((fileName, file) in files) {
            val cat = CatProgram()
            cat.args = listOf(fileName)
            cat.execute()
            assertEquals(file.readText(), outContent.toString())
        }
    }

//    @Test
    fun catWithoutArgsTest() {
        val cat = CatProgram()
        val echo = EchoProgram()
        echo.args = args
        echo.linkTo(cat)
        echo.execute()
        cat.execute()
        val expected1 = outContent.toString()

        outContent.reset()
        val echo2 = EchoProgram()
        echo2.args = args
        echo2.execute()
        val expected2 = outContent.toString()
        assertEquals(expected1, expected2)
    }

    @Test
    fun pwdTest() {
        val pwd = PwdProgram()
        pwd.execute()
        assertEquals(System.getProperty("user.dir"), outContent.toString())
    }

    @Test
    fun wcWithArgsTest() {
        // single file
        val wc = WcProgram()
        wc.args = listOf(bigFileName)
        wc.execute()
        assertEquals("     185     781    5766 $bigFileName", outContent.toString())

        // multiple files
        outContent.reset()
        val wc2 = WcProgram()
        val wcArgs = files.keys.toMutableList()
        val notExistingFileName = "src/test/resources/test_data_cccdfdsfsfsdfsf"
        wcArgs.add(notExistingFileName)
        wc2.args = wcArgs.sorted()
        wc2.execute()
        val expected =
            "     185     781    5766 $bigFileName\n" +
            "wc: $notExistingFileName: open: No such file or directory\n" +
            "       1       1      26 $smallFileName\n" +
            "     186     782    5792 total"
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun grepTest() {
        val grep = GrepProgram()
        grep.execute()
        assertEquals(System.getProperty("user.dir"), outContent.toString())
    }
}