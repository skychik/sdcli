package ru.ifmo.jb.hw.sdcli

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import ru.ifmo.jb.hw.sdcli.programs.*
import java.io.File
import java.io.PrintStream

import java.io.ByteArrayOutputStream



// many fails 
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramTests {
    var files = mutableMapOf<String, File>()
    val resourcesPath = "src/test/resources/"
    val smallFileName = "${resourcesPath}test_data_created.txt"
    val bigFileName = "${resourcesPath}test_data.txt"

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
    fun wcWithArgsTestSingleFile() {
        val wc = WcProgram()
        wc.args = listOf(bigFileName)
        wc.execute()
        assertEquals("     185     781    5766 $bigFileName", outContent.toString())
    }

    @Test
    fun wcWithArgsTestMultipleFiles() {
        val wc = WcProgram()
        val wcArgs = files.keys.toMutableList()
        val notExistingFileName = "${resourcesPath}test_data_cccdfdsfsfsdfsf"
        wcArgs.add(notExistingFileName)
        wc.args = wcArgs.sorted()
        wc.execute()
        val expected =
            "     185     781    5766 $bigFileName\n" +
            "wc: $notExistingFileName: open: No such file or directory\n" +
            "       1       1      26 $smallFileName\n" +
            "     186     782    5792 total"
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun grepTestNoArgs() {
        val grep = GrepProgram()
        grep.execute()
        assertEquals("usage: grep [-iw] [-A num] [pattern] [file ...]", outContent.toString())
    }

    @Test
    fun grepTestWithArgsAndFileNames() {
        val argsAndExpected = listOf(
            Pair(listOf("RG", bigFileName), "grep_simple_expected.txt"),
            Pair(listOf("-A2", "if", bigFileName), "grep_A2_expected.txt"),
            Pair(listOf("-A223", "-A2", "if", bigFileName), "grep_A2_expected.txt"),
            Pair(listOf("-A", "2", "if", bigFileName), "grep_A2_expected.txt"),
            Pair(listOf("--after-context=2", "if", bigFileName), "grep_A2_expected.txt"),
            Pair(listOf("-A2", "-w", "if", bigFileName), "grep_A2_w_expected.txt"),
            Pair(listOf("-w", "-A2", "if", bigFileName), "grep_A2_w_expected.txt"),
            Pair(listOf("--word-regexp", "-A2", "if", bigFileName), "grep_A2_w_expected.txt"),
            Pair(listOf( "-i", "-A1", "RG", bigFileName), "grep_A1_i_expected.txt"),
            Pair(listOf("-A1", "-i", "RG", bigFileName), "grep_A1_i_expected.txt"),
            Pair(listOf("-A1", "--ignore-case", "RG", bigFileName), "grep_A1_i_expected.txt"),
            Pair(listOf("-A1", "--ignore-case", "-i", "RG", bigFileName), "grep_A1_i_expected.txt"),
        )

        for (pair in argsAndExpected) {
            val grep = GrepProgram()
            grep.args = pair.first
            grep.execute()
            assertEquals(File("${resourcesPath}${pair.second}").readText(), outContent.toString(),
                "Failed on args: ${pair.first}")
            outContent.reset()
        }
    }
}