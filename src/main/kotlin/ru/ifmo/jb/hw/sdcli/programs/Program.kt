package ru.ifmo.jb.hw.sdcli.programs

import ru.ifmo.jb.hw.sdcli.Token
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.lang.Exception

/**
 * Basic class for executable programs.
 * They have to use input and output streams instead of System.in and System.out, because programs can be piped
 * @property input - to do piping.
 * @property output - to do piping.
 * @property args - arguments without the name of the program as a first arg
 */
abstract class Program {
    protected var input: InputStream = System.`in`
    protected var output: OutputStream = System.out
    var args = emptyList<Token>() // TODO: maybe args[0] should be the name of a program, as in actual bash

    fun execute() {
        try {
            executeImpl()
        } finally {
            if (output is PipedOutputStream) {
                output.close()
            }
            if (input is PipedInputStream) {
                input.close()
            }
        }
    }

    protected abstract fun executeImpl()

    fun linkTo(other: Program) {
        other.input = PipedInputStream()
        this.output = PipedOutputStream(other.input as PipedInputStream)
    }
}
