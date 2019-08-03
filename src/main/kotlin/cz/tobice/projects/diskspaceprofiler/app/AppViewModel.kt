package cz.tobice.projects.diskspaceprofiler.app

import javafx.beans.property.*
import javafx.collections.FXCollections
import tornadofx.*
import java.io.File

/**
 * ViewModel which holds the state of the whole app. It gets updated by the controller and rendered
 * by the views.
 */
class AppViewModel : ViewModel() {
    /** The overall app status. */
    enum class Status {
        WELCOME_SCREEN, SCANNING_IN_PROGRESS, SCANNING_FAILED, SCANNING_CANCELLED, SCANNING_FINISHED
    }
    val status = SimpleObjectProperty<Status>(Status.WELCOME_SCREEN)

    /** The target directory which should be scanned & analyzed. */
    val targetDirectory = SimpleObjectProperty<File>()

    /**
     * When a scan task is running, this contains the overall size of all data that have been found
     * so far. When the tasks finishes, this contains the overall total size of analyzed files.
     */
    val runningSize = SimpleLongProperty()

    /**
     * As the user explores the scanned directories, this contains the list of directories they
     * dived into. It does not contain the current selected directory.
     */
    val directoryStack = SimpleListProperty<Node>(FXCollections.observableArrayList())
}