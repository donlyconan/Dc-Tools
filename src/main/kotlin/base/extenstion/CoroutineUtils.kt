package base.extenstion

import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import java.io.File


public fun CoroutineScope.runOnMainThread(invoker: () -> Unit) = Platform.runLater {
    try {
        invoker.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

public fun Int.toTime(): String {
    val time = this
    val hour = time / 3600
    val min = (time - hour * 3600) / 60
    val sec = time - min * 60 - hour * 3600
    return String.format("%02d:%02d:%02d", hour, min, sec)
}
