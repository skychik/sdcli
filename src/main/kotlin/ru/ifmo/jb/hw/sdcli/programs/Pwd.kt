package ru.ifmo.jb.hw.sdcli.programs

class Pwd : Program {
    override fun execute() {
        println(System.getProperty("user.dir"))
    }
}