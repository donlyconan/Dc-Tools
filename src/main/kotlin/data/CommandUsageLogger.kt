package data

import com.google.gson.Gson
import data.model.CommandUsageFrequency
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import utils.HOME_FOLDER
import java.io.File

class CommandUsageLogger private constructor() {

    companion object {
        const val LOGGER_NAME = "usage-command.log"
        private var instance: CommandUsageLogger? = null

        fun getInstance(): CommandUsageLogger {
            if(instance == null) {
                instance = CommandUsageLogger()
            }
            return instance!!
        }
    }

    private val file: File by lazy { File(HOME_FOLDER, LOGGER_NAME) }
    private val flushIntervalMillis: Long = 10_000L
    private val gson = Gson()
    private val logQueue = MutableSharedFlow<CommandUsageFrequency>(extraBufferCapacity = 100)
    private val buffer = mutableListOf<CommandUsageFrequency>()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startLoggingLoop()
    }

    fun logNow(data: CommandUsageFrequency) {
        logQueue.tryEmit(data)
    }

    private fun startLoggingLoop() {
        scope.launch {
            combine(
                logQueue.onEach { buffer.add(it) },
                tickerFlow(flushIntervalMillis)
            ) { _, _ -> }
                .collect {
                    flushToFile()
                }
        }
    }

    private fun flushToFile() {
        if (buffer.isEmpty()) return
        file.appendText(
            buffer.joinToString("\n") { gson.toJson(it) } + "\n"
        )
        buffer.clear()
    }

    private fun tickerFlow(intervalMillis: Long): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(intervalMillis)
        }
    }

    fun shutdown() {
        scope.cancel()
        flushToFile() // đảm bảo flush lần cuối
    }
}
