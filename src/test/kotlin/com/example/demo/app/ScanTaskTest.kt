package com.example.demo.app

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.any
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.testfx.framework.junit5.ApplicationTest
import java.nio.file.Files
import java.nio.file.Path

/** TODO(tobik): Add JavaDoc here. */
class ScanTaskTest : ApplicationTest() {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var scanTask: ScanTask
    private lateinit var runningSizeListenerMock: (Long) -> Unit

    @BeforeEach fun setUp() {
        runningSizeListenerMock = mock()

        createFile("file1", 1)
        createFile("file2", 2)
        createFile("file3", 3)
        mkdir("subdir1")
        createFile("subdir1/file1", 4)
        createFile("subdir1/file2", 5)
        createFile("subdir1/file3", 6)
        mkdir("subdir2")
        mkdir("subdir3")
        mkdir("subdir3/subdir4")
        createFile("subdir3/subdir4/file1", 7)

        scanTask = ScanTask(tempDir.toFile())
        scanTask.onRunningSizeUpdated = runningSizeListenerMock
        scanTask.run()
    }

    @Test fun rootNode_hasExpectedSize() {
        assertThat(scanTask.get().size, equalTo(28L))
    }

    @Test fun subdirNode_hasExpectedSize() {
        val subdirNode = scanTask.get().childNodes.find { it.file.endsWith("subdir1") }!!
        assertThat(subdirNode.size, equalTo(15L))
    }

    @Test fun runningSize_updatedForEachFile() {
        verify(runningSizeListenerMock, times(7)).invoke(any())
    }

    @Test fun runningSize_updatedWithTotalSize() {
        verify(runningSizeListenerMock).invoke(28L)
    }

    // TODO(tobik): Add tests for edge cases such as unreadable files / directories.
    // (That may be hard to simulate.)

    private fun mkdir(path: String) {
        tempDir.resolve(path).toFile().mkdir()
    }

    private fun createFile(path: String, size: Int) {
        val data = ByteArray(size) { 1 }
        Files.write(tempDir.resolve(path), data)
    }
}