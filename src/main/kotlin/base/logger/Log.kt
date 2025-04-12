package base.logger

import java.text.SimpleDateFormat

object Log {
    private val format = SimpleDateFormat("dd/MM/YY HH:mm:ss")
    const val DEBUG = true
    const val TAG = "TAG"

    fun d(message: String) {
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