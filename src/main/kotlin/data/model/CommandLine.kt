package data.model

import tornadofx.getProperty
import tornadofx.property

class CommandLine(
    name: String,
    content: String,
    description: String,
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = System.currentTimeMillis()
) {
    var name: String by property(name)
    fun nameProperty() = getProperty(CommandLine::name)

    var content: String by property(content)
    fun contentProperty() = getProperty(CommandLine::content)

    var description: String by property(description)
    fun descriptionProperty() = getProperty(CommandLine::description)

    var createdAt: Long by property(createdAt)
    fun createdAtProperty() = getProperty(CommandLine::createdAt)

    var updatedAt: Long by property(updatedAt)
    fun updatedAtProperty() = getProperty(CommandLine::updatedAt)
}