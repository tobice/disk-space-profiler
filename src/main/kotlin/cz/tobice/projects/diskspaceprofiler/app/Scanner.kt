package cz.tobice.projects.diskspaceprofiler.app

import tornadofx.*
import java.io.File

/**
 * A factory helper for instantiating and starting the {@link ScanTask}.
 *
 * <p>While the encapsulation of running the scan task is only partial (it is still possible to
 * start the task outside of this scanner), it is sufficient to make the controller much easier to
 * test.
 */

class Scanner : Component(), ScopedInstance {
    fun createScanTask(rootDirectory: File): ScanTask {
        return ScanTask(rootDirectory)
    }

    fun startScanTask(scanTask: ScanTask) {
        Thread(scanTask).start()
    }
}