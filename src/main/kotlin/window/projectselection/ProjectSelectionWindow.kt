package window.projectselection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import project.FileBasedRecentProjectManager
import project.SdGenProject
import util.projectChooser

private const val WINDOW_TITLE = "Select Project"
private const val WINDOW_WIDTH = 700
private const val WINDOW_HEIGHT = 550

@Composable
fun ApplicationScope.ProjectSelectionWindow(
    creatingNewProject: Boolean,
    onProject: (SdGenProject) -> Unit,
    onCreateNewProject: () -> Unit,
    onGoToRecentProjects: () -> Unit
) {
    val recentProjectManager by remember { mutableStateOf(FileBasedRecentProjectManager) }

    Window(
        onCloseRequest = ::exitApplication,
        title = WINDOW_TITLE,
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(WINDOW_WIDTH.dp, WINDOW_HEIGHT.dp)
        )
    ) {
        MaterialTheme(colors = darkColors()) {
            AnimatedContent(
                targetState = creatingNewProject,
                modifier = Modifier.background(Color.DarkGray)
            ) { currentlyCreatingNewProject ->
                Column(modifier = Modifier.fillMaxSize()) {
                    if (currentlyCreatingNewProject) {
                        NewProjectContent(
                            this@Window.window,
                            onBack = onGoToRecentProjects,
                            onSdGenProject = { newProject ->
                                recentProjectManager.saveRecentProject(newProject)
                                onProject(newProject)
                            }
                        )
                    } else {
                        ProjectSelectionContent(
                            recentProjects = recentProjectManager.getRecentProjects(),
                            onNewProject = onCreateNewProject,
                            onOpenProject = {
                                projectChooser(this@Window.window)?.let { project ->
                                    recentProjectManager.saveRecentProject(project)
                                    onProject(project)
                                }
                            },
                            onRecentProjectClicked = onProject
                        )
                    }
                }
            }
        }
    }
}