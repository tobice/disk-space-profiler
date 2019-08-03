package cz.tobice.projects.diskspaceprofiler.app

import javafx.beans.property.ListProperty
import tornadofx.*

/**
 * View model that holds the current selected directory displayed on the screen.
 *
 * <p>It is separated from the {@link AppViewModel} because a standalone {@link ItemViewModel} makes
 * it easier to wire up.
 */
class SelectedNodeModel : ItemViewModel<Node>() {
    /** All files & directories in the current directory sorted by size. */
    val sortedChildNodes: ListProperty<Node> = bind {
        item?.childNodes?.sortedByDescending { it.size }?.observable()?.toProperty()
    }
}
