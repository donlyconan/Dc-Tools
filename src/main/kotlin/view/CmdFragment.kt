package view

import R
import base.extenstion.onMain
import base.logger.Log
import base.view.Toast
import cmdhandlers.CmdBridge
import data.model.CmdFile
import data.repository.CmdFileRepository
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import utils.COMMAND_EXT
import utils.getHome
import utils.onIO
import utils.pushLines
import java.io.File

class CmdFragment(title: String, private val cmdFile: CmdFile? = null) : BaseFragment(R.layout.fragment_command, title) {
    private val btnSend by fxid<Button>()
    private val btnNew by fxid<Button>()
    private val btnSave by fxid<Button>()
    private val btnRelaunch by fxid<Button>()
    private val tfLog by fxid<TextArea>()
    private val tfInputCmd by fxid<TextField>()
    private var cmdBridge: CmdBridge = CmdBridge()
    private val histories: ArrayList<String> by lazy { ArrayList() }
    private var currentIndex: Int = 0
    private var readingJob: Job? = null

    init {
        tfInputCmd.apply {
            setOnAction {
                val command = tfInputCmd.text.trim()
                runCommand(command)
            }
            setOnKeyPressed {
                when(it.code) {
                    KeyCode.DOWN -> {
                        if (currentIndex in 0..histories.size - 1) {
                            currentIndex++
                        }
                        tfInputCmd.text = histories.getOrNull(currentIndex).orEmpty()
                        positionCaret(text.length)
                    }
                    KeyCode.UP -> {
                        if (currentIndex in 0..histories.size) {
                            currentIndex--
                        }
                        tfInputCmd.text = histories.getOrNull(currentIndex).orEmpty()
                        positionCaret(text.length)
                    }
                    else -> { }
                }
            }
        }
        tfInputCmd.requestFocus()
        btnSave.setOnAction {
            println("btnSave is triggered!")
            save()
            tfInputCmd.clear()
        }
        btnNew.setOnAction {
            println("btnNew is triggered!")
            clear()
        }
        registerLog()
        btnSend.setOnAction {
            val command = tfInputCmd.text.trim()
            runCommand(command)
            currentIndex = histories.size - 1
        }
        btnRelaunch.setOnAction {
            println("btnRelaunch is triggered!")
            tfLog.clear()
            runCommandFromLines(histories)
        }
        onIO {
            cmdFile?.load()
            histories.clear()
            histories.addAll(cmdFile?.cmdLines ?: arrayListOf())
            Log.d("Run command: $cmdFile")
            runCommandFromLines(histories)
        }
    }

    private fun runCommandFromLines(lines: List<String>) = onIO {
        withContext(Dispatchers.JavaFx) {
            tfInputCmd.isEditable = false
        }
        lines.forEach { line ->
            runCommand(line, false)
        }
        withContext(Dispatchers.JavaFx) {
            tfInputCmd.isEditable = true
        }
    }

    private fun save() = onIO {
        val name = title.trim()
        val file = File(getHome(), name + COMMAND_EXT)
        file.pushLines(histories)
    }

    private fun clear() {
        readingJob?.cancel()
        currentIndex = 0
        histories.clear()
        tfInputCmd.clear()
        tfLog.clear()
        runCatching {
            cmdBridge.close()
        }
        cmdBridge = CmdBridge()
        registerLog()
    }

    fun registerLog() {
        readingJob = onIO {
            cmdBridge.readAsFlow()
                .collect { line ->
                    withContext(Dispatchers.JavaFx) {
                        tfLog.appendText(line)
                        tfLog.appendText("\n")
                    }
                }
        }
    }

    private fun runCommand(command: String, saveHis: Boolean = true) = onIO {
        if (command.isNotBlank()) {
            if(saveHis) {
                histories.add(command)
            }
            cmdBridge.sendCommand(command)
        }
        onMain {
            tfInputCmd.clear()
            currentIndex = histories.size
        }
    }
}
