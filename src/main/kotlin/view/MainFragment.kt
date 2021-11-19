package view

import R
import adapter.CommandCell
import base.extenstion.id
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
import javafx.scene.layout.BorderPane
import base.logger.Log
import base.manager.ProcessManager
import base.observable.Observable
import base.view.DraggingTabPaneSupport
import data.model.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornadofx.*
import java.io.File


class MainFragment : View(APP_NAME), EventHandler<ActionEvent>,
    CommandOperationFragment.OnClickListener,
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
    private lateinit var processManager: ProcessManager
    private val draggingTabPaneSupport: DraggingTabPaneSupport = DraggingTabPaneSupport()

    init {
        val file = File(ROOT_FOLDER)
        repository = CommandListRepositoryImpl(file)
        repository.subject.observe(this)
        lvStatements.setCellFactory { createCellFactory() }
        lvStatements.selectionModel.selectionMode = SelectionMode.MULTIPLE
        tabPane.tabs.clear()
        tabPane.contextMenu.items.forEach { it.setOnAction(this::onMenuItemClicked) }
        coroutineScope.launch { repository.loadFromDisk() }
        processManager = ProcessManager.getInstance()
        draggingTabPaneSupport.addSupport(tabPane)
        initFrame()
    }

    private fun initFrame() {
        primaryStage.title = APP_NAME
        primaryStage.setOnCloseRequest {
            Log.d("setOnCloseRequest: ")
            if (processManager.listProcessIds().isNotEmpty()) {
                val alert = Alert(Alert.AlertType.CONFIRMATION).also {
                    it.title = APP_NAME
                    it.initOwner(primaryStage)
                    it.buttonTypes.clear()
                    it.buttonTypes.addAll(ButtonType.NO, ButtonType.YES)
                    it.headerText = "Dọn dẹp trước khi thoát app?"
                }
                val option = alert.showAndWait()
                if (option.get() == ButtonType.YES) {
                    ProcessManager.getInstance().killAll()
                }
            }
        }
        primaryStage.icons.add(Image(R.drawable.settings))
    }

    private fun createCellFactory(): ListCell<Command> {
        val cellFactory = CommandCell(repository, coroutineScope)
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
            R.id.btnAdd -> {
                val dialog = CommandDialog.create()
                dialog.onDismissListener = object : CommandDialog.OnDismissListener {
                    override fun onNegativeClick() {}
                    override fun onPositiveClick(statement: Command) {
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

    override fun onAdded(lst: ListView<Executor>, node: Node) {
        val listItems = lvStatements.selectionModel.selectedItems
        if (listItems.isEmpty()) {
            Toast.makeText(null, "No item added!").play()
        } else {
            listItems.forEach { cmd ->
                val item = lst.items.find { it.fullName == cmd.fullName }
                if(item == null && cmd.isExecutable) {
                    lst.items.add(Executor(cmd.file))
                }
            }
            lvStatements.selectionModel.clearSelection()
        }
    }

    /**
     *  Add command new tab on tabpane
     */
    private fun addNewTab() {
        val dialog = TabNameDialog()
        dialog.setOnAction {
            val newTab = CommandOperationFragment(repository)
            newTab.onClickListener = this
            val tab = Tab(it)
            tab.add(newTab)
            tabPane.tabs.add(tab)
            tab.select()
            tab.setOnClosed { newTab.onClosed() }
        }
        dialog.show(primaryStage)
    }

}