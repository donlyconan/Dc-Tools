package view

import R
import adapter.PreviewCommandCellFactory
import base.view.Toast
import data.model.Command
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import kotlinx.coroutines.*
import base.logger.Log
import tornadofx.Fragment
import java.io.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit


class CommandTabFragment() : Fragment(), EventHandler<ActionEvent>, PreviewCommandCellFactory.OnButtonClickListener {
    override val root: Parent by fxml(R.layout.fragment_list_tabs)
    private val lvExecutedStatements by fxid<ListView<Command>>()
    private val txtLoggedOutput by fxid<TextArea>()
    private val btnDelete by fxid<Button>()
    private var job: Job = Job()
    private var coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private var process: Process? = null
    var onClickListener: OnClickListener? = null

    init {
        txtLoggedOutput.clear()
        lvExecutedStatements.setCellFactory { createCellFactory() }
        lvExecutedStatements.selectionModel.selectionMode = SelectionMode.MULTIPLE
        btnDelete.setOnAction(this::handle)
    }

    private fun createCellFactory(): ListCell<Command> {
        val previewCommandCellFactory = PreviewCommandCellFactory()
        previewCommandCellFactory.onButtonClickListener = this
        previewCommandCellFactory.type = PreviewCommandCellFactory.TYPE_REDUCE
        return previewCommandCellFactory
    }

    override fun handle(event: ActionEvent?) {
        Log.p("handle: " + event?.source)
        val node = event?.source as? Node
        if (node?.id == R.id.btnStop) {
            job.cancelChildren()
            lvExecutedStatements.isDisable = false
            process?.destroyForcibly()
        } else if (!lvExecutedStatements.isDisable) {
            when (node?.id) {
                R.id.btnAdd -> {
                    onClickListener?.onClick(this, node)
                }
                R.id.btnDelete -> {
                    val indices = lvExecutedStatements.selectionModel.selectedIndices
                    Log.p("Array[] = $indices")
                    val newListItems = ArrayList<Command>()
                    for (i in 0 until lvExecutedStatements.items.size) {
                        val item = lvExecutedStatements.items[i]
                        if (!indices.contains(i)) {
                            newListItems.add(item)
                        }
                    }
                    lvExecutedStatements.items = FXCollections.observableList(newListItems)
                    lvExecutedStatements.refresh()
                }
                R.id.btnClearAll -> {
                    txtLoggedOutput.clear()
                    lvExecutedStatements.items.clear()
                    Log.p("on Clear All")
                }
                R.id.btnExecute -> {
                    if (lvExecutedStatements.items.isNotEmpty()) {
                        runOnDisableBlock { executeListStatements(lvExecutedStatements.items) }
                    } else {
                        Toast.makeText(null, "The command list is empty!").play()
                    }
                }
                else -> {
                    Toast.makeText(null, "No item selected").play()
                }
            }
        }
    }

    fun runOnDisableBlock(function: suspend CommandTabFragment.() -> Unit) {
        coroutineScope.launch {
            Platform.runLater {
                lvExecutedStatements.isDisable = true
                txtLoggedOutput.clear()
            }
            function.invoke(this@CommandTabFragment)
            Platform.runLater { lvExecutedStatements.isDisable = false }
        }
    }

    suspend fun executeListStatements(statments: List<Command>) {
        for (item in statments) {
            executeStatement(item)
        }
    }

    suspend fun executeStatement(statement: Command) {
        try {
            val commands = buildCommandLine(statement)
            val command = commands.last()
            process = Runtime.getRuntime().exec(commands)
            Log.p("execute: $command")
            process!!.waitFor(100L, TimeUnit.MILLISECONDS)
            logged(process!!.inputStream)
            logged(process!!.errorStream)
            Platform.runLater { txtLoggedOutput.selectPositionCaret(txtLoggedOutput.length) }
            process!!.destroy()
        } catch (e: IOException) {
            Platform.runLater { txtLoggedOutput.appendText("Error: " + e.message) }
            e.printStackTrace()
        }
    }

    private fun buildCommandLine(statement: Command): Array<String> {
//        val lines = statement.command.split('\n').filter { it.isNotEmpty() }
        val builder = StringBuilder()
//        for ((index, line) in lines.withIndex()) {
//            builder.append(" $line ")
//            logged(">> Executing[$index] = $line\n")
//            if (index < lines.size - 1) {
//                builder.append("&&")
//            }
//        }
        logged("\n-------------------------------------------------------------------\n")
        return arrayOf("cmd", "/c", builder.toString())
    }

    private fun logged(stream: InputStream) {
        val reader = stream.bufferedReader()
        reader.use {
            var line = it.readLine()
            while (line != null) {
                val tmp = line
                logged(tmp + '\n')
                line = it.readLine()
            }
        }
    }

    private fun logged(text: String) = Platform.runLater {
        txtLoggedOutput.appendText(text)
    }

    override fun onDelete() {
        super.onDelete()
        job.cancel()
    }

    fun addCommands(commands: List<Command>) {
        lvExecutedStatements.items.addAll(commands)
    }

    override fun onDeleted(statement: Command) {
    }

    override fun onStarted(statement: Command) {
        runOnDisableBlock { executeStatement(statement) }
    }

    override fun onEdited(statement: Command) {
    }

    interface OnClickListener {
        fun onClick(tab: CommandTabFragment, node: Node)
    }
}


//fun main() {
//    val commands = arrayOf("cmd", "/c", "cd A:\\Projects\\comic && dir && tree")
//    val builder = ProcessBuilder("cmd", "/c", "C:\\Users\\Admin\\Documents\\commands\\test.bat")
//        .
//    val p = builder.start()
//
//    val reader = p.inputStream.bufferedReader()
//    reader.use {
//        var line = it.readLine()
//        while (line != null) {
//            println(line)
//            line = reader.readLine()
//        }
//    }
//    p.waitFor()
////    for (command in commands) {
////        p = r.exec(command)
////        p.waitFor()
////    }
//}