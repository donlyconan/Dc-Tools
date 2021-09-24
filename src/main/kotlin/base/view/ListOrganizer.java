package base.view;

import base.observable.Observable;
import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

interface DragAndDropListener {
    void onDragDetected(MouseEvent event);

    void onDragOver(DragEvent event);

    void onDragEntered(DragEvent event);

    void onDragExited(DragEvent event);

    void onDragDropped(DragEvent event);

    void onDragDone(DragEvent event);
}

public class ListOrganizer extends Application {
    private static final String PREFIX =
            "http://icons.iconarchive.com/icons/jozef89/origami-birds/72/bird";

    private static final String SUFFIX =
            "-icon.png";

    private static final ObservableList<String> birds = FXCollections.observableArrayList(
            "-black",
            "-blue",
            "-red",
            "-red-2",
            "-yellow",
            "s-green",
            "s-green-2"
    );

    private static final ObservableList<Image> birdImages = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        birds.forEach(bird -> birdImages.add(new Image(PREFIX + bird + SUFFIX)));

        ListView<String> birdList = new ListView<>(birds);
        birdList.setCellFactory(param -> new BirdCell());
        birdList.setPrefWidth(180);

        VBox layout = new VBox(birdList);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout));
        stage.show();
    }

    public static void main(String[] args) {
        launch(ListOrganizer.class);
    }


    private static class BirdCell extends ListCell<String> implements DragAndDropListener {
        private final ImageView imageView = new ImageView();
        private ListCell thisCell;

        public BirdCell() {
            thisCell = this;
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setAlignment(Pos.CENTER);
            setOnDragDetected(this::onDragDetected);
            setOnDragOver(this::onDragOver);
            setOnDragEntered(this::onDragEntered);
            setOnDragDropped(this::onDragDropped);
            setOnDragDetected(this::onDragDetected);

        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                imageView.setImage(birdImages.get(getListView().getItems().indexOf(item)));
                setGraphic(imageView);
            }
        }

        @Override
        public void onDragDetected(MouseEvent event) {
            if (getItem() != null) {
                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem());
                WritableImage image = imageView.snapshot(new SnapshotParameters(), null);
                dragboard.setDragView(image);
                dragboard.setContent(content);
                event.consume();
            }
        }

        @Override
        public void onDragOver(DragEvent event) {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        }

        @Override
        public void onDragEntered(DragEvent event) {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                setOpacity(0.5);
            }
        }

        @Override
        public void onDragExited(DragEvent event) {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                setOpacity(1);
            }
        }

        @Override
        public void onDragDropped(DragEvent event) {
            if (getItem() != null) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    ObservableList<String> items = getListView().getItems();
                    int draggedIdx = items.indexOf(db.getString());
                    int thisIdx = items.indexOf(getItem());
                    Image temp = birdImages.get(draggedIdx);
                    birdImages.set(draggedIdx, birdImages.get(thisIdx));
                    birdImages.set(thisIdx, temp);
                    items.set(draggedIdx, getItem());
                    items.set(thisIdx, db.getString());
                    List<String> itemscopy = new ArrayList<>(getListView().getItems());
                    getListView().getItems().setAll(itemscopy);
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        }

        @Override
        public void onDragDone(DragEvent event) {
            event.consume();
        }
    }


}