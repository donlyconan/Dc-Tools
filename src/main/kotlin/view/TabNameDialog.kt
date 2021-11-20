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
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import tornadofx.Fragment
import java.io.File
import java.text.SimpleDateFormat


class TabNameDialog : Fragment(MainFragment.APP_NAME) {
    override val root: Parent by fxml(R.layout.dialog_group_name)
    private val tabName by fxid<TextField>()
    private val btnOk by fxid<Button>()


    init {
        tabName.setOnKeyPressed {
            if(it.code == KeyCode.ENTER) {
                btnOk.onAction?.handle(null)
            }
        }
    }

    fun show(ownerStage: Stage) = Stage().apply {
        initOwner(ownerStage)
        title = this@TabNameDialog.title
        icons?.add(Image(R.drawable.settings))
        isResizable = false
        scene = Scene(root)
        centerOnScreen()
        show()
    }

    fun setOnAction(func: (String) -> Unit) = btnOk.setOnAction {
        val name = tabName.text
        if (name?.length ?: 0 > 0) {
            func.invoke(name)
            close()
        } else {
            Toast.makeText("Tabname is invalid!").play()
        }
    }
}