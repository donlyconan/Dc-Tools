package base.extenstion

import R
import javafx.fxml.FXMLLoader

fun newFXMLLoader(any: Any, layoutId: String) = FXMLLoader(any.javaClass.getResource(layoutId))
