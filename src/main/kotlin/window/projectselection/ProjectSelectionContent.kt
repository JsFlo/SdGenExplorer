@file:OptIn(ExperimentalMaterialApi::class)

package window.projectselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import project.FileBasedProject
import project.SdGenProject
import util.isValid
import util.projectIfValid

private const val NEW_PROJECT_BUTTON_TEXT = "New Project"
private const val OPEN_PROJECT_BUTTON_TEXT = "Open Project"
private const val RECENT_PROJECTS_TEXT = "Recent Projects"

@Composable
fun ColumnScope.ProjectSelectionContent(
    recentProjects: List<SdGenProject>,
    onNewProject: () -> Unit,
    onOpenProject: () -> Unit,
    onRecentProjectClicked: (recentProject: SdGenProject) -> Unit
) {
    Row(modifier = Modifier.wrapContentSize().fillMaxWidth().align(Alignment.End)) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)) {
            Button(modifier = Modifier.padding(start = 8.dp, end = 16.dp), onClick = {
                onNewProject()
            }) {
                Text(NEW_PROJECT_BUTTON_TEXT)
            }

            Button(modifier = Modifier.padding(start = 8.dp, end = 16.dp), onClick = {
                onOpenProject()
            }) {
                Text(OPEN_PROJECT_BUTTON_TEXT)
            }
        }
    }

    var invalidProjects: Set<SdGenProject> by rememberSaveable { mutableStateOf(setOf()) }

    LazyColumn(
        modifier = Modifier.background(Color.Black).weight(1f).fillMaxWidth().padding(start = 16.dp, end = 16.dp)
    ) {
        item {
            Text(RECENT_PROJECTS_TEXT, color = Color.White)
        }
        items(recentProjects) { recentProject ->
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = if (invalidProjects.contains(recentProject)) {
                        Color.Red
                    } else {
                        Color.Gray
                    },
                    elevation = 8.dp,
                    onClick = {
                        if (recentProject.isValid()) {
                            onRecentProjectClicked(recentProject)
                        } else {
                            invalidProjects += recentProject
                        }

                    }) {
                    Text(recentProject.pathToProject, color = Color.White, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}