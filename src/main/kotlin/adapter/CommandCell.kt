package adapter

import R
import base.extenstion.id
import base.extenstion.newFXMLLoader
import base.extenstion.node
import base.logger.Log
import base.view.CellRender
import data.model.Command
import data.repository.CommandListRepository
import duplicate
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.ComposerDialog
import java.io.File

class CommandCell(val repository: CommandListRepository, val scope: CoroutineScope) :
    CellRender<Command>(), EventHandler<ActionEvent> {

    private lateinit var root: Parent
    @FXML
    private lateinit var lbName: Label
    @FXML
    private lateinit var imgIcon: ImageView
    @FXML
    private lateinit var btnRun: MenuItem
    var listener: OnMenuItemClickListener? = null

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
            if (item.isExecutable) {
                btnRun.isVisible = true
                imgIcon.image = Image(R.drawable.ic_execute)
            } else {
                btnRun.isVisible = false
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
            R.id.btnRun -> {
                listener?.onItemClick(event.source as? MenuItem, item.file)
            }
            R.id.btnEdit -> {
                val stage = listView.scene.window as Stage
                val dialog = ComposerDialog.create(item)
                dialog.onDismissListener = object : ComposerDialog.OnDismissListener {
                    override fun onNegativeClick() {}
                    override fun onPositiveClick(statement: Command) {
                        scope.launch { repository.update(statement) }
                    }
                }
                dialog.show(stage)
            }
            R.id.btnRefresh -> {
                scope.launch { repository.loadFromDisk() }
            }

            R.id.btnPath -> {
                val clipboard = Clipboard.getSystemClipboard()
                val content = ClipboardContent()
                content.putString(item.file.absolutePath)
                clipboard.setContent(content)
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

    interface OnMenuItemClickListener {
        fun onItemClick(item: MenuItem? , file: File)
    }
}