package data.repository

import base.logger.Log
import data.model.CmdFile
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import utils.COMMAND_EXT
import utils.getHome
import utils.pushLines
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds


object CmdFileRepository {

    val files by lazy { FXCollections.observableArrayList<CmdFile>() }

    suspend fun startWatch() {
        val watchService = FileSystems.getDefault().newWatchService()
        val dirPath = Paths.get(getHome().path)
        dirPath.register(
            watchService, StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        )
        while (true) {
            val key = watchService.take()
            load()
            if (!key.reset()) break
        }
    }


    suspend fun load() {
        val home = getHome()
        val newFiles = home.listFiles().filter { it.isFile }
            .map {
                CmdFile(it.name.substringBefore("."), it.path)
            }
        files.clear()
        files.addAll(newFiles)
    }

    suspend fun add(cmdFile: CmdFile) {
        val file = File(getHome(), cmdFile.name + COMMAND_EXT)
        file.pushLines(cmdFile.cmdLines)
    }

    suspend fun add(name: String, lines: List<String>): CmdFile {
        val file = File(getHome(), name + COMMAND_EXT)
        file.pushLines(lines)
        return CmdFile(name, file.path, ArrayList(lines))
    }

    suspend fun delete(cmdFile: CmdFile): Boolean {
        val file = File(getHome(), cmdFile.name + COMMAND_EXT)
        return file.delete()
    }

}
