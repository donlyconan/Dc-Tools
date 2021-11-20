package widgets

import javafx.scene.Node
import javafx.scene.layout.StackPane
import java.util.*

class StackView: StackPane() {
    val stack = Stack<Node>()
}