package data

import R
import com.google.gson.Gson
import data.model.CommandUsageFrequency
import utils.HOME_FOLDER
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object CommandManager {
    val commandUsageLogger by lazy { CommandUsageLogger.getInstance() }

    val cmdFrequencies: Set<CommandUsageFrequency> by lazy {
        val gson = Gson()
        val now = System.currentTimeMillis()
        val inputStream = javaClass.getResourceAsStream(R.raw.command)
        val commands = ReverseLineReader(File(HOME_FOLDER, CommandUsageLogger.LOGGER_NAME)).readLinesReversed(200)
            .map { line ->
                gson.fromJson(line, CommandUsageFrequency::class.java)
            }.take(100)
            .distinctBy { it.commandName }
            .sortedBy { it.usageCount }
            .toList() +  inputStream.bufferedReader().readLines()
            .map { cmd ->
                CommandUsageFrequency(cmd, 1, now)
            }
        sortCommands(commands).distinctBy {
            it.commandName
        }.toSet()
    }

    fun sortCommands(commands: List<CommandUsageFrequency>): List<CommandUsageFrequency> {
        val now = Instant.now()
        val α = 2.0  // trọng số usage
        val β = 1.0  // trọng số recency

        return commands.sortedByDescending { cmd ->
            val last = Instant.ofEpochMilli(cmd.lastUsed)
            val daysAgo = ChronoUnit.DAYS.between(last.atZone(ZoneId.systemDefault()).toLocalDate(),
                now.atZone(ZoneId.systemDefault()).toLocalDate()).coerceAtLeast(0)
            val recencyScore = (30 - daysAgo).coerceAtLeast(0)
            α * cmd.usageCount + β * recencyScore
        }
    }

    suspend fun log(command: String) {
        val cmd = cmdFrequencies.find { it.commandName == command }
        cmd?.let { cmd ->
            commandUsageLogger.logNow(cmd.copy(usageCount = cmd.usageCount + 1))
        }
    }

}