package base.view

import base.extenstion.onMain
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx


object Toast {
    const val SHORT_TIME = 2000
    const val LONG_TIME = 5000
    val job = Job()
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)
    val stage = Stage()
    var ownerStage: Stage? = null

    init {
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.isResizable = false
        stage.isAlwaysOnTop = true
    }

    @Synchronized
    fun makeText(
        toastMsg: String?,
        toastDelay: Int = SHORT_TIME,
        fadeInDelay: Int = 300,
        fadeOutDelay: Int = 300
    ) = scope.onMain {
        if (stage.isShowing) {
            stage.close()
        }
        val text = Text(toastMsg)
        text.font = Font.font("Verdana", 14.0)
        val root = StackPane(text)
        root.style = "-fx-background-radius: 5px; -fx-background-color: #cccccc; " +
                "-fx-padding: 10px 15px 10px 15px;"
        root.opacity = 0.5
        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT
        stage.scene = scene
        // ✅ Định vị dưới cùng của ownerStage
        ownerStage?.let {
            val ownerX = it.x
            val ownerY = it.y
            val ownerWidth = it.width
            val ownerHeight = it.height
            val toastWidth = scene.width
            val toastHeight = scene.height

            stage.x = ownerX + (ownerWidth - toastWidth) / 2
            stage.y = ownerY + ownerHeight - toastHeight - 50 // 50px cách đáy
        }
        stage.show()
        val fadeInTimeline = Timeline()
        val fadeInKey1 =
            KeyFrame(Duration.millis(fadeInDelay.toDouble()), KeyValue(stage.scene.root.opacityProperty(), 1))
        fadeInTimeline.keyFrames.add(fadeInKey1)
        fadeInTimeline.onFinished = EventHandler { ae: ActionEvent? ->
            scope.launch(Dispatchers.JavaFx) {
                delay(toastDelay.toLong())
                val fadeOutTimeline = Timeline()
                val fadeOutKey1 = KeyFrame(
                    Duration.millis(fadeOutDelay.toDouble()),
                    KeyValue(stage.scene.root.opacityProperty(), 0)
                )
                fadeOutTimeline.keyFrames.add(fadeOutKey1)
                fadeOutTimeline.onFinished =
                    EventHandler { aeb: ActionEvent? -> stage.close() }
                fadeOutTimeline.play()
            }
        }
        job.cancelChildren()
        scope.launch(Dispatchers.JavaFx) {
            delay((toastDelay.toFloat() * 1.5).toLong())
            onMain {
                if (stage.isShowing) {
                    stage.close()
                }
            }
        }
        fadeInTimeline.play()
    }

}