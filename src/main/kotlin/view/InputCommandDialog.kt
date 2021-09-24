package view

import data.model.Command
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.stage.Stage
import base.logger.Log
import tornadofx.Fragment
import java.time.LocalDate

class InputCommandDialog(title: String, private var actionName: String = ACTION_INSERT,
                         private val statement: Command? = null) : Fragment(title), EventHandler<ActionEvent> {
    companion object {
        const val ACTION_INSERT = "Insert command line"
        const val ACTION_EDIT = "Edit command line"
    }
    override val root: Parent by fxml(R.layout.fragment_input_command)
    private val datePicker by fxid<DatePicker>()
    private val txtCommandName by fxid<TextField>()
    private val txtCommands by fxid<TextArea>()
    private val lbTitle by fxid<Label>()
    private var stage: Stage? = null
    var onItemClickListener: OnItemClickListener? = null

    init {
        datePicker.value = LocalDate.now()
        lbTitle.text = actionName
        if(actionName == ACTION_EDIT && statement != null) {
//            txtCommands.text = statement.command
//            txtCommandName.text = statement.name
//            datePicker.value = LocalDate.ofEpochDay(statement.updatedAt)
        } else {
            actionName = ACTION_INSERT
        }
    }

    fun show(ownerStage: Stage) {
        stage = Stage()
        stage?.initOwner(ownerStage)
        stage?.title = this.title
        stage?.icons?.add(Image(R.drawable.settings))
        stage?.isResizable = false
        stage?.scene = Scene(root)
        stage?.centerOnScreen()
        stage?.show()
    }

    override fun handle(event: ActionEvent?) {
        val node = event?.source as? Node
        when(node?.id) {
            R.id.btnCancel -> {
                stage?.close()
                onItemClickListener?.onClickCancel()
            }
//            "btnOk" -> {
//                val item = Command(
//                    name = txtCommandName.text,
//                    command = txtCommands.text,
//                    updatedAt = datePicker.value.toEpochDay()
//                )
//                Log.p("Inserted item = $item")
//                if(!item.isValid()) {
//                    Toast.makeText(stage, "The data is inputted invalid!").play()
//                } else {
//                    if(actionName == ACTION_EDIT) {
//                        item.id = statement!!.id
//                    }
//                    stage?.close()
//                    onItemClickListener?.onClickOk(item)
//                }
//            }
            else -> {
                Log.p("Id unidentifier!")
            }
        }
    }

    interface OnItemClickListener {
        fun onClickCancel()
        fun onClickOk(statement: Command)
    }
}