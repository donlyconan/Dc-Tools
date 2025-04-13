package data.repository

import data.model.CmdFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import utils.dotBat
import utils.getHome
import utils.pushLines
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds


object CmdFileRepository {

    suspend fun startWatch(block: suspend (files: List<CmdFile>) -> Unit) {
        val watchService = FileSystems.getDefault().newWatchService()
        val dirPath = Paths.get(getHome().path)
        dirPath.register(
            watchService, StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE
        )
        while (true) {
            val key = watchService.take()
            if(key.pollEvents().isNotEmpty()) {
                val files = load()
                withContext(Dispatchers.JavaFx) {
                    block(files)
                }
            }
            if (!key.reset()) break
        }
    }


    suspend fun load(): List<CmdFile> {
        val home = getHome()
        return home.listFiles().filter { it.isFile }
            .map {
                CmdFile(it.name.substringBefore("."), it.path)
            }

    }

    suspend fun add(cmdFile: CmdFile) {
        val file = File(getHome(), cmdFile.name.dotBat())
        file.pushLines(cmdFile.cmdLines)
    }

    suspend fun add(name: String, lines: List<String>): CmdFile {
        val file = File(getHome(), name.dotBat())
        file.pushLines(lines)
        return CmdFile(name, file.path, ArrayList(lines))
    }

    fun exist(name: String): Boolean {
        return File(getHome(), name.dotBat()).exists()
    }

    suspend fun delete(cmdFile: CmdFile): Boolean {
        println("Delete: $cmdFile")
        val file = File(cmdFile.path)
        return file.delete()
    }

    suspend fun delete(name: String): Boolean {
        println("Delete: $name")
        val file = File(getHome(), name.dotBat())
        return file.delete()
    }

}
