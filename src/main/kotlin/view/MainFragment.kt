package view

import R
import adapter.CommandCell
import base.extenstion.id
import base.extenstion.onMain
import base.logger.Log
import base.manager.ProcessManager
import base.observable.Observable
import base.view.CTab
import base.view.DraggingTabPaneSupport
import base.view.exist
import data.model.CmdFile
import data.model.Command
import data.repository.CmdFileRepository
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import tornadofx.close
import tornadofx.select
import utils.onIO
import kotlin.system.exitProcess


class MainFragment : BaseFragment(R.layout.fragment_main), EventHandler<ActionEvent> {

    companion object {
        const val APP_NAME = "DC Tools"
    }

    private val lvStatements by fxid<ListView<CmdFile>>()
    private val tabPane by fxid<TabPane>()
    private val draggingTabPaneSupport: DraggingTabPaneSupport = DraggingTabPaneSupport()

    init {
        lvStatements.setCellFactory { createCellFactory() }
        lvStatements.selectionModel.selectionMode = SelectionMode.MULTIPLE
        tabPane.tabs.clear()
        tabPane.contextMenu.items.forEach { it.setOnAction(this::onMenuItemClicked) }
        draggingTabPaneSupport.addSupport(tabPane)
        initFrame()
        initData()
    }

    private fun initFrame() {
        primaryStage.title = APP_NAME
        primaryStage.setOnCloseRequest {
            Log.d("setOnCloseRequest: ")
            val alert = Alert(Alert.AlertType.CONFIRMATION).also {
                it.title = APP_NAME
                it.initOwner(primaryStage)
                it.buttonTypes.clear()
                it.buttonTypes.addAll(ButtonType.NO, ButtonType.YES)
                it.headerText = "Clean up before exiting the app?"
            }
            val option = alert.showAndWait()
            if (option.get() == ButtonType.YES) {
                ProcessManager.getInstance().killAll()
            }
            exitProcess(0)
        }
        primaryStage.icons.add(Image(R.drawable.settings))
    }

    private fun initData() = onIO {
        CmdFileRepository.load()
        withContext(Dispatchers.JavaFx) {
            lvStatements.items = CmdFileRepository.files
        }
        CmdFileRepository.startWatch()
    }

    private fun createCellFactory(): CommandCell {
        val cell = CommandCell()
        cell.listener = onMenuItemClickListener
        return cell
    }

    private val onMenuItemClickListener = object : CommandCell.OnMenuItemClickListener {
        override fun onItemClick(item: MenuItem?, cmdFile: CmdFile?) {
            println("onMenuItemClickListener: $cmdFile")
            if (cmdFile != null && !tabPane.tabs.exist(cmdFile.name)) {
                createNewTab(cmdFile)
            }
        }
    }

    fun onMenuItemClicked(event: ActionEvent) {
        when (event.id) {
            R.id.itAddTab -> {
                inputTabName()
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
                val dialog = ComposerDialog.create(null)
                dialog.show(primaryStage)
            }
        }
    }

    /**
     *  Add command new tab on tabpane
     */
    private fun inputTabName() {
        val dialog = TabNameDialog()
        dialog.setOnAction {
            if(!tabPane.tabs.exist(it)) {
                createNewTab(it)
            }
        }
        dialog.show(primaryStage)
    }

    private fun createNewTab(cmdFile: CmdFile): CmdFragment {
        val newTab = CmdFragment(cmdFile.name, cmdFile)
        val tab = CTab(cmdFile.name)
        tab.add(newTab)
        tabPane.tabs.add(tab)
        tab.select()
        tab.setOnClosed { newTab.onDestroy() }
        return newTab
    }

    private fun createNewTab(name: String): CmdFragment {
        val newTab = CmdFragment(name)
        val tab = CTab(name)
        tab.add(newTab)
        tabPane.tabs.add(tab)
        tab.select()
        tab.setOnClosed { newTab.onDestroy() }
        return newTab
    }

}