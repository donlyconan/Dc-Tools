package ui.cell

import R
import base.extension.*
import base.settings.Settings
import base.view.Toast
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.text.Text
import javafx.stage.Stage
import tornadofx.*
import java.io.File

class NoteCell : ListCell<File>() {

    private lateinit var lbName: Label
    private val settings = Settings.getInstance()

    init {
        lbName = label {
            graphic = imageview(R.drawable.ic_file).also {
                it.prefHeight(15.0)
                it.prefWidth(15.0)
            }
            text = null
            paddingAll = 2.0
        }
        prefWidth = 0.0
        createContextMenu()
    }

    private fun createContextMenu() {
        contextMenu = ContextMenu()
        contextMenu.items.addAll(
            MenuItem("Rename"),
            MenuItem("Copy"),
            MenuItem("Copy text"),
            MenuItem("Delete"),
        )
        for (item in contextMenu.items) {
            item.setOnAction {
                handle(item.text, it)
            }
        }
    }

    override fun updateItem(item: File?, empty: Boolean) {
        super.updateItem(item, empty)
        if (!empty) {
            lbName.text = item?.nameWithoutExtension
            graphic = lbName
        } else {
            graphic = null
        }
    }


    fun handle(title: String, event: ActionEvent?) {
        when (title.toUpperCase()) {
            "RENAME" -> {
                val textField = textfield {
                    promptText = "Enter filename..."
                    text = item.nameWithoutExtension
                    prefWidth = 290.0
                }

                val dialog = Dialog<ButtonType>().apply {
                    this.title = "Rename file"
                    width = 200.0
                    dialogPane.buttonTypes.addAll(
                        ButtonType.OK, ButtonType.CANCEL
                    )
                    (dialogPane.scene.window as Stage)
                        .icons
                        .add(Image(R.drawable.settings))
                }

                dialog.dialogPane.content = vbox {
                    add(textField)
                    paddingAll = 5.0
                }

                val result = dialog.showAndWait()
                if (result.get() == ButtonType.OK) {
                    if (settings.getDir()?.contains(textField.text) == true) {
                        Toast.makeText(Stage(), "${textField.text} was exists!")
                            .play()
                    } else {
                        val file = File(settings.getDir(), textField.text + "." + EXT_NOTE_FILE)
                        item.renameTo(file)
                        listView.items[index] = file
                    }
                }
            }
            "COPY" -> {
                val clipboard = Clipboard.getSystemClipboard()
                clipboard.putFiles(arrayListOf(item))
            }
            "COPY TEXT" -> {
                val content = item.readText()
                val clipboard = Clipboard.getSystemClipboard()
                clipboard.putString(content)
            }
            "DELETE" -> {
                if (item.delete()) {
                    listView.items.remove(item)
                }
            }

        }
    }

}