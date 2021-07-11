package ui.view

import base.extension.executeWithBat
import data.model.CommandLine
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import tornadofx.*
import ui.dialog.InputCommandDialog
import ui.cell.CommandLineCell
import java.text.SimpleDateFormat

class CommandScreen : Fragment(), EventHandler<ActionEvent> {

    companion object {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
    }

    override val root: BorderPane by fxml(R.layout.fragment_command_line)
    private val tableCommand: TableView<CommandLine> by fxid()
    private val btnAdd: Button by fxid()
    private val btnEdit: Button by fxid()
    private val btnDelete: Button by fxid()


    init {
        tableCommand.apply {
            column("Command Name", CommandLine::name).prefWidth(150)
            column("Description", CommandLine::description).remainingWidth()
            column("Created At", CommandLine::createdAt).prefWidth(80)
                .setCellFactory { createCellFactory() }
            column("Modified", CommandLine::updatedAt).prefWidth(80)
                .setCellFactory { createCellFactory() }
            column("Action", CommandLine::content)
                .prefWidth(60)
                .setCellFactory {
                CommandLineCell<CommandLine> { executeWithBat(it ?: "start cmd.exe") }
            }
            for (col in columns) {
                col.isSortable =  false
            }
            columnResizePolicy = SmartResize.POLICY
        }
        arrayOf(btnAdd, btnDelete, btnEdit).forEach { it.onAction = this }
    }

    private fun createCellFactory() = object : TableCell<CommandLine, Long>() {
        override fun updateItem(item: Long?, empty: Boolean) {
            super.updateItem(item, empty)
            if(item != null) {
                graphic = Label(simpleDateFormat.format(item!!))
            } else {
                graphic = null
            }
        }
    }

    override fun handle(event: ActionEvent?) {
        when (event?.source) {
            btnAdd -> {
                showDialogInput("Add Commands") {
                    tableCommand.items.add(it)
                }
            }
            btnEdit -> {
                if (tableCommand.selectedItem == null) {
                    return;
                }
                val index = tableCommand.selectionModel.selectedIndex
                val item = tableCommand.selectedItem

                showDialogInput("Edit Commands", item = item) {
                    tableCommand.items.set(index, it)
                }
                tableCommand.refresh()
            }
            btnDelete -> {
                if (tableCommand.selectedItem == null) {
                    return;
                }
                val item = tableCommand.selectedItem
                tableCommand.items.remove(item)
                tableCommand.refresh()
            }
        }
    }

    private fun showDialogInput(
        title: String,
        item: CommandLine? = null,
        listener: (item: CommandLine) -> Unit
    ) {
        val inputCommandDialog = InputCommandDialog(title, item)
        inputCommandDialog.apply {
            showAndWait()
                .filter { it.equals(ButtonType.OK) }
                .ifPresent {
                    listener.invoke(getItem())
                }
        }
    }

}