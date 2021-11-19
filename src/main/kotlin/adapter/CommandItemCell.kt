package adapter

import R
import base.extenstion.id
import base.extenstion.newFXMLLoader
import base.logger.Log
import base.view.CellRender
import data.model.Command
import data.repository.CommandListRepository
import duplicate
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.CommandDialog

class CommandItemCell(val repository: CommandListRepository, val scope: CoroutineScope) :
    CellRender<Command>(), EventHandler<ActionEvent> {
    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_REDUCE = 1
    }
    private lateinit var root: Parent
    @FXML
    private lateinit var lbName: Label
    @FXML
    private lateinit var imgIcon: ImageView
    @FXML
    private lateinit var btnAction: Button

    init {
        prefWidth = 0.0
        loadingUI()
    }

    private fun loadingUI() {
        val fxmlLoader = newFXMLLoader(this, R.layout.item_file_info)
        fxmlLoader.setController(this)
        root = fxmlLoader.load()
    }

    override fun updateItem(item: Command?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            lbName.text = item.name
            setFileIconType(item.isExecutable)
            root.prefWidth(listView.width)
            graphic = root
        } else {
            graphic = null
        }
    }

    fun setFileIconType(executable: Boolean) {
        if(executable) {
            imgIcon.image = Image(R.drawable.ic_execute)
            btnAction.isVisible = true
        } else {
            imgIcon.image = Image(R.drawable.ic_script)
            btnAction.isVisible = false
        }
    }

    override fun handle(event: ActionEvent?) {
        val items = listView.items
        val item = items.get(index)

        when (event?.id) {
            R.id.btnEdit -> {
                val stage = listView.scene.window as Stage
                val dialog = CommandDialog.create("", item)
                dialog.onDismissListener = object : CommandDialog.OnDismissListener {
                    override fun onClickCancel() {}
                    override fun onClickOk(statement: Command) {
                        scope.launch { repository.update(statement) }
                    }
                }
                dialog.show(stage)
            }
            R.id.btnDuplicate -> {
                scope.launch {
                    item.file.duplicate()
                    repository.loadFromDisk()
                }
            }
            R.id.btnDelete-> {
                scope.launch { repository.delete(item) }
            }
            else -> {
                Log.d("Id ${event?.id} not found!")
            }
        }
    }

    fun setOnAction(event: EventHandler<ActionEvent>) {
        btnAction.onAction = event
    }

}