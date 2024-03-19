package project

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ProjectData
import java.io.File

interface SdGenProject {
    val pathToProject: String
    fun getProjectData(): ProjectData

    fun setProjectData(projectData: ProjectData)
}

data class FileBasedProject(
    override val pathToProject: String
) : SdGenProject {

    private val projectFile = File(pathToProject)

    override fun getProjectData(): ProjectData {
        val projectFileString = projectFile.readText()
        return Json.decodeFromString<ProjectData>(projectFileString)
    }

    override fun setProjectData(projectData: ProjectData) {
        projectFile.printWriter().use {
            it.print(Json.encodeToString(projectData))
        }
    }

    companion object {
        fun create(pathToProjectFile: String, projectData: ProjectData): FileBasedProject {
            return FileBasedProject(pathToProjectFile).apply {
                setProjectData(projectData)
            }
        }
    }
}