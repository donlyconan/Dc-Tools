package data.repository

import base.observable.Subject
import data.model.Command
import findIndex
import java.io.File


class CommandListRepositoryImpl(val file: File) : CommandListRepository {
    override val subject = Subject<List<Command>>()
    private var commands: MutableList<Command> = ArrayList()

    override suspend fun loadFromDisk(): List<Command> {
        commands.clear()
        val files = file.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile) {
                    commands.add(Command(file))
                }
            }
            subject.summit(commands)
        }
        return commands
    }

    override suspend fun add(command: Command) {
        loadFromDisk()
    }

    override suspend fun update(command: Command) {
        loadFromDisk()
    }

    override suspend fun delete(command: Command): Boolean {
        val res = command.file.delete()
        if (res) {
            commands.remove(command)
            loadFromDisk()
        }
        return res
    }

    private fun writeToFile(file: File, content: String) {
        val writer = file.bufferedWriter()
        writer.use { it.write(content) }
    }
}
