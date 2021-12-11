package launcher

import R
import base.logger.Log
import tornadofx.App
import tornadofx.importStylesheet
import view.MainFragment
import view.MainFragment2

class Application: App(MainFragment2::class) {
    init {
        Log.d("The application is stated...")
        importStylesheet(R.style.style)
    }
}