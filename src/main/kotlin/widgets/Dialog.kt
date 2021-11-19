package widgets

import javafx.stage.Stage
import javafx.stage.WindowEvent

abstract class Dialog() : Stage() {

    init {
        setOnCloseRequest(::onDismiss)
    }


    abstract fun onDismiss(event: WindowEvent)

}