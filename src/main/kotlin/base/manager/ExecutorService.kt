package base.manager

import base.logger.Log
import data.model.Executor
import view.MainFragment
import java.io.File
import java.io.InputStream
import java.io.OutputStream

public class ExecutorService() {
    var process: Process? = null
    var file: File? = null
    var executors: List<Executor> = ArrayList()
    val processManager: ProcessManager = ProcessManager.getInstance()
    var listener: RunningCommandListener? = null

    private fun createCacheFileIfNotExist(): File {
        val rootFolder = File(MainFragment.ROOT_FOLDER, "/cache/")
        if (!rootFolder.exists()) {
            rootFolder.mkdir()
        }
        val file = File(rootFolder, "cache${hashCode()}.bat")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    fun prepare() {
        if (executors.size == 1) {
            file = executors.first().file
        } else {
            file = createCacheFileIfNotExist()
            file?.bufferedWriter()?.use {
                for (item in executors) {
                    val text = item.file.readText()
                    it.write(text)
                    it.write("\n")
                }
            }
        }
    }

    suspend fun execute() {
        Log.d("Executed!")
        if(file != null) {
            process = processManager.newProcess(hashCode().toString(), file!!.absolutePath)
            Log.d("Executed: ${file?.absolutePath}")
            processManager.put(hashCode().toString(), process!!)
            listener?.onRunning(process!!.inputStream, process!!.errorStream, process!!.outputStream)
            close()
        }
    }

    fun close() {
        Log.d("Process: alive=" + process?.isAlive)
        processManager.removeAndKill(hashCode().toString())
        listener?.onClosed(process)
        if(file?.parentFile?.name.equals("cache")) {
            file?.delete()
        }
    }

    interface RunningCommandListener {
        suspend fun onRunning(inp: InputStream, err: InputStream, out: OutputStream)

        fun onClosed(process: Process?) {}
    }

}
