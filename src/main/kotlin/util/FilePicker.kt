package util

import java.awt.Component
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter

enum class FileChooserSelectionMode {
    DIRECTORIES,
    FILES
}

private fun fileChooser(
    parent: Component,
    title: String,
    fileChooserSelectionMode: FileChooserSelectionMode,
    currentDirectoryPath: String = ".",
    fileFilter: FileFilter? = null,
    isMultiSelectionEnabled: Boolean = false
): Array<out File>? {
    val chooser = JFileChooser().apply {
        currentDirectory = File(currentDirectoryPath)
        dialogTitle = title
        this.isMultiSelectionEnabled = isMultiSelectionEnabled
        fileSelectionMode = when (fileChooserSelectionMode) {
            FileChooserSelectionMode.DIRECTORIES -> JFileChooser.DIRECTORIES_ONLY
            FileChooserSelectionMode.FILES -> JFileChooser.FILES_ONLY
        }
        this.fileFilter = fileFilter
        isAcceptAllFileFilterUsed = false

    }
    val result = chooser.showOpenDialog(parent)
    println(result)
    return if (isMultiSelectionEnabled) {
        chooser.selectedFiles
    } else {
        arrayOf(chooser.selectedFile)
    }
}

fun projectDirectoryChooser(
    parent: Component
): File? {
    return fileChooser(
        parent,
        "Project Directory",
        fileChooserSelectionMode = FileChooserSelectionMode.DIRECTORIES,
        fileFilter = null
    )?.firstOrNull()
}

fun imageFileChooser(
    parent: Component
): List<File>? {
    return fileChooser(
        parent,
        "Images",
        fileChooserSelectionMode = FileChooserSelectionMode.FILES,
        fileFilter = FileNameExtensionFilter("Images", "png", "jpg", "jpeg"),
        isMultiSelectionEnabled = true
    )?.toList()
}

fun fileChooser(
    parent: Component
): File? {
    return fileChooser(
        parent,
        "Project",
        fileChooserSelectionMode = FileChooserSelectionMode.FILES,
    )?.firstOrNull()
}