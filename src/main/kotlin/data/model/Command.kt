package data.model

import java.io.File
import java.lang.StringBuilder


class Command(val file: File): Comparable<Command> {
    companion object {
        const val EXT_BAT = ".bat"
        const val EXT_CMD = ".cmd"
    }
    var name = file.nameWithoutExtension
    var ext = file.extension
    var content: String = ""

    constructor(path: String): this(File(path))

    constructor(command: Command) : this(command.file)

    fun canExecute() = ext == EXT_BAT || ext == EXT_CMD

    fun getModified() = file.lastModified()

    fun readFileContent(): String {
        val builder = StringBuilder()
        val bufferedReader = file.bufferedReader()
        var line: String? = bufferedReader.readLine()
        while (line != null) {
            builder.append(line).append('\n')
            line = bufferedReader.readLine()
        }
        content = builder.toString()
        return content
    }

    override fun compareTo(other: Command): Int {
        if (!other.canExecute()) {
            return Int.MIN_VALUE
        } else if(!canExecute()) {
            return Int.MAX_VALUE
        } else {
            return (other.getModified() - getModified()) as Int
        }
    }

    override fun equals(other: Any?): Boolean {
        val item = other as? Command
        return name == item?.name
    }
}