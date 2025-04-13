package utils

import java.io.File
import java.io.FileOutputStream

val USER_HOME: File by lazy { File(System.getProperty("user.home")) }

const val COMMAND_EXT = ".bat"

val HOME_FOLDER: File by lazy {
    val home = File(USER_HOME, "dc-tools")
    if(!home.exists()) {
        home.mkdir()
    }
    home
}

fun File.pushLines(lines: List<String>) {
    if(!exists()) {
        createNewFile()
    }
    FileOutputStream(this, false).use { out ->
        lines.forEach { line ->
            out.write("$line\n".toByteArray(Charsets.UTF_8))
        }
        out.flush()
        out.close()
    }
}

fun File.readCmdLines(): List<String> = readLines(Charsets.UTF_8).map { it.trim() }
    .filter { it.isNotEmpty() && it.isNotBlank()  }


fun String.dotBat() = this + COMMAND_EXT