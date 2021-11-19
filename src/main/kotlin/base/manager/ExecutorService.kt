package base.manager

import data.model.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sun.plugin.com.Dispatcher
import view.MainFragment
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class ExecutorService(val scope: CoroutineScope, var executors: List<Executor>) {
    var processLifecycle: ProcessLifecycle? = null
    lateinit var process: Process
    lateinit var file: File

    init {
        processLifecycle?.onCreate()
    }

    private fun createCacheFileIfNotExist(): File {
        val rootFolder = File(MainFragment.ROOT_FOLDER, "/cache/")
        if (!rootFolder.exists()) {
            rootFolder.mkdir()
        }
        val file = File(rootFolder, "cache.bat")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    fun prepare() {
        file = createCacheFileIfNotExist()
        file.bufferedWriter().use {
            for (item in executors) {
                val text = item.file.readText()
                it.write(text)
                it.write("\n")
                if (item.delay > 0) {
                    it.write("timeout ${item.delay}\n")
                }
            }
        }

    }

    fun execute() {
        process = ProcessBuilder("cmd", "/c", "start ${file.absolutePath}")
            .start()
        processLifecycle?.onStart(process)
        processLifecycle?.onOutputStream(process.outputStream)
        processLifecycle?.onInputStream(process.inputStream)
        processLifecycle?.onError(process.errorStream)
    }
//    .apply {
//        invokeOnCompletion { processLifecycle?.onDestroy() }
//    }
}

fun main() {
    val service = ExecutorService(
        CoroutineScope(Dispatchers.IO),
        arrayListOf(
            Executor(File("C:\\Users\\Admin\\Documents\\tools\\C1.bat")),
            Executor(File("C:\\Users\\Admin\\Documents\\tools\\C2.bat"))
        )
    )
    service.processLifecycle = object : ProcessLifecycle {
        override fun onInputStream(stream: InputStream) {
            var c = stream.read()
            while (c != -1) {
                print(c)
                c = stream.read()
            }
        }
    }
    service.prepare()
    service.execute()
    var stream = service.process.inputStream.bufferedReader()
    var c = stream.readLine()
    while (c != null) {
        println(c)
        c = stream.readLine()
    }
}