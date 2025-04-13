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
import kotlinx.coroutines.delay
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
    private var histories: ArrayList<String> = ArrayList()
    private var currentIndex: Int = 0
    private var readingJob: Job? = null
    var onCommandProcessingListener: OnCommandProcessingListener? = null

    init {
        tfInputCmd.apply {
            setOnAction {
                sendCommandTo()
            }
            setOnKeyPressed {
                when(it.code) {
                    KeyCode.DOWN -> {
                        if (currentIndex < histories.size) {
                            currentIndex++
                        }
                        tfInputCmd.text = histories.getOrNull(currentIndex).orEmpty()
                        positionCaret(text.length)
                    }
                    KeyCode.UP -> {
                        if (currentIndex > 0) {
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
            sendCommandTo()
        }
        btnRelaunch.setOnAction {
            println("btnRelaunch is triggered!")
            tfLog.clear()
            runCommandFromLines(histories)
        }
        onIO {
            cmdFile?.load()
            Log.d("Run command: $cmdFile")
            histories = cmdFile?.cmdLines ?: arrayListOf()
            runCommandFromLines(histories)
        }
    }

    private fun sendCommandTo() {
        val command = tfInputCmd.text.trim()
        runCommand(command)
        histories.add(command)
        currentIndex = histories.size
        tfInputCmd.clear()
    }

    private fun runCommandFromLines(lines: List<String>) = onIO {
        println("runCommandFromLines: $lines")
        withContext(Dispatchers.JavaFx) {
            tfInputCmd.isEditable = false
        }
        lines.forEach { line ->
            runCommand(line)
            delay(100)
        }
        withContext(Dispatchers.JavaFx) {
            tfInputCmd.isEditable = true
        }
    }

    private fun save() = onIO {
        val name = title.trim()
        val file = File(getHome(), name + COMMAND_EXT)
        file.pushLines(histories)
        onMain {
            Toast.makeText("${title} is saved!")
        }
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
            when(command.lowercase()) {
                COMMANDS.CLS.cmd -> {
                    tfLog.clear()
                }
                COMMANDS.EXIT.cmd -> {
                    cmdBridge.sendCommand(command)
                    onCommandProcessingListener?.onExit(title)
                }
                else -> cmdBridge.sendCommand(command)
            }
        }
    }

    override fun onDestroy() {
        println("Cmd Fragment: onDestroy called!")
        super.onDestroy()
        readingJob?.cancel()
        kotlin.runCatching {
            cmdBridge.close()
        }
    }


    enum class COMMANDS(val cmd: String) {
        CLS("cls"), EXIT("exit")
    }

    interface OnCommandProcessingListener {
        fun onExit(title: String)
    }

}
