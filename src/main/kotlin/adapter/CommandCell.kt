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
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.CommandDialog

class CommandCell(val repository: CommandListRepository, val scope: CoroutineScope) :
    CellRender<Command>(), EventHandler<ActionEvent> {

    private lateinit var root: Parent
    @FXML
    private lateinit var lbName: Label
    @FXML
    private lateinit var imgIcon: ImageView

    init {
        prefWidth = 0.0
    }

    private fun loadingUI(): Parent {
        val fxmlLoader = newFXMLLoader(this, R.layout.item_file_info)
        fxmlLoader.setController(this)
        return fxmlLoader.load()
    }

    override fun updateItem(item: Command?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            root = loadingUI()
            lbName.text = item.name
            root.prefWidth(listView.width)
            if(item.isExecutable) {
                imgIcon.image = Image(R.drawable.ic_execute)
            } else {
                imgIcon.image = Image(R.drawable.ic_script)
            }
            graphic = root
        } else {
            graphic = null
        }
    }


    override fun handle(event: ActionEvent?) {
        val items = listView.items
        val item = items.get(index)

        when (event?.id) {
            R.id.btnEdit -> {
                val stage = listView.scene.window as Stage
                val dialog = CommandDialog.create(item)
                dialog.onDismissListener = object : CommandDialog.OnDismissListener {
                    override fun onNegativeClick() {}
                    override fun onPositiveClick(statement: Command) {
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
            R.id.btnDelete -> {
                scope.launch { repository.delete(item) }
            }
            else -> {
                Log.d("Id ${event?.id} not found!")
            }
        }
    }
}