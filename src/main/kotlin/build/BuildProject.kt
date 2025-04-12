package build

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

val TAB_SPACE = "    "
val PATTERN = "(fx:id=\"([a-zA-Z]*)\")"

fun lazyGetAllFile(file: File, action: (file: File) -> Unit) {
    file.listFiles()?.let { listFile ->
        for (item in listFile) {
            val result = !item.name.equals("R.java") && !item.name.contains("-")
            if (result) {
                action.invoke(item)
                if (!item.isFile) {
                    lazyGetAllFile(item, action)
                }
            }
        }
    }
}


fun build(file: File, builder: StringBuilder) {
    var hasFirFolder = false

    builder.append(" \n")
    builder.append("public class R {\n")
    var fileLayout: File? = null

    lazyGetAllFile(file) {
        if (it.isFile) {
            val fileName = it.nameWithoutExtension
            val path = it.absolutePath.substringAfter("resources")
                .replace("\\", "/")
            builder.append(TAB_SPACE + TAB_SPACE)
                .append("public static final String ")
                .append(fileName)
                .append(" = ")
                .append('"')
                .append(path)
                .append('"')
                .append(";\n")
        } else {
            if (hasFirFolder) {
                builder.append(TAB_SPACE).append("}\n")
            }
            builder.append(TAB_SPACE)
                .append("public static class ")
                .append(it.name)
                .append(" {\n")
            hasFirFolder = true
        }
        if(it.name.equals("layout")) {
            fileLayout = it
        }
    }

    builder.append(TAB_SPACE).append("}\n")

    var sets = HashSet<String>()
    lazyGetAllFile(fileLayout!!) {
        buildId(it, sets)
    }
    builder.append(TAB_SPACE).append("public static class id {\n")
    for (id in sets) {
        builder.append(TAB_SPACE).append(TAB_SPACE)
            .append("public static final String ").append(id).append(" = ")
            .append("\"$id\";\n")
    }

    builder.append(TAB_SPACE).append("}\n")
        .append("}\n")
}

fun buildId(file: File, sets: HashSet<String>) {
    val builder = StringBuilder()
    builder.append(file.readLines())
    val matches = PATTERN.toRegex().findAll(builder)
    val iterator = matches.iterator()
    while (iterator.hasNext()) {
        val res = iterator.next()
        for (group in res.groupValues) {
            if (!group.startsWith("fx:id")) {
                sets.add(group)
            }
        }
    }
}


fun main() {
    val builder = StringBuilder()
    val file = File("src\\main\\resources")
    val fileOut = File("src\\main\\kotlin\\R.java")
    build(file, builder)
    print(builder.toString())
    Files.write(fileOut.toPath(), builder.toString().toByteArray(StandardCharsets.UTF_8))
}
