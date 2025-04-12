package base.extenstion

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

fun <T: Any> T.loadFxml(layoutId: String) = lazy {
    val loader = FXMLLoader(javaClass.getResource(layoutId))
    loader.setController(this)
    loader.load<Parent>()
}
