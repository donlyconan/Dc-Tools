package data.repository

import base.observable.Subject
import data.model.Command

public interface CommandListRepository {
    val subject: Subject<List<Command>>

    suspend fun loadFromDisk(): List<Command>

    suspend fun add(command: Command)

    suspend fun update(command: Command)

    suspend fun delete(command: Command): Boolean
}