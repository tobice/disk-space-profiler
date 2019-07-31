package com.example.demo.app

import javafx.beans.property.ListProperty
import tornadofx.*

class SelectedNodeModel : ItemViewModel<Node>() {
    val childNodes: ListProperty<Node> = bind { item?.childNodes?.sortedByDescending { it.size }?.observable()?.toProperty() }
}
