package ui.view

import R
import base.extension.*
import base.settings.Settings
import base.view.Toast
import com.google.gson.Gson
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.text.Font
import javafx.stage.DirectoryChooser
import tornadofx.*
import ui.cell.NoteCell
import ui.item.TabFile
import java.io.File
import java.lang.Exception
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EditorScreen : Fragment("File Manager"), EventHandler<MouseEvent> {
    override val root by fxml<BorderPane>(R.layout.fragment_editor)

    private val lstOfNote by fxid<ListView<File>>()
    private val tabpane by fxid<TabPane>()
    private val btnAdd by fxid<ImageView>()
    private val txtSearch by fxid<TextField>()
    private val imgClose by fxid<ImageView>()
    private val settings = Settings.getInstance()

    override fun onCreate() {
        super.onCreate()
        btnAdd.image = Image(R.drawable.ic_add)
        try {
            checkLocalSetupData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        btnAdd.onMouseClicked = this
        lstOfNote.setCellFactory { createNoteCell() }
        openTabs(settings.getListPaths())
        tabpane.selectionModel.selectedItemProperty().addListener { _, _, tab ->
            settings.currentTabFile = (tab.userData as File).absolutePath
        }
        imgClose.setOnMouseClicked { checkLocalSetupData() }
        txtSearch.textProperty().addListener { _, _, new ->
            if (new.isNotEmpty()) {
                val newList = lstOfNote.items
                    .filter { it.name.toLowerCase().contains(new.toLowerCase()) }
                lstOfNote.items = FXCollections.observableList(newList)
            } else {
                checkLocalSetupData()
            }
        }
    }

    private fun createNoteCell(): NoteCell {
        val noteCell = NoteCell()
        noteCell.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY && noteCell.item != null) {
                onItemClick(noteCell.index, noteCell.item)
            }
        }
        return noteCell
    }

    private fun checkLocalSetupData() {
        if (settings.folderOfListFile != null
            && File(settings.folderOfListFile!!).exists()
        ) {
            loadData()
        } else {
            val directoryChooser = DirectoryChooser()
            directoryChooser.title = "Select a Folder"
            val file = directoryChooser.showDialog(primaryStage)
            settings.folderOfListFile = file.absolutePath
            loadData()
        }
    }


    private fun loadData() {
        val file = File(settings.folderOfListFile!!)
        val listFiles = file.getListFiles()
        if (listFiles != null) {
            lstOfNote.items.addAll(listFiles)
        }
    }


    fun onItemClick(position: Int, file: File) {
        val tab = tabpane.tabs.find {
            val tabFile = it.userData as? File
            file.equalsWithPath(tabFile)
        }
        if (tab != null) {
            tab.text = file.nameWithoutExtension
            tab.select()
        } else {
            addNewTab(file)
        }
    }

    private fun addNewTab(file: File, isSelected: Boolean = true) {
        val item = TabFile(file)
        tabpane.add(item)
        val newTab = tabpane.tabs.last()
        newTab.userData = file
        newTab.text = file.nameWithoutExtension
        newTab.isClosable = true

        if (isSelected) {
            newTab.select()
        }
        GlobalScope.launch(Dispatchers.IO) {
            val listTabs = tabpane.tabs
                .map { (it.userData as File).absolutePath }
            settings.openTabs = Gson().toJson(listTabs)
        }
    }

    private fun openTabs(paths: List<String>) {
        for (path in paths) {
            val file = lstOfNote.items.find { it.equalsWithPath(path) }
            val isSelected = path == settings.currentTabFile
            if (file != null) {
                addNewTab(file, isSelected)
            }
        }
    }

    override fun handle(event: MouseEvent?) {
        when (event?.source) {
            btnAdd -> {
                val textField = textfield {
                    promptText = "Enter filename..."
                    text = settings.getDir()?.createDefaultName()
                    prefWidth = 290.0
                }

                val dialog = Dialog<ButtonType>().apply {
                    title = "Add file"
                    width = 200.0
                    dialogPane.buttonTypes.addAll(
                        ButtonType.OK, ButtonType.CANCEL
                    )
                    (dialogPane.scene.window as Stage)
                        .icons
                        .add(Image(R.drawable.settings))
                }

                dialog.dialogPane.content = vbox {
                    add(textField)
                    paddingAll = 5.0
                }

                val result = dialog.showAndWait()
                if (result.get() == ButtonType.OK) {
                    val file = createNFFile(settings.getDir(), textField.text)
                    if (file.exists()) {
                        addNewTab(file)
                        lstOfNote.items.add(file)
                        lstOfNote.refresh()
                    }
                }
            }
            else -> {
                // TODO
            }
        }
    }

}