package view

import R
import adapter.CommandItemCell
import base.extenstion.id
import base.extenstion.node
import base.extenstion.runOnMainThread
import base.view.Toast
import data.model.Command
import data.repository.CommandListRepository
import data.repository.CommandListRepositoryImpl
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.stage.StageStyle
import base.logger.Log
import base.manager.ProcessManager
import base.observable.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornadofx.View
import tornadofx.close
import tornadofx.select
import java.io.File


class MainFragment : View("Main"), EventHandler<ActionEvent>,
    CommandTabFragment.OnClickListener,
    Observable<List<Command>> {

    companion object {
        val ROOT_FOLDER = "C:\\Users\\${System.getProperty("user.name")}\\Documents\\tools\\"
        const val APP_NAME = "DC Tools"
        const val UNTITLED = "Untitled"
    }

    override val root by fxml<BorderPane>(R.layout.fragment_main)
    private val lvStatements by fxid<ListView<Command>>()
    private val tabPane by fxid<TabPane>()
    private lateinit var repository: CommandListRepository
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    init {
        val file = File(ROOT_FOLDER)
        repository = CommandListRepositoryImpl(file)
        repository.subject.observe(this)
        lvStatements.setCellFactory { createCellFactory() }
        lvStatements.selectionModel.selectionMode = SelectionMode.MULTIPLE
        tabPane.tabs.clear()
        tabPane.contextMenu.items.forEach { it.setOnAction(this::onMenuItemClicked) }
        coroutineScope.launch { repository.loadFromDisk() }
        initFrame()
    }

    private fun initFrame() {
        var xOffset = 0.0
        var yOffset = 0.0
        primaryStage.initStyle(StageStyle.UNDECORATED)
        primaryStage.widthProperty().addListener { obs, oldVal, newVal ->
            root.prefWidth = newVal.toDouble()
        }
        primaryStage.heightProperty().addListener { obs, oldVal, newVal ->
            root.prefHeight = newVal.toDouble()
        }
        root.onMousePressed = EventHandler<MouseEvent> { event ->
            xOffset = primaryStage.x - event.screenX
            yOffset = primaryStage.y - event.screenY;
        }
        root.onMouseDragged = EventHandler<MouseEvent> { event ->
            primaryStage.x = event.screenX + xOffset
            primaryStage.y = event.screenY + yOffset
        }
        primaryStage.fullScreenExitHint = null
        primaryStage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        primaryStage.icons.add(Image(R.drawable.settings))
    }

    private fun createCellFactory(): ListCell<Command> {
        val cellFactory = CommandItemCell(repository, coroutineScope)
        return cellFactory
    }

    override fun onChanged(values: List<Command>?) {
        Log.d("onChanged: ${values?.size}")
        coroutineScope.runOnMainThread {
            if (values != null) {
                lvStatements.items = FXCollections.observableList(values)
            } else {
                lvStatements.items.clear()
            }
        }
    }

    fun onMenuItemClicked(event: ActionEvent) {
        when (event.id) {
            R.id.itAddTab -> {
                addNewTab()
            }
            R.id.itCloseTab -> {
                val currentTab = tabPane.selectionModel.selectedItem
                currentTab?.close()
            }
            R.id.itClearAll -> {
                tabPane.tabs.clear()
            }
            else -> {
                println("Not found menu item!")
            }
        }
    }

    override fun handle(event: ActionEvent?) {
        val node = event?.source as? Node
        when (node?.id) {
            R.id.btnClose -> {
                primaryStage.close()
                ProcessManager.getInstance().killAll()
                Log.d("The Application is stopped")
            }
            R.id.btnZoom -> {
                primaryStage.fullScreenExitHint = null
                primaryStage.isFullScreen = !primaryStage.isFullScreen
            }
            R.id.btnHidden -> {
                primaryStage.isIconified = true
            }
            R.id.btnAdd -> {
                val dialog = CommandDialog.create(APP_NAME)
                dialog.onDismissListener = object : CommandDialog.OnDismissListener {
                    override fun onClickCancel() {}
                    override fun onClickOk(statement: Command) {
                        val index = 0// lvStatements.items.findIndex { it.id == statement.id }
                        if (index == -1) {
                            coroutineScope.launch { repository.add(statement) }
                        } else {
                            coroutineScope.launch { repository.update(statement) }
                        }
                    }
                }
                dialog.show(primaryStage)
            }
        }
    }

    override fun onClick(tab: CommandTabFragment, node: Node) {
        val listItems = lvStatements.selectionModel.selectedItems
        if (listItems.isEmpty()) {
            Toast.makeText(null, "No item added!").play()
        } else {
            tab.addCommands(listItems)
            lvStatements.selectionModel.clearSelection()
        }
    }

    private fun addNewTab(command: Command? = null): CommandTabFragment {
        val newTab = CommandTabFragment(repository)
        tabPane.add(newTab)
        newTab.onClickListener = this
        val tab = tabPane.tabs.last()
        tab?.text = ""
        tab?.select()
        tab.setOnClosed { newTab.onCloseTab() }
        return newTab
    }

}