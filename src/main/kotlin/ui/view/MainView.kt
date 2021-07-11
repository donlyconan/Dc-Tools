package ui.view

import R
import base.view.DragResizer
import javafx.animation.TranslateTransition
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCombination
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.View
import tornadofx.rotate

class MainView : View("Main"), EventHandler<ActionEvent> {
    override val root by fxml<BorderPane>(R.layout.view_main)
    private val editorView by fragment<EditorScreen>()
    private val commandLineView by fragment<CommandScreen>()


    private val imgLogo by fxid<ImageView>()
    private val btnClose by fxid<Button>()
    private val btnHidden by fxid<Button>()
    private val btnZoom by fxid<Button>()
    private val btnFile by fxid<Button>()
    private val btnCommand by fxid<Button>()
    private val stackpane by fxid<StackPane>()
    private lateinit var mTraslateTransition: TranslateTransition

    private var xOffset = 0.0
    private var yOffset = 0.0
    private var isZoomOut = false


    init {
        primaryStage.initStyle(StageStyle.UNDECORATED)
        primaryStage.widthProperty().addListener { obs, oldVal, newVal ->
            editorView.root.prefWidth = newVal.toDouble()
        }
        primaryStage.heightProperty().addListener { obs, oldVal, newVal ->
            editorView.root.prefHeight = newVal.toDouble()
        }

        root.onMousePressed = EventHandler<MouseEvent> { event ->
            xOffset = primaryStage.x - event.screenX;
            yOffset = primaryStage.y - event.screenY;
        }
        root.onMouseDragged = EventHandler<MouseEvent> { event ->
            primaryStage.x = event.screenX + xOffset
            primaryStage.y = event.screenY + yOffset
        }
        primaryStage.fullScreenExitHint = null
        primaryStage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        imgLogo.image = Image(R.drawable.settings)
        mTraslateTransition = TranslateTransition(Duration.millis(400.0))

        stackpane.add(commandLineView)
        stackpane.add(editorView)
        editorView.onCreate()
        commandLineView.onCreate()
    }

    override fun handle(event: ActionEvent?) {

        when (event?.source) {
            btnClose -> primaryStage.close()
            btnZoom -> primaryStage.isFullScreen = !primaryStage.isFullScreen
            btnHidden -> {

            }

            btnFile -> {
                val root = editorView.root
                if (root != stackpane.getTop()) {
                    slideShow(root)
                }
            }

            btnCommand -> {
                val root = commandLineView.root
                if (root != stackpane.getTop()) {
                    slideShow(root)
                }
            }
        }
    }

    private fun slideShow(node: Pane) {
        node.toFront()
        mTraslateTransition.node = node
        mTraslateTransition.fromX = -node.width
        mTraslateTransition.toX = 0.0
        mTraslateTransition.play()
    }

    private fun StackPane.getTop(): Node? {
        return children.last()
    }


}