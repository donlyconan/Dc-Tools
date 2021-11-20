package base.view;

import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

interface DragAndDropListener {
    void onDragDetected(MouseEvent event);
    void onDragOver(DragEvent event);
    void onDragEntered(DragEvent event);
    void onDragExited(DragEvent event);
    void onDragDropped(DragEvent event);
    void onDragDone(DragEvent event);
}
