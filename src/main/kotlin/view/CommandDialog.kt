package view

import R
import base.extenstion.fromLong
import base.logger.Log
import base.view.Toast
import data.model.Command
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.Fragment
import java.io.File
import java.text.SimpleDateFormat


// TODO Xu ly bai toan return thay doi filetype
class CommandDialog private constructor(title: String) : Fragment(title), EventHandler<ActionEvent> {
    companion object {
        const val ACTION_INSERT = "Insert command line"
        const val ACTION_EDIT = "Edit command line"

        val sDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

        fun create(title: String): CommandDialog {
            return CommandDialog(title)
        }

        fun create(title: String, command: Command): CommandDialog {
            return CommandDialog(title, ACTION_EDIT, command)
        }
    }

    override val root: Parent by fxml(R.layout.fragment_input_command)
    private val txtName by fxid<TextField>()
    private val txtCommands by fxid<TextArea>()
    private val txtModified by fxid<Label>()
    private val rdExecutable by fxid<RadioButton>()
    private val rdScript by fxid<RadioButton>()
    private val rdGroup by fxid<ToggleGroup>()
    private var stage: Stage? = null
    private val action = ACTION_INSERT
    var onDismissListener: OnDismissListener? = null
    private var command: Command? = null
    private var rootFolder = File(MainFragment.ROOT_FOLDER)

    private constructor(title: String, action: String, command: Command) : this(title) {
        this.command = command
        txtModified.text = sDateFormat.fromLong(System.currentTimeMillis())
        if (action == ACTION_EDIT) {
            initSetUp()
        }
    }

    private fun initSetUp() {
        Log.d("initSetUp: $command")
        txtName.text = command?.name
        txtCommands.text = command?.file?.readText()
        rdExecutable.isSelected = command?.isExecutable ?: true
        rdScript.isSelected = !rdExecutable.isSelected
        txtModified.text = sDateFormat.fromLong(command!!.modified)
    }

    fun show(ownerStage: Stage) = Stage().apply {
        initOwner(ownerStage)
        title = this@CommandDialog.title
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
                onDismissListener?.onClickCancel()
            }
            R.id.btnOk -> {
                val name = txtName.text
                val type = if (rdExecutable.isSelected) Command.EXT_BAT else Command.EXT_SCT
                val file = File(rootFolder, "$name.$type")
                if (action == ACTION_INSERT) {
                    val isReplaced = file.extension != command?.file?.extension
                    val isRootName = file.name.equals(command?.file?.name)
                    if (name.isEmpty()) {
                        Toast.makeText(null, "Filename is invalid!").play()}
                    else if (file.exists() && !isRootName) {
                        Toast.makeText(null, "\"$name.$type\" is existed!").play()
                    } else {
                        if(isReplaced) {
                            command?.file?.renameTo(file)
                        }
                        file.createNewFile()
                        file.writeText(txtCommands.text)
                        command = Command(file)
                        onDismissListener?.onClickOk(command!!)
                        stage?.close()
                    }
                } else {
                    if (file.exists() && !file.name.equals(command?.file?.name)) {
                        Toast.makeText(null, "Filename \"${file.name}\" is existed!").play()
                    } else if (name.isEmpty()) {
                        Toast.makeText(null, "Filename is invalid!").play()
                    } else {
                        file.createNewFile()
                        file.writeText(txtCommands.text)
                        command = Command(file)
                        onDismissListener?.onClickOk(command!!)
                        stage?.close()
                    }
                }
            }
            else -> {
                Log.d("Id unidentified!")
            }
        }
    }


    interface OnDismissListener {
        fun onClickCancel()
        fun onClickOk(command: Command)
    }
}