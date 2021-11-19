import java.io.File



fun <T> List<T>.findIndex(compare: (item: T) -> Boolean): Int {
    for ((id, item) in withIndex()) {
        if (compare.invoke(item)) {
            return id
        }
    }
    return -1
}

fun File.retype(ext: String): File {
    val newPath = "$parent${File.separatorChar}$nameWithoutExtension.$ext"
    renameTo(File(newPath))
    return this
}


fun File.hasName(filename: String): Boolean {
    return File(this, filename).exists()
}

fun File.duplicate(): File {
    var name = ""
    val parent = parentFile
    var index = 1
    do {
        name = String.format("$nameWithoutExtension $index.$extension")
        index++
    } while (parent.hasName(name))
    val newFile = File(parentFile, name)
    copyTo(newFile)
    return newFile
}
