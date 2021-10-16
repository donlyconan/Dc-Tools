package base.manager
import base.extenstion.getPid
import base.extenstion.kill

class ProcessManager {

    companion object {
        @Volatile
        private var sInstance: ProcessManager? = null

        fun getInstance(): ProcessManager = (sInstance ?: synchronized(this) {
            if(sInstance == null) {
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