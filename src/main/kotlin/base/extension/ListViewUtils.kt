package base.extension

import javafx.scene.control.TablePosition
import javax.swing.text.html.ListView
import javax.xml.soap.Node

interface OnItemClickListener {
    fun onItemClick(position: Int, node: Node)
}

fun <T> List<T>.findIndex(compare: (item: T) -> Boolean): Int {
    for ((id, item) in withIndex()) {
        if (compare.invoke(item)) {
            return id
        }
    }
    return -1
}
