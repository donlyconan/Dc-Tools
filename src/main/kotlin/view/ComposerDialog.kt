package view

import R
import base.logger.Log
import base.view.Toast
import data.model.CmdFile
import data.repository.CmdFileRepository
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import utils.*
import java.io.File


class ComposerDialog private constructor(
    private val action: String,
    var cmdFile: CmdFile?
) : BaseFragment(R.layout.fragment_input_command), EventHandler<ActionEvent> {

    companion object {
        const val ACTION_INSERT = "Insert command line"
        const val ACTION_EDIT = "Edit command line"

        fun create(cmdFile: CmdFile? = null): ComposerDialog {
            val action = if(cmdFile == null) ACTION_INSERT else ACTION_EDIT
            return ComposerDialog(action, cmdFile = cmdFile)
        }
    }
    private val txtName by fxid<TextField>()
    private val txtCommands by fxid<TextArea>()
    private var stage: Stage? = null

    init {
        if (action == ACTION_EDIT) {
            initSetUp()
        }
    }

    private fun initSetUp() = onMain {
        Log.d("initSetUp: $cmdFile")
        txtName.text = cmdFile?.name
        onIO {
            cmdFile?.load()
            cmdFile?.cmdLines?.forEach { line ->
                withContext(Dispatchers.JavaFx) {
                    txtCommands.appendText(line)
                    txtCommands.appendText("\n")
                }
            }
        }
    }


    fun show(ownerStage: Stage) = Stage().apply {
        initOwner(ownerStage)
        title = MainFragment.APP_NAME
        icons?.add(Image(R.drawable.settings))
        isResizable = false
        scene = Scene(root)
        centerOnScreen()
        show()
        stage = this
    }

    override fun handle(event: ActionEvent?) {
        val node = event?.source as? Node
        when (node?.id) {
            R.id.btnCancel -> {
                stage?.close()
            }
            R.id.btnOk -> {
                val name = txtName.text
                val lines = txtCommands.text.split('\n')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && it.isNotBlank() }

                if (lines.isEmpty()) {
                    Toast.makeText("Input data is empty!")
                    return
                }
                if (action == ACTION_INSERT) {
                    onIO {
                        cmdFile = CmdFileRepository.add(name, lines)
                        onMain {
                            stage?.close()
                        }
                    }
                } else if (action == ACTION_EDIT && cmdFile != null) {
                    onIO {
                        val newFile = File(getHome(), name.dotBat())
                        val oldFile = File(cmdFile!!.path)
                        if (newFile.exists() && cmdFile?.name != name) {
                            Toast.makeText("Filename \"${newFile.name}\" is existed!")
                        } else if (newFile.exists()) {
                            runCatching {
                                CmdFileRepository.add(name, lines)
                            }
                            onMain {
                                stage?.close()
                            }
                        } else {
                            runCatching {
                                oldFile.delete()
                                CmdFileRepository.add(name, lines)
                            }
                            onMain {
                                stage?.close()
                            }
                        }
                    }
                }
            }
            else -> {
                Log.d("Id unidentified!")
            }
        }
    }
}