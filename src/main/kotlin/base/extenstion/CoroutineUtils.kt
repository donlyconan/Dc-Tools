package base.extenstion

import javafx.application.Platform
import kotlinx.coroutines.CoroutineScope
import java.io.File


fun CoroutineScope.runOnMainThread(invoker: () -> Unit) = Platform.runLater {
    try {
        invoker.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
