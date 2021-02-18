package ru.ifmo.jb.hw.sdcli.programs

class Echo(private val args: List<Token>) : Program {
    override fun execute() {
        for (arg in args) {
            print("$arg ")
        }
        println()
    }
}