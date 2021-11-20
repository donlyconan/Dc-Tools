package data.model

import java.io.File


public class Executor(file: File) : Command(file) {
    var delay = 0

    override fun toString(): String {
        return super.toString() + ", delay=$delay"
    }
}