package ui.dialog

import data.model.CommandLine
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import java.net.URL

class InputCommandDialog(title: String, val item: CommandLine? = null) : Dialog<ButtonType>() {

    @FXML
    lateinit var txtCommandName: TextField

    @FXML
    lateinit var lbTitle: Label

    @FXML
    lateinit var txtDescription: TextArea

    @FXML
    lateinit var txtCommands: TextArea

    @FXML
    lateinit var datePicker: DatePicker

    private val window = dialogPane.scene.window

    init {
        this.title = title

        val loader = FXMLLoader(javaClass.getResource(R.layout.fragment_input_command))
        loader.setController(this)
        dialogPane.content = loader.load()

        if (item != null) {
            txtCommandName.text = item.name
            txtDescription.text = item.description
            txtCommands.text = item.content
        }
        lbTitle.text = title
        dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)
        window.setOnCloseRequest { hide() }
    }

    @JvmName("getItem1")
    fun getItem(): CommandLine {
        if (item == null) {
            return CommandLine(txtCommandName.text, txtCommands.text, txtDescription.text)
        } else {
            return CommandLine(txtCommandName.text
                , txtCommands.text
                , txtDescription.text
                , item.createdAt
                , System.currentTimeMillis())
        }
    }
}