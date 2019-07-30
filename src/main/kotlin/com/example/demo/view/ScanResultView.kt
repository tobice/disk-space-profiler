package com.example.demo.view

import com.example.demo.app.AppViewModel
import com.example.demo.app.Node
import javafx.collections.ObservableList
import tornadofx.*

class ScanResultView : View() {
    private val appViewModel : AppViewModel by inject()

//    val list : ObservableList<Node> = appViewModel.getSelectedNode().select { node -> node.childNodesForUi }.value

    override val root = vbox {
        label("Display files below")
        label(appViewModel.getSelectedNode().select { node -> node.sizeInBytesForUi })
//        vbox {
//            bindChildren(appViewModel.getSelectedNode().select { node -> node.childNodesForUi }) { node: Node ->
//                label("${node.file} + (${node.sizeInBytesForUi} B)")
//            }
//        }
    }
}
