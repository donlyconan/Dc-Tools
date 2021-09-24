package base.extenstion

import kotlinx.coroutines.CoroutineScope


fun CoroutineScope.runOnMainThread(invoker: () -> Unit) {
    try {
        invoker.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}