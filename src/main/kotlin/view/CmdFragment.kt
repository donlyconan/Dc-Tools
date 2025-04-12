package view

import R
import base.extenstion.onMain
import cmdhandlers.CmdBridge
import data.model.CmdFile
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
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
                        if (currentIndex in 1..histories.size) {
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
            save()
            tfInputCmd.clear()
        }
        btnNew.setOnAction {
            clear()
        }
        registerLog()
        btnSend.setOnAction {
            val command = tfInputCmd.text.trim()
            runCommand(command)
        }
        runCommandFromCmd()
    }

    private fun runCommandFromCmd() = onIO {
        withContext(Dispatchers.JavaFx) {
            tfInputCmd.isEditable = false
        }
        if(cmdFile != null) {
            cmdFile.load()
            cmdFile.cmdLines.forEach(::runCommand)
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

    private fun runCommand(command: String) = onIO {
        if (command.isNotBlank()) {
            histories += command
            cmdBridge.sendCommand(command)
        }
        onMain {
            tfInputCmd.clear()
        }
    }
}
