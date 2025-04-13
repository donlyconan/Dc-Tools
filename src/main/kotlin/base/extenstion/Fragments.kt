package base.extenstion

import javafx.scene.Node
import tornadofx.Fragment

fun Fragment.showBelow(node: Node) {
    val screen = node.localToScreen(0.0, node.boundsInLocal.height)
    openModal(
        escapeClosesWindow = true,
        resizable = false,
        block = false,
        owner = node.scene.window
    )
    root.scene.window.x = screen.x
    root.scene.window.y = screen.y
}
