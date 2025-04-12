package view

import base.view.Toast
import javafx.scene.Parent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import tornadofx.Fragment

abstract class BaseFragment(layoutId: String, title: String = "Fragment"): Fragment(title) {
    override val root by fxml<Parent>(layoutId)
    private val handler = CoroutineExceptionHandler { _, e -> Toast.makeText(e.message) }
    val coroutineScope = CoroutineScope(Dispatchers.IO + handler + SupervisorJob())

    open fun onDestroy() { }
}