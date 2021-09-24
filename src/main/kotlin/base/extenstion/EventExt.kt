package base.extenstion

import javafx.event.Event
import javafx.scene.Node

val Event.node get() = this.source as Node