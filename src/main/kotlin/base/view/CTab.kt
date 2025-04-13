package base.view

import javafx.scene.control.Tab

class CTab(val title: String): Tab(title)

fun List<Tab>.exist(name: String): Boolean = any { tab ->
    (tab as? CTab)?.title == name
}