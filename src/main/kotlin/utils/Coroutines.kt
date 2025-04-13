package utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import view.BaseFragment

fun BaseFragment.onMain(block: suspend CoroutineScope.() -> Unit): Job {
    return coroutineScope.launch(Dispatchers.JavaFx) {
        block()
    }
}

fun BaseFragment.onIO(block: suspend CoroutineScope.() -> Unit): Job {
    return coroutineScope.launch(Dispatchers.IO) {
        block()
    }
}

fun CoroutineScope.onMain(block: suspend CoroutineScope.() -> Unit): Job {
    return launch(Dispatchers.JavaFx) {
        block()
    }
}

fun CoroutineScope.onIO(block: suspend CoroutineScope.() -> Unit): Job {
    return launch(Dispatchers.IO) {
        block()
    }
}