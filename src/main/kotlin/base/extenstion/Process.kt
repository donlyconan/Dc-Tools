package base.extenstion

import java.io.File
import java.lang.management.ManagementFactory


fun Process.getPid(): Long {
    val bean = ManagementFactory.getRuntimeMXBean()
    val jvmName = bean.name
    return java.lang.Long.valueOf(jvmName.split("@").toTypedArray().first())
}

fun Process.kill(): Process? {
    val pid = getPid()
    destroy()
    if (isAlive) {
        destroyForcibly()
        if (isAlive) {
            val builder = ProcessBuilder("cmd", "/c", "taskkill /F /PID $pid")
                .directory(File(System.getProperty("user.home")))
            return builder.start()
        }
    }
    return null
}