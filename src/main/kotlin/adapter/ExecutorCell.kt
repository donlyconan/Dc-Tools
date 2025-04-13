package adapter

import R
import base.view.CellRender
import data.model.Executor
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class ExecutorCell : CellRender<Executor>(), EventHandler<ActionEvent> {
    lateinit var root: Parent

    @FXML
    lateinit var lbName: Label

    @FXML
    lateinit var imgIcon: ImageView

    var listener: ActionClickListener? = null

    init {
        prefWidth = 0.0
    }

    private fun loadingUI(): Parent {
        val fxmlLoader = FXMLLoader(javaClass.getResource(R.layout.item_run_file_info))
        fxmlLoader.setController(this)
        return fxmlLoader.load()
    }

    override fun updateItem(item: Executor?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            root = loadingUI()
            root.prefWidth(listView.width)
            lbName.text = item.name
            imgIcon.image = Image(R.drawable.ic_execute)
            graphic = root
        } else {
            graphic = null
        }
    }

    override fun handle(event: ActionEvent?) {
        listener?.onActionClick(item)
    }

    interface ActionClickListener {
        fun onActionClick(item: Executor)
    }

}