package data.model

import utils.readCmdLines
import java.io.File

data class CmdFile(
    val name: String,
    val path: String,
    val cmdLines: ArrayList<String> = arrayListOf()
) {
    fun load() {
        cmdLines += File(path).readCmdLines()
    }
}
