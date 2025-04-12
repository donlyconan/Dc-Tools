package adapter

import R
import base.extenstion.id
import base.logger.Log
import base.view.CellRender
import data.model.CmdFile
import data.repository.CmdFileRepository
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import view.ComposerDialog

class CommandCell : CellRender<CmdFile>(), EventHandler<ActionEvent> {
    private var root: Parent? = null
    @FXML private lateinit var lbName: Label
    @FXML private lateinit var imgIcon: ImageView
    @FXML private lateinit var btnAction: Button
    var listener: OnMenuItemClickListener? = null
    private val scope by lazy { CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler {_,_ -> }) }

    init {
        prefWidth = 0.0
        root = loadingUI()
        btnAction.setOnAction(::handle)
    }

    private fun loadingUI(): Parent {
        val fxmlLoader = FXMLLoader(javaClass.getResource(R.layout.item_run_file_info))
        fxmlLoader.setController(this)
        return fxmlLoader.load()
    }

    override fun updateItem(item: CmdFile?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            lbName.text = item.name
            root?.prefWidth(listView.width)
            imgIcon.image = Image(R.drawable.ic_execute)
            graphic = root
        } else {
            graphic = null
        }
    }


    override fun handle(event: ActionEvent?) {
        val items = listView.items
        val item = items[index]

        println("event: ${event?.id}")

        when (event?.id) {
            R.id.btnRun, R.id.btnAction -> {
                listener?.onItemClick(event?.source as? MenuItem, item)
            }
            R.id.btnEdit -> {
                val stage = listView.scene.window as Stage
                val dialog = ComposerDialog.create(item)
                dialog.show(stage)
            }
            R.id.btnRefresh -> {
                items.clear()
                scope.launch {
                    CmdFileRepository.load()
                }
            }
            R.id.btnPath -> {
                val clipboard = Clipboard.getSystemClipboard()
                val content = ClipboardContent()
                content.putString(item.path)
                clipboard.setContent(content)
            }
            R.id.btnDelete -> {
                scope.launch { CmdFileRepository.delete(item) }
            }
            else -> {
                Log.d("Id ${event?.id} not found!")
            }
        }
    }

    interface OnMenuItemClickListener {
        fun onItemClick(item: MenuItem? , file: CmdFile?)
    }
}