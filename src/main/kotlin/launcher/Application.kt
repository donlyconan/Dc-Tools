package launcher

import R
import base.logger.Log
import tornadofx.App
import tornadofx.importStylesheet
import view.MainFragment

class AppTools: App(MainFragment::class) {
    init {
        Log.d("The application is stated...")
        importStylesheet(R.style.style)
    }
}