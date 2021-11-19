package base.view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


public class CellRender<T> extends ListCell<T> implements DragAndDropListener {
    private ListCell thisCell;
    private String currentStyle;

    public CellRender() {
        thisCell = this;
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);
        setOnDragDetected(this::onDragDetected);
        setOnDragOver(this::onDragOver);
        setOnDragEntered(this::onDragEntered);
        setOnDragExited(this::onDragExited);
        setOnDragDropped(this::onDragDropped);
        setOnDragDetected(this::onDragDetected);
        currentStyle = getStyle();
    }

    private boolean isSelectedOneItem() {
        return getListView().getSelectionModel().getSelectedItems().size() == 1 && getItem() != null;
    }

    @Override
    public void onDragDetected(MouseEvent event) {
        if (getItem() != null && getGraphic() != null && isSelectedOneItem()) {
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getListView().getItems().indexOf(getItem())));
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.WHITE);
            WritableImage writableImage = new WritableImage((int) getWidth(), (int) getHeight());
            WritableImage image = getGraphic().snapshot(parameters, writableImage);
            dragboard.setDragView(image);
            dragboard.setContent(content);
            event.consume();
        }
    }

    @Override
    public void onDragOver(DragEvent event) {
        if (event.getGestureSource() != thisCell &&
                event.getDragboard().hasString() && isSelectedOneItem()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    @Override
    public void onDragEntered(DragEvent event) {
        if (event.getGestureSource() != thisCell &&
                event.getDragboard().hasString() && isSelectedOneItem()) {
            setStyle("-fx-border-color: #f07e00; -fx-border-width:1px; -fx-border-radius:2px;");
            setOpacity(0.6);
        }
    }

    @Override
    public void onDragExited(DragEvent event) {
        if (event.getGestureSource() != thisCell &&
                event.getDragboard().hasString()) {
            setStyle(currentStyle);
            setOpacity(1.0);
        }
    }

    @Override
    public void onDragDropped(DragEvent event) {
        if (getItem() != null &&  isSelectedOneItem()) {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                ObservableList<T> items = getListView().getItems();
                int draggedIdx = Integer.valueOf(db.getString());
                int thisIdx = items.indexOf(getItem());
                T draggedItem = items.get(draggedIdx);
                items.remove(draggedIdx);
                items.add(thisIdx, draggedItem);
                List<T> itemscopy = new ArrayList<>(getListView().getItems());
                getListView().getItems().setAll(itemscopy);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
            getListView().refresh();
        }
    }

    @Override
    public void onDragDone(DragEvent event) {
        event.consume();
    }

}


