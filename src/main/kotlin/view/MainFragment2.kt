package view

import BuildConfig
import R
import base.extenstion.id
import base.extenstion.node
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import tornadofx.Fragment
import tornadofx.clear
import tornadofx.select
import java.lang.StringBuilder

class MainFragment2 : Fragment(BuildConfig.APP_NAME){
    override val root: Parent by fxml<Parent>(R.layout.main_fragment_2)


    private val btnAppFir: Label by fxid(R.id.btnAppFir)
    private val btnAppSec: Label by fxid(R.id.btnAppSec)
    private val screen: StackPane by fxid(R.id.screen)
    private var btnCurrentButton: Node? = null
    private val screenMap = HashMap<String, Fragment>()

    init {
        val mainFragment = MainFragment()
        screenMap.put(R.id.btnAppFir, mainFragment)
        // Show first screen
        showApp(R.id.btnAppFir)

    }

    /**
     * Show app with app's id
     * @param appId
     */
    fun showApp(appId: String) {
        val fragment = screenMap.get(appId)
        screen.clear()
        if(fragment != null) {
            screen.add(fragment)
        }
    }

    fun onClick(event: MouseEvent) {
        val currentNode = event.node
        when(event.id) {
            R.id.btnAppFir -> {

            }
            R.id.btnAppSec -> {

            }
        }
        showApp(event.id)
        currentNode?.style = createBackgroundColor("#e8a261")
        btnCurrentButton?.style = createBackgroundColor("transparent")
        btnCurrentButton = currentNode
    }

    private fun createBackgroundColor(color: String) = "-fx-background-color:$color;"

}