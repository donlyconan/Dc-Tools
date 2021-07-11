package build

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption



val TAB_SPACE = "    "

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

    builder.append("public class R {\n")

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
    }

    builder.append(TAB_SPACE).append("}\n")
        .append("}\n")
}


class BuildProject {
    companion object {
        @JvmStatic
        fun build() {
            val builder = StringBuilder()
            val file = File("A:\\Projects\\tools-cmic\\src\\main\\resources")
            val fileOut = File("A:\\Projects\\tools-cmic\\src\\main\\kotlin\\R.java")
            build(file, builder)
            Files.write(fileOut.toPath(), builder.toString().toByteArray(StandardCharsets.UTF_8))
        }
    }
}

