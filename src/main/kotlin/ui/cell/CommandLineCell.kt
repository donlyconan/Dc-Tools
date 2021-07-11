package ui.cell

import javafx.scene.control.Button
import javafx.scene.control.TableCell
import javafx.scene.text.Font
import tornadofx.*

class CommandLineCell<T>(listener: (item: String?) -> Unit = {}) : TableCell<T, String>() {

    private var action: Button = button {
        imageview(R.drawable.play_button).apply {
            prefWidth(15.0)
            prefHeight(15.0)
        }
        setOnAction {
            listener.invoke(item)
        }
        prefWidth = 15.0
        prefHeight = 15.0
    }
    private var item: String? = null

    init {
        paddingAll = 6
        alignment = null
        font = Font("Consolas", 13.0)
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (!empty && item != null) {
            graphic = action
        }
        this.item = item
    }

}