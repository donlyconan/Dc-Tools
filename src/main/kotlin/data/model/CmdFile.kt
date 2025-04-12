package data.model

import utils.readCmdLines
import java.io.File

data class CmdFile(
    val name: String,
    val path: String,
    var cmdLines: ArrayList<String> = arrayListOf()
) {
    fun load() {
        cmdLines.clear()
        cmdLines.addAll(File(path).readCmdLines())
    }
}
