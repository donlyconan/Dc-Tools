package adapter

import R
import base.extenstion.newFXMLLoader
import data.model.Command
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import base.logger.Log

class PreviewCommandCellFactory : ListCell<Command>(), EventHandler<ActionEvent> {
    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_REDUCE = 1
    }
    private lateinit var root: Parent
    @FXML
    private lateinit var lbName: Label
    @FXML
    private lateinit var lbStt: Label
    @FXML
    private lateinit var imgDelete: ImageView
    @FXML
    private lateinit var imgAction: ImageView
    @FXML
    private lateinit var imgEdit: ImageView
    @FXML
    private lateinit var btnDelete: Button
    @FXML
    private lateinit var btnEdit: Button
    var type = TYPE_DEFAULT
    var onButtonClickListener: OnButtonClickListener? = null

    init {
        prefWidth = 0.0
    }

    override fun updateItem(item: Command?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            val fxmlLoader = newFXMLLoader(this, R.layout.item_file_info)
            fxmlLoader.setController(this)
            root = fxmlLoader.load()
            root.prefWidth(listView.width)
            imgAction.image = Image(R.drawable.play_button)
            if(type == TYPE_REDUCE) {
                btnDelete.isVisible = false
                btnEdit.isVisible = false
            } else {
                imgDelete.image = Image(R.drawable.ic_trash)
                imgEdit.image = Image(R.drawable.ic_edit)
            }
            graphic = root
        } else {
            graphic = null
        }
    }

    override fun handle(event: ActionEvent?) {
        val node = event?.source as? Node
        when (node?.id) {
            "btnDelete" -> onButtonClickListener?.onDeleted(item)
            "btnAction" -> onButtonClickListener?.onStarted(item)
            "btnEdit" -> onButtonClickListener?.onEdited(item)
            else -> Log.p("Id not found!")
        }
    }

    interface OnButtonClickListener {
        fun onDeleted(statement: Command)
        fun onStarted(statement: Command)
        fun onEdited(statement: Command)
    }
}