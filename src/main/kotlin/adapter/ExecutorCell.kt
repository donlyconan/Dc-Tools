package adapter

import R
import base.extenstion.id
import base.extenstion.newFXMLLoader
import base.logger.Log
import base.view.CellRender
import data.model.Command
import data.model.Executor
import data.repository.CommandListRepository
import duplicate
import findIndex
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tornadofx.getProperty
import view.CommandDialog
import java.sql.Time

class ExecutorCell(private val onItemClick:(Int) -> Unit) :
    CellRender<Executor>(), EventHandler<ActionEvent> {

    class TimeDisplay(val time: Int) {
        override fun toString(): String  {
            if(time < 60) {
                return "$time sec"
            } else {
                return "${(time.toDouble()/60).toInt()} min"
            }
        }

        override fun equals(other: Any?): Boolean {
            if(other is Int) {
                return this.time == other
            } else if(other is TimeDisplay) {
                return other.time == this.time
            } else {
                return super.equals(other)
            }
        }
    }

    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_REDUCE = 1
        val timeDisplays = FXCollections.observableArrayList(
            TimeDisplay(0),
            TimeDisplay(5),
            TimeDisplay(10),
            TimeDisplay(15),
            TimeDisplay(30),
            TimeDisplay(60),
            TimeDisplay(120),
            TimeDisplay(300),
            TimeDisplay(600),
            TimeDisplay(1200),
            TimeDisplay(3600),
        )
    }
    lateinit var root: Parent
    @FXML
    lateinit var lbName: Label
    @FXML
    lateinit var btnAction: Button
    @FXML
    lateinit var cbTime: ComboBox<TimeDisplay>
    private var isProtected = false

    init {
        prefWidth = 0.0
        loadingUI()
    }

    private fun loadingUI() {
        val fxmlLoader =  FXMLLoader(javaClass.getResource(R.layout.item_run_file_info))
        fxmlLoader.setController(this)
        root = fxmlLoader.load()
        cbTime.items = timeDisplays
        cbTime.selectionModel.selectedItemProperty().addListener { n, oval, nval ->
            if(!isProtected) {
                item.delay = nval.time
            }
        }
    }

    override fun updateItem(item: Executor?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            root.prefWidth(listView.width)
            val indexOf = cbTime.items.findIndex { it.time == item.delay }
            isProtected = true
            if(indexOf != -1) {
                cbTime.selectionModel.select(indexOf)
            } else {
                cbTime.selectionModel.select(0)
            }
            isProtected = false
            lbName.text = item.name
            graphic = root
        } else {
            graphic = null
        }
    }

    override fun handle(event: ActionEvent?) {
        onItemClick.invoke(index)
    }

}