package com.example.demo.app

import javafx.beans.property.*
import javafx.collections.FXCollections
import tornadofx.*
import java.io.File

/** TODO(tobik): Add JavaDoc here. */
class AppViewModel : ViewModel() {
    enum class Status {
        WELCOME_SCREEN, SCANNING_IN_PROGRESS, SCANNING_FAILED, SCANNING_CANCELLED, SCANNING_FINISHED
    }
    val status = SimpleObjectProperty<Status>(Status.WELCOME_SCREEN)
    val targetDirectory = SimpleObjectProperty<File>()
    val runningSize = SimpleLongProperty()
    val directoryStack = SimpleListProperty<Node>(FXCollections.observableArrayList())
}