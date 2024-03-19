package util

import project.FileBasedProject
import project.SdGenProject
import java.awt.Component

fun String.projectIfValid(): SdGenProject? {
    return toProject().takeIf { it.isValid() }
}

fun String.toProject(): SdGenProject = FileBasedProject(this)

fun SdGenProject.isValid(): Boolean {
    return try {
        getProjectData()
        true
    } catch (e: Exception) {
        println("not a valid project: $e")
        false
    }
}

fun projectChooser(
    parent: Component
): SdGenProject? = fileChooser(parent)?.path?.projectIfValid()