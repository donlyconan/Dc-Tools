package tools

import tornadofx.App
import tornadofx.importStylesheet
import ui.view.MainView

class ToolsApplication: App(MainView::class) {

    init {
        importStylesheet(R.style.style)
    }
}