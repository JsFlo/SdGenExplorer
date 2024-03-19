@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package window.projectselection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import models.Checkpoint
import models.ProjectConfig
import models.ProjectData
import project.FileBasedProject
import project.SdGenProject
import util.projectDirectoryChooser
import java.io.File

private const val PROJECT_NAME_LABEL = "Project Name"
private const val PROJECT_DIRECTORY_LABEL = "Project Directory"
private const val PROJECT_IMAGE_OUTPUTS_LABEL = "Image Output Directory"
private const val TXT_2_IMG_DIRECTORY_LABEL = "Txt2Img Directory"
private const val IMG_2_IMG_DIRECTORY_LABEL = "Img2Img Directory"
private const val CREATE_PROJECT_TEXT = "Create Project"

@Composable
fun ColumnScope.NewProjectContent(
    component: ComposeWindow,
    onBack: () -> Unit,
    onSdGenProject: (SdGenProject) -> Unit
) {
    BackArrowIcon(onBack)

    var projectName by rememberSaveable { mutableStateOf("") }

    val projectDirectory = rememberSaveable { mutableStateOf("") }
    val outPictureDirectoryPath = rememberSaveable { mutableStateOf("/home/bootloop/Pictures") }
    val txt2ImgProcessPath = rememberSaveable { mutableStateOf("/home/bootloop/SdGenExplorer/scripts/txt2Img.sh") }
    val img2ImgProcessPath = rememberSaveable { mutableStateOf("/home/bootloop/SdGenExplorer/scripts/img2Img.sh") }

    // TODO: Make this configurable
    var checkpoints: Set<Checkpoint> by rememberSaveable {
        mutableStateOf(
            setOf(
                Checkpoint(
                    "1.1",
                    path = "/home/bootloop/Downloads/sd-v1-1.ckpt",
                    configPath = "/home/bootloop/stable-diffusion/configs/stable-diffusion/v1-inference.yaml"
                ),
                Checkpoint(
                    "1.2",
                    path = "/home/bootloop/Downloads/sd-v1-2.ckpt",
                    configPath = "/home/bootloop/stable-diffusion/configs/stable-diffusion/v1-inference.yaml"
                ),
                Checkpoint(
                    "1.3",
                    path = "/home/bootloop/Downloads/sd-v1-3.ckpt",
                    configPath = "/home/bootloop/stable-diffusion/configs/stable-diffusion/v1-inference.yaml"
                ),
                Checkpoint(
                    "1.4",
                    path = "/home/bootloop/Downloads/sd-v1-4.ckpt",
                    configPath = "/home/bootloop/stable-diffusion/configs/stable-diffusion/v1-inference.yaml",
                    autoSelected = true
                )
            )
        )
    }
    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
        TextField(
            value = projectName,
            onValueChange = {
                projectName = it
            },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
            label = { Text(PROJECT_NAME_LABEL) }
        )

        FileSelectorRow(
            mutableStateString = projectDirectory,
            label = PROJECT_DIRECTORY_LABEL,
            component = component
        )

        FileSelectorRow(
            mutableStateString = outPictureDirectoryPath,
            label = PROJECT_IMAGE_OUTPUTS_LABEL,
            component = component
        )

        FileSelectorRow(
            mutableStateString = txt2ImgProcessPath,
            label = TXT_2_IMG_DIRECTORY_LABEL,
            component = component
        )

        FileSelectorRow(
            mutableStateString = img2ImgProcessPath,
            label = IMG_2_IMG_DIRECTORY_LABEL,
            component = component
        )

        val isCreateProjectEnabled = projectDirectory.value.isNotEmpty() && projectName.isNotEmpty()

        CreateProjectButton(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            isEnabled = isCreateProjectEnabled
        ) {
            val projectFilePath = projectDirectory.value + "/" + projectName
            val projectFile = File(projectFilePath)
            if (!projectFile.exists()) {
                projectFile.createNewFile()
                val projectData = ProjectData(
                    projectName = projectName,
                    config = ProjectConfig(
                        outPictureDirectoryPath = outPictureDirectoryPath.value,
                        txt2ImgProcessPath = txt2ImgProcessPath.value,
                        img2ImgProcessPath = img2ImgProcessPath.value,
                        checkpoints = checkpoints
                    ),
                    explorerImages = emptySet()
                )
                onSdGenProject(FileBasedProject.create(projectFilePath, projectData))
            }
        }
    }
}

@Composable
private fun BackArrowIcon(onBack: () -> Unit) {
    Row {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(64.dp, 64.dp).padding(16.dp).align(Alignment.CenterVertically).onClick {
                onBack()
            },
            tint = Color.White
        )
    }
}

@Composable
private fun RowScope.AddIcon(onClick: () -> Unit) {
    Icon(
        Icons.Default.Add,
        contentDescription = null,
        modifier = Modifier.padding(16.dp).align(Alignment.CenterVertically).onClick {
            onClick()
        }
    )
}

@Composable
private fun CreateProjectButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled
    ) {
        Text(CREATE_PROJECT_TEXT)
    }
}

@Composable
private fun FileSelectorRow(
    modifier: Modifier = Modifier.fillMaxWidth().padding(8.dp),
    mutableStateString: MutableState<String>,
    label: String,
    component: ComposeWindow,
) {
    Row(modifier = modifier) {
        TextField(
            value = mutableStateString.value,
            onValueChange = {
                mutableStateString.value = it
            },
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
            label = { Text(label) }
        )
        AddIcon {
            val directoryFile = projectDirectoryChooser(component)
            directoryFile?.let {
                mutableStateString.value = it.path
            }
        }
    }
}