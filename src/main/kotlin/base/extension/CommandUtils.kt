package base.extension

import java.io.File
import java.nio.charset.Charset


fun executeWithBat(cmdStr: String) {
    val file = File("execute.bat")

    if(!file.exists()) {
        file.createNewFile()
    }
    file.bufferedWriter(Charsets.UTF_8)
        .append(cmdStr)
        .apply { flush() }
        .close()
    val cmds = arrayOf("cmd", "/K", "start ${file.path}")
    Runtime.getRuntime().exec(cmds)
}



