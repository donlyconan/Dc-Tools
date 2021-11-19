package view

import R
import adapter.ExecutorCell
import base.extenstion.runOnMainThread
import base.logger.Log
import base.manager.ExecutorService
import base.view.Toast
import data.model.Executor
import data.repository.CommandListRepository
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import kotlinx.coroutines.*
import tornadofx.Fragment
import java.io.*
import java.text.SimpleDateFormat


class CommandOperationFragment(val repository: CommandListRepository) : Fragment(), EventHandler<ActionEvent> {
    companion object {
        const val ENQUEUE = 0
        const val STARTED = 1
        const val RUNNING = 2
        const val ENDED = 3
        const val TIME_DELAY = 10L
        val SIMPLE_FORMAT = SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS")
    }

    override val root: Parent by fxml(R.layout.fragment_list_tabs)
    private val lvExecutedStatements by fxid<ListView<Executor>>()
    private val txtLoggedOutput by fxid<TextArea>()
    private val btnDelete by fxid<Button>()
    private val btnStop by fxid<Button>()
    private val btnAdd by fxid<Button>()
    private val btnExecute by fxid<Button>()
    private val btnClearAll by fxid<Button>()
    private var job: Job = Job()
    private var coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private var state = ENQUEUE
    private val service: ExecutorService = ExecutorService()
    var onClickListener: OnClickListener? = null

    private val runningCommandListener = object : ExecutorService.RunningCommandListener {
        override suspend fun onRunning(inp: InputStream, err: InputStream, out: OutputStream) {
            Log.d("onRunning: ")
            inp.bufferedReader().use {
                var line = it.readLine()
                while (line != null) {
                    delay(TIME_DELAY)
                    log(line)
                    line = it.readLine()
                }
            }
            err.bufferedReader().use {
                var line = it.readLine()
                while (line != null) {
                    delay(TIME_DELAY)
                    log(line)
                    line = it.readLine()
                }
            }
        }

        override fun onClosed(process: Process?) {
            Log.d("onClosed: ")
            setEnableStop(true)
        }
    }

    private val onActionEvent = object : ExecutorCell.ActionClickListener {
        override fun onActionClick(item: Executor) {
            service.executors = arrayListOf(item)
            service.prepare()
            setState(RUNNING)
        }
    }

    init {
        txtLoggedOutput.clear()
        lvExecutedStatements.setCellFactory {
            val  cell = ExecutorCell()
            cell.listener = onActionEvent
            cell
        }
        lvExecutedStatements.selectionModel.selectionMode = SelectionMode.MULTIPLE
        btnDelete.setOnAction(this::handle)
        service.listener = runningCommandListener
    }

    private fun setState(state: Int) {
        when (state) {
            STARTED -> {
                service.executors = lvExecutedStatements.items
                service.prepare()
            }
            RUNNING -> {
                setEnableStop(false)
                txtLoggedOutput.clear()
                coroutineScope.launch { service.execute() }
            }
            ENDED -> {
                service.close()
            }
        }
        this.state = state
    }

    fun setEnableStop(enable: Boolean)  {
        lvExecutedStatements.isDisable = !enable
        btnStop.isDisable = enable
        btnExecute.isDisable = !enable
        btnAdd.isDisable = !enable
        btnDelete.isDisable = !enable
        btnClearAll.isDisable = !enable
    }

    override fun handle(event: ActionEvent?) {
        Log.d("handle: " + event?.source)
        val node = event?.source as? Node

        if (node?.id == R.id.btnStop) {
            job.cancelChildren()
            lvExecutedStatements.isDisable = false
            service.close()
            setEnableStop(true)
            return
        }

        if (lvExecutedStatements.isDisable) {
            return
        }
        when (node?.id) {
            R.id.btnAdd -> {
                onClickListener?.onAdded(lvExecutedStatements, node)
            }
            R.id.btnDelete -> {
                val indices = lvExecutedStatements.selectionModel.selectedIndices
                val newListItems = ArrayList<Executor>()
                for (i in 0 until lvExecutedStatements.items.size) {
                    val item = lvExecutedStatements.items[i]
                    if (!indices.contains(i)) {
                        newListItems.add(Executor(item.file))
                    }
                }
                lvExecutedStatements.items = FXCollections.observableList(newListItems)
                lvExecutedStatements.refresh()
            }
            R.id.btnClearAll -> {
                txtLoggedOutput.clear()
                lvExecutedStatements.items.clear()
                Log.d("on Clear All")
            }
            R.id.btnExecute -> {
                val items = lvExecutedStatements.items
                if(!items.isEmpty()) {
                    try {
                        setState(STARTED)
                        setState(RUNNING)
                    } catch (e: Exception) {
                        Toast.makeText(primaryStage, e.message).play()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(null, "No item selected").play()
                }
            }
            else -> {
                Toast.makeText(null, "No item selected").play()
            }
        }
    }

    fun onMenuItemClick(event: ActionEvent) {
        val item = event.source as? MenuItem
        when(item?.text?.toUpperCase()) {
            "COPY" -> {
                val clipboard = Clipboard.getSystemClipboard()
                val content = ClipboardContent()
                content.putString(txtLoggedOutput.selectedText)
                clipboard.setContent(content)
            }
            "SELECT ALL" -> {
                txtLoggedOutput.selectAll()
            }
            "CLEAR ALL" -> txtLoggedOutput.clear()
        }
    }

    private fun log(message: String) = coroutineScope.runOnMainThread {
        val text = String.format("%s : %s", SIMPLE_FORMAT.format(System.currentTimeMillis()), message)
        txtLoggedOutput.appendText(text)
        txtLoggedOutput.appendText("\n")
    }

    fun onClosed() {
        service.close()
        job.cancel()
    }

    interface OnClickListener {
        fun onAdded(lst: ListView<Executor>, node: Node)
    }
}
