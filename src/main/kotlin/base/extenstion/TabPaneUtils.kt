package base.extenstion

import javafx.scene.control.Tab
import javafx.scene.control.TabPane


fun TabPane.findTabById(id: String): Tab? {
    for (tab in tabs) {
        if(tab.id == id) {
            return tab
        }
    }
    return null
}