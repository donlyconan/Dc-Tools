package data.model

import R.drawable.folder
import base.extenstion.kill
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException


interface ExecutorLifeCycle {
    fun onStart(process: Process)
    fun onDestroy(process: Process?)
}

interface ExecutorRuntime {
    fun onError(exitCode: Int, msg: String?)
    fun onSuccess(exitCode: Int)
}

class Executor(val commands: List<Command>) {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private lateinit var builder: ProcessBuilder
    private var executorRuntime: ExecutorRuntime? = null
    private var executorLifeCycle: ExecutorLifeCycle? = null
    var process: Process? = null
        private set

    init {
//        builder.command(file.absolutePath)
//            .directory(File(System.getProperty("user.name")))
    }

    fun start() = scope.launch {
        try {
            process = builder.start()
            executorLifeCycle?.onStart(process!!)
        } catch (e: IOException) {
            executorRuntime?.onError(-1, e.message)
        }
    }

    fun destroy() {
        job.cancelChildren()
        process?.kill()
        if (process?.exitValue() == 0) {
            executorRuntime?.onSuccess(process!!.exitValue())
        } else if (process != null) {
            executorRuntime?.onError(process!!.exitValue(), "The process failure completed!")
        }
        executorLifeCycle?.onDestroy(process)
    }
}