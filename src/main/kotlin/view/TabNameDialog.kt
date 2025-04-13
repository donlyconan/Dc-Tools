package view

import R
import base.view.Toast
import data.repository.CmdFileRepository
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.stage.Stage


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