package cmdhandlers

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield

class CmdBridge {
    private val process = ProcessBuilder("cmd")
        .redirectErrorStream(true)
        .start()

    private val writer = process.outputStream.bufferedWriter()
    private val reader = process.inputStream.bufferedReader()

    fun sendCommand(command: String) {
        writer.write(command)
        writer.newLine()
        writer.flush()
    }

    fun readAsFlow()= flow<String> {
       while (true) {
           yield()
           while (reader.ready()) {
               emit(reader.readLine())
           }
       }
    }

    fun close() {
        writer.close()
        reader.close()
        process.destroy()
    }
}
