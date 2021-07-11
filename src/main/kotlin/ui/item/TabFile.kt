package ui.item

import R
import base.settings.Settings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import javafx.application.Platform
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.text.Font
import kotlinx.coroutines.*
import sun.awt.CharsetString
import tornadofx.Fragment
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.CharsetEncoder
import java.util.concurrent.TimeUnit


class TabFile(val file: File) : Fragment(file.nameWithoutExtension) {
    companion object {
        val TIME_DELAY: Long = 500

        private val listCharsets = arrayListOf(
            Charsets.UTF_8.name(),
            Charsets.UTF_16.name(),
            Charsets.UTF_16BE.name(),
            Charsets.UTF_16LE.name(),
            Charsets.UTF_32.name(),
            Charsets.UTF_32BE.name(),
            Charsets.UTF_32LE.name(),
        )
        private val listFonts = Font.getFamilies()

        private val listFontSize = arrayListOf(10, 11, 12, 13, 14, 15, 16, 18, 20, 22, 24, 28, 32, 36, 42, 48)
    }

    private val compositeDisposable = CompositeDisposable()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override val root by fxml<BorderPane>(R.layout.item_tab_file)
    private val txtArea by fxid<TextArea>()
    private val imgStatus by fxid<ImageView>()
    private val cbCharset by fxid<ComboBox<String>>()
    private val cbFontSize by fxid<ComboBox<String>>()
    private val cbFont by fxid<ComboBox<String>>()
    private val settings = Settings.getInstance()
    var flagChange = false


    init {
        updateContent(file)
        txtArea.textProperty().addListener { observable, oldValue, newValue ->
            if (!flagChange) {
                isContentChanged = true
                subject.onNext(newValue)
            }
        }

        txtArea.font = Font.font(settings.fontFamily, settings.fontSize)

        cbCharset.items.addAll(listCharsets)
        cbFont.items.addAll(Font.getFamilies())
        cbFontSize.items.addAll(listFontSize.map { it.toString() })

        cbFont.selectionModel.select(settings.fontFamily)
        cbFontSize.selectionModel.select(settings.fontSize.toInt().toString())
        cbCharset.selectionModel.select(settings.charset)

        cbFont.selectionModel.selectedItemProperty()
            .addListener { o, old, new -> updateFontFamily(new) }
        cbFontSize.selectionModel.selectedItemProperty()
            .addListener { o, old, new -> updateFontSize(new) }
        cbCharset.selectionModel.selectedItemProperty()
            .addListener { o, old, new -> updateCharset(new) }
    }

    private val subject = PublishSubject.create<String>()
        .apply {
            debounce(TIME_DELAY, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .subscribe({ text ->
                    file.writeText(text, Charsets.UTF_8)
                    Platform.runLater { isContentChanged = false }
                }, { error -> print(error.message) })
                .apply { compositeDisposable.add(this) }

        }

    var isContentChanged: Boolean = false
        set(value) {
            field = value
            updateStatus()
        }

    private fun updateContent(file: File) {
        txtArea.isEditable = false
        flagChange = true
        val charset = Charset.forName(settings.charset)
        coroutineScope.launch {
            val content = file.readText(charset)
            txtArea.text = content
            flagChange = false
            txtArea.isEditable = true
        }
    }

    fun updateFontSize(newValue: String) {
        val font = txtArea.font
        txtArea.font = Font.font(font.family, newValue.toDouble())
        settings.fontSize = newValue.toDouble()
    }

    fun updateFontFamily(newValue: String) {
        val font = txtArea.font
        txtArea.font = Font.font(newValue, font.size)
        settings.fontFamily = newValue
    }

    fun updateCharset(newValue: String) {
        settings.charset = newValue
        updateContent(file)
    }


    private fun updateStatus() {
        if (!isContentChanged) {
            imgStatus.image = Image(R.drawable.ic_checked)
        } else {
            imgStatus.image = null
        }
    }


}