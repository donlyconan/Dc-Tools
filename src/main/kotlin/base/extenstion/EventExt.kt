package base.extenstion

import javafx.css.Styleable
import javafx.event.Event
import javafx.scene.Node

val Event.node get() = this.source as Node?

val Event.id get() = (source as Styleable).id
