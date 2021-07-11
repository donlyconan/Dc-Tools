package base.extension

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.filechooser.FileSystemView


fun getFileIcon(file: File): Image {
    val imageIcon =  FileSystemView.getFileSystemView().getSystemIcon(file) as ImageIcon;
    val bufferedImage = imageIcon.image as? BufferedImage;
    return SwingFXUtils.toFXImage(bufferedImage, null);
}