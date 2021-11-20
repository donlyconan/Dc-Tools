package base.extenstion

import javafx.fxml.FXMLLoader

fun newFXMLLoader(any: Any, layoutId: String) = FXMLLoader(any.javaClass.getResource(layoutId))
