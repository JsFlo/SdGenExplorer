import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.application
import project.SdGenProject
import window.loadingstate.LoadingStateWindow
import window.projectselection.ProjectSelectionWindow
import window.projectstate.ProjectStateWindow

sealed class AppState {
    data object Loading : AppState()

    data class ProjectSelection(val creatingNewProject: Boolean) : AppState()

    data class ProjectState(val project: SdGenProject) : AppState()
}

fun main() = application {
    var appState: AppState by remember { mutableStateOf(AppState.Loading) }

    when (val currentAppState = appState) {
        AppState.Loading -> LoadingStateWindow(
            onFinishedLoading = {
                appState = AppState.ProjectSelection(
                    creatingNewProject = false
                )
            }
        )

        is AppState.ProjectSelection -> ProjectSelectionWindow(
            creatingNewProject = currentAppState.creatingNewProject,
            onProject = { project ->
                appState = AppState.ProjectState(project)
            },
            onCreateNewProject = {
                appState = currentAppState.copy(creatingNewProject = true)
            },
            onGoToRecentProjects = {
                appState = currentAppState.copy(creatingNewProject = false)
            }
        )

        is AppState.ProjectState -> ProjectStateWindow(currentAppState.project)
    }
}
