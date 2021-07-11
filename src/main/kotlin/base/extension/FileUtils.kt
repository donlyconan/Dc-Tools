package base.extension

import java.io.File
import javax.xml.bind.annotation.XmlType

const val EXT_NOTE_FILE = "nf"
const val DEFAULT_FILENAME = "Untitled"


fun loadAllFile(file: File): List<File> {
    val files = ArrayList<File>()
    loadFiles(file) { files.add(it) }
    return files
}

fun loadFiles(file: File, onNext: (file: File) -> Unit) {
    val listFile = file.listFiles()
    if (listFile != null) {
        for (item in listFile) {
            if (item.isFile) {
                onNext.invoke(item)
            } else {
                loadFiles(item, onNext)
            }
        }
    }
}

fun File.equalsWithPath(file: File?): Boolean {
    return absolutePath == file?.absolutePath
}

fun File.equalsWithPath(path: String): Boolean {
    return absolutePath == path
}

fun File.getListFiles(): List<File>? {
    return listFiles()?.filter { it.isFile && it.extension == EXT_NOTE_FILE }
}


fun File.getListFolders(): List<File>? {
    return listFiles()?.filter { !it.isFile }
}

fun File.contains(filename: String): Boolean {
    return listFiles()?.find { it.name == "$filename.$EXT_NOTE_FILE" } != null
}

fun File.createDefaultName(): String {
    var fileName = DEFAULT_FILENAME
    var index = 1

    while (contains(fileName)) {
        fileName = "$DEFAULT_FILENAME $index"
        index++
    }
    return fileName
}

fun createNFFile(folder: File?, filename: String): File {
    return File(folder, "$filename.$EXT_NOTE_FILE")
        .apply {
            createNewFile()
        }
}

