package tools

import R
import tornadofx.App
import tornadofx.importStylesheet
import view.MainFragment

class AppTools: App(MainFragment::class) {
    init {
        importStylesheet(R.style.style)
    }
}