package base.extenstion

import base.logger.Log
import base.manager.ProcessManager
import java.io.File
import java.lang.management.ManagementFactory


fun Process.getPid(): Long {
    val bean = ManagementFactory.getRuntimeMXBean()
    val jvmName = bean.name
    val processId = jvmName?.split("@")?.toTypedArray()?.first() ?: "0"
    return java.lang.Long.valueOf(processId)
}

fun Process.kill(): Process? {
    destroy()
    if (isAlive) {
        destroyForcibly()
        if (isAlive) {
            val pid = getPid()
            val builder = ProcessBuilder("cmd", "/c", "taskkill /F /PID $pid")
                .directory(File(System.getProperty("user.home")))
            return builder.start()
        }
    }
    return null
}

fun main() {
////    val process = ProcessBuilder(
////        arrayListOf("cmd", "/c").apply {
////            addAll(
////                File("C:\\Users\\Admin\\Documents\\tools\\C2.bat").readLines().toMutableList()
////            )
////        }
////    )
////        .start()
    val process = ProcessManager.getInstance().newProcess("C2", "C:\\Users\\Admin\\Documents\\tools\\C2.bat")
//    process.kill()
    val reader = process.inputStream.bufferedReader()
    var line = reader.readLine()
    while (line != null ){
        println(line)
        line = reader.readLine()
    }

}