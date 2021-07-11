package base.view

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

class DragResizer(private val region: Region) {
    private var y = 0.0
    private var initMinHeight = false
    private var dragging = false
    protected fun mouseReleased(event: MouseEvent?) {
        dragging = false
        region.cursor = Cursor.DEFAULT
    }

    protected fun mouseOver(event: MouseEvent) {
        if (isInDraggableZone(event) || dragging) {
            region.cursor = Cursor.S_RESIZE
        } else {
            region.cursor = Cursor.DEFAULT
        }
    }

    protected fun isInDraggableZone(event: MouseEvent): Boolean {
        return event.y > region.height - RESIZE_MARGIN
    }

    protected fun mouseDragged(event: MouseEvent) {
        if (!dragging) {
            return
        }
        val mousey: Double = event.y
        val newHeight: Double = region.minHeight + (mousey - y)
        region.minHeight = newHeight
        y = mousey
    }

    protected fun mousePressed(event: MouseEvent) {

        // ignore clicks outside of the draggable margin
        if (!isInDraggableZone(event)) {
            return
        }
        dragging = true

        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight())
            initMinHeight = true
        }
        y = event.getY()
    }

    companion object {
        /**
         * The margin around the control that a user can click in to start resizing
         * the region.
         */
        private const val RESIZE_MARGIN = 5
        fun makeResizable(region: Region) {
            val resizer = DragResizer(region)
            region.setOnMousePressed {
                resizer.mousePressed(it)
            }
            region.setOnMouseDragged {
                resizer.mouseDragged(it)
            }
            region.setOnMouseMoved {
                resizer.mouseOver(it)
            }
            region.setOnMouseReleased {
                resizer.mouseReleased(it)
            }
        }
    }

}