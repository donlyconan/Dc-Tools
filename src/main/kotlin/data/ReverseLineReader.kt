package data

import java.io.File
import java.io.RandomAccessFile

class ReverseLineReader(private val file: File) {

    fun readLinesReversed(limit: Int = Int.MAX_VALUE): Sequence<String> = sequence {
        if (!file.exists()) return@sequence
        val raf = RandomAccessFile(file, "r")
        val builder = StringBuilder()
        var pointer = raf.length() - 1
        var lineCount = 0

        while (pointer >= 0 && lineCount < limit) {
            raf.seek(pointer)
            val char = raf.read().toChar()

            if (char == '\n') {
                if (builder.isNotEmpty()) {
                    yield(builder.reverse().toString())
                    builder.clear()
                    lineCount++
                }
            } else {
                builder.append(char)
            }
            pointer--
        }
        if (builder.isNotEmpty()) {
            yield(builder.reverse().toString())
        }
        raf.close()
    }
}