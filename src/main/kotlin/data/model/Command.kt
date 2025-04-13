package data.model
import java.io.File

open class Command(var file: File) : Comparable<Command> {
    companion object {
        const val EXT_BAT = "bat"
        const val EXT_CMD = "cmd"
        const val EXT_SCT = "sct"
    }

    var name = file.nameWithoutExtension
    var fullName = file.name
    var ext = file.extension
    val isExecutable get() = ext == EXT_BAT || ext == EXT_CMD
    val modified get() = file.lastModified()

    var content: String = ""
        get() = file.readText()

    override fun compareTo(other: Command): Int {
        if (!other.isExecutable && isExecutable) {
            return Int.MIN_VALUE
        } else if (other.isExecutable && !isExecutable) {
            return Int.MAX_VALUE
        } else {
            return (other.modified - modified).toInt()
        }
    }

    override fun equals(other: Any?): Boolean {
        val item = other as? Command
        return this === other || this.fullName == item?.fullName
    }

    override fun toString(): String {
        return "Command(file=$file, name='$name', fullName='$fullName', ext='$ext', isExecutable=$isExecutable, modified='$modified')"
    }


}