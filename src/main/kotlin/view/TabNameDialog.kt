package view

import R
import base.extenstion.fromLong
import base.logger.Log
import base.view.Toast
import data.model.Command
import data.repository.CmdFileRepository
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
import tornadofx.command
import java.io.File
import java.text.SimpleDateFormat


class TabNameDialog : BaseFragment(R.layout.dialog_group_name) {
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
        val existFile = CmdFileRepository.exist(name)
        if (name.isNotBlank() && !existFile) {
            func.invoke(name)
            close()
        } else if(existFile) {
            Toast.makeText("Tab name: $name is existed!")
        } else {
            Toast.makeText("Tabname is invalid!")
        }
    }
}