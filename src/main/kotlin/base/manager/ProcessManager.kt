package base.manager

import base.extenstion.getPid
import base.extenstion.kill
import data.model.Command
import java.io.File

class ProcessManager {

    companion object {
        @Volatile
        private var sInstance: ProcessManager? = null

        fun getInstance(): ProcessManager = (sInstance ?: synchronized(this) {
            if (sInstance == null) {
                sInstance = ProcessManager()
            }
            sInstance!!
        })
    }

    private val processMapper = HashMap<String, Process>()

    fun put(key: String, process: Process) {
        processMapper.put(key, process)
    }

    fun get(key: String): Process? {
        return processMapper.get(key)
    }

    fun remove(key: String) {
        processMapper.remove(key)
    }

    fun removeAndKill(key: String) {
        val process = processMapper.get(key)
        process?.kill()
        processMapper.remove(key)
    }

    fun newProcess(key: String, commands: List<String>): Process {
        return newProcess(key, *commands.toTypedArray())
    }

    fun newProcess(key: String, vararg commands: String): Process {
        val builder = ProcessBuilder(*commands)
            .directory(File(System.getProperty("user.home")))
        val process = builder.start()
        processMapper.put(key, process)
        return process
    }

    fun listProcessIds(): List<Long> {
        return processMapper.values.map { it.getPid() }
    }

    fun killAll() {
        try {
            processMapper.values.forEach { it.kill() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}