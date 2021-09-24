package launcher

import R
import base.logger.Log
import tornadofx.App
import tornadofx.importStylesheet
import view.MainFragment

class Application: App(MainFragment::class) {
    init {
        Log.p("The application is stated...")
        importStylesheet(R.style.style)
    }
}