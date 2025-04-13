package base.extenstion

import base.logger.Log
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
