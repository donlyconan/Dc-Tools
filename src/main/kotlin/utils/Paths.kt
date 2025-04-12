package utils

import java.io.File

val USER_HOME: File = File(System.getProperty("user.home"))

const val COMMAND_EXT = ".bat"

fun getHome(): File {
    val home = File(USER_HOME, "dc-tools")
    if(!home.exists()) {
        home.mkdir()
    }
    return home
}

fun File.pushLines(lines: List<String>) {
    if(!exists()) {
        createNewFile()
    }
    outputStream().use { out ->
        lines.forEach { line ->
            out.write("$line\n".toByteArray(Charsets.UTF_8))
        }
        out.flush()
        out.close()
    }
}

fun File.readCmdLines() = readLines(Charsets.UTF_8).map { it.trim() }
    .filter { it.isNotEmpty() && it.isNotBlank()  }


fun String.dotBat() = this + COMMAND_EXT