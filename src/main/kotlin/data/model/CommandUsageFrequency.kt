package data.model

data class CommandUsageFrequency(
    val commandName: String,
    val usageCount: Int,
    val lastUsed: Long
)
