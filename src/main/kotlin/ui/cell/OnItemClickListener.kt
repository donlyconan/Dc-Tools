package ui.cell

import javafx.scene.input.MouseEvent


interface OnItemClickListener {
    fun onItemClick(event: MouseEvent, position: Int)
}