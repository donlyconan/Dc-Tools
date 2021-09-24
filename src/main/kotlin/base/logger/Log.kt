package base.logger

import java.text.SimpleDateFormat

object Log {
    const val DEBUG = true
    const val TAG = "TAG"
    val format = SimpleDateFormat("dd/MM/YY HH:mm:ss")

    fun p(message: String) {
        if (DEBUG) {
            val text = String.format("%s   %s: %s", format.format(System.currentTimeMillis()), TAG, message)
            println(text)
        }
    }

    fun s(message: String) {
        val text = String.format("%s %s", format.format(System.currentTimeMillis()), TAG, message)
        println(text)
    }

}