package project

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.toProject
import java.io.File

interface RecentProjectManager {
    fun getRecentProjects(): List<SdGenProject>

    fun saveRecentProject(project: SdGenProject)
}

object FileBasedRecentProjectManager: RecentProjectManager {
    private const val RECENT_PROJECTS_PATH = ".recentProjects"


    override fun getRecentProjects(): List<SdGenProject> {
        return getRecentProjectPaths().map { it.toProject() }
    }

    override fun saveRecentProject(project: SdGenProject) {
        val newRecentProjectPaths = getRecentProjectPaths() + project.pathToProject
        recentProjectsFile.printWriter().use { out ->
            out.println(Json.encodeToString(newRecentProjectPaths))
        }
    }

    private val recentProjectsFile by lazy {
        val recentProjectsFile = File(RECENT_PROJECTS_PATH)
        if (!recentProjectsFile.exists()) {
            recentProjectsFile.createNewFile()
        }
        recentProjectsFile
    }

    private fun getRecentProjectPaths(): Set<String> = runCatching {
        Json.decodeFromString<List<String>>(recentProjectsFile.readText())
    }.getOrNull()?.toSet() ?: setOf()

}