package view

import R
import base.extenstion.fromLong
import base.logger.Log
import base.view.Toast
import data.model.Command
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.Fragment
import tornadofx.selectedValueProperty
import java.io.File
import java.text.SimpleDateFormat


class ComposerDialog: Fragment, EventHandler<ActionEvent> {
    companion object {
        const val ACTION_INSERT = "Insert command line"
        const val ACTION_EDIT = "Edit command line"

        val sDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa")

        fun create(): ComposerDialog {
            return ComposerDialog()
        }

        fun create(command: Command): ComposerDialog {
            return ComposerDialog(ACTION_EDIT, command)
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
    private var action = ACTION_INSERT
    var onDismissListener: OnDismissListener? = null
    private var command: Command? = null
    private var rootFolder = File(MainFragment.ROOT_FOLDER)

    private constructor() {
        txtModified.text = sDateFormat.fromLong(System.currentTimeMillis())
    }


    private constructor(action: String, command: Command) : this() {
        this.command = command
        this.action = action
        if (action == ACTION_EDIT) {
            txtModified.text = sDateFormat.fromLong(command.file.lastModified())
            initSetUp()
        }
        rdGroup.selectedToggleProperty().addListener(listener)
    }

    private val listener = object : ChangeListener<Toggle> {

        override fun changed(observable: ObservableValue<out Toggle>?, oldValue: Toggle?, newValue: Toggle?) {
            Log.d("ChangeListener: ")
        }

    }

    private fun getSelectedType() = if (rdExecutable.isSelected) Command.EXT_CMD else Command.EXT_SCT

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
        initStyle(StageStyle.UNIFIED)
        title = MainFragment.APP_NAME
        icons?.add(Image(R.drawable.settings))
        scene = Scene(root)
        isResizable = false
        centerOnScreen()
        show()
        stage = this
    }

    override fun handle(event: ActionEvent?) {
        val node = event?.source as? Node
        when (node?.id) {
            R.id.btnCancel -> {
                stage?.close()
                onDismissListener?.onNegativeClick()
            }
            R.id.btnOk -> {
                val name = txtName.text
                val type = getSelectedType()
                val file = File(rootFolder, "$name.$type")
                val text = txtCommands.text

                if (name.isEmpty() || text.isEmpty()) {
                    Toast.makeText("Input data is empty!").play()
                    return
                }
                if (action == ACTION_INSERT) {
                    if (file.exists()) {
                        Toast.makeText("File \"${file.name}\" is existed!")
                    } else {
                        addFile(file)
                        stage?.close()
                    }
                } else if (action == ACTION_EDIT) {
                    if (file.exists() && command?.file?.absolutePath != file?.absolutePath) {
                        Toast.makeText("Filename \"${file.name}\" is existed!").play()
                        return
                    } else if (file.exists()) {
                        editFile(command!!.file)
                        stage?.close()
                    } else {
                        command?.file?.delete()
                        addFile(file)
                        stage?.close()
                    }
                }
            }
            else -> {
                Log.d("Id unidentified!")
            }
        }
    }

    private fun addFile(file: File) {
        file.createNewFile()
        file.writeText(txtCommands.text)
        val command = Command(file)
        onDismissListener?.onPositiveClick(command)
    }

    private fun editFile(file: File) {
        file.writeText(txtCommands.text)
        command = Command(file)
        onDismissListener?.onPositiveClick(command!!)
    }


    interface OnDismissListener {
        fun onNegativeClick()
        fun onPositiveClick(command: Command)
    }
}