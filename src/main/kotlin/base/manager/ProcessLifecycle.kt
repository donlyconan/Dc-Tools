package base.manager

import java.io.InputStream
import java.io.OutputStream

interface ProcessLifecycle {

    fun onCreate() {}

    fun onStart(process: Process) {}

    fun onOutputStream(stream: OutputStream) {}

    fun onInputStream(stream: InputStream) {}

    fun onError(stream: InputStream) {}

    fun onDestroy() {}

}