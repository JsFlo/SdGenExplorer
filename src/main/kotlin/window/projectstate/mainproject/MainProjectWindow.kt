package window.projectstate.mainproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import inference.GenerationRequestServiceStatus
import models.ExplorerImage
import models.ExplorerImageSource
import models.ProjectData
import util.imageFileChooser
import java.util.*

@Composable
fun ApplicationScope.MainProjectWindow(
    projectData: MutableState<ProjectData>,
    generationRequestServiceStatusInformation: GenerationRequestServiceStatus,
    onGenerateImagesButtonClicked: () -> Unit,
    onImageClicked: (ExplorerImage) -> Unit
) {
    Window(
        onCloseRequest = ::exitApplication,
        title = title(projectData.value.projectName, projectData.value.explorerImages),
        state = WindowState(placement = WindowPlacement.Maximized)
    ) {
        MaterialTheme {
            Scaffold(
                topBar = {
                    ToolbarView(
                        onAddImagesButtonClicked = {
                            val newExplorerImages = showFileSelectorAndGetExplorerImages(this@Window.window)
                            if (newExplorerImages.isNotEmpty()) {
                                val explorerImages = projectData.value.explorerImages + newExplorerImages
                                projectData.value = projectData.value.copy(
                                    explorerImages = explorerImages
                                )
                            }
                        },
                        onGenerateImages = onGenerateImagesButtonClicked
                    )
                },
                bottomBar = {
                    GenerationRequestStatusBarView(generationRequestServiceStatusInformation)
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ImageListView(
                        explorerImages = projectData.value.explorerImages,
                        onFavoriteImage = { checked, image ->
                            projectData.value = projectData.value.copy(
                                explorerImages = projectData.value.explorerImages.map {
                                    if (it.id == image.id) {
                                        it.copy(
                                            isFavorite = checked
                                        )
                                    } else {
                                        it
                                    }
                                }.toSet()
                            )
                        },
                        onImageClicked = onImageClicked,
                        onImageRemoved = { explorerImage ->
                            val newList = projectData.value.explorerImages.toMutableSet()
                            newList.removeIf { it.fileName == explorerImage.fileName }
                            projectData.value = projectData.value.copy(
                                explorerImages = newList
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun showFileSelectorAndGetExplorerImages(
    window: ComposeWindow
): List<ExplorerImage> {
    val files = imageFileChooser(window)
    return files?.map { it.path }?.map { filePath ->
        ExplorerImage(
            id = UUID.randomUUID().toString(),
            fileName = filePath,
            source = ExplorerImageSource.NotGenerated
        )
    } ?: emptyList()
}

private fun title(
    projectName: String,
    explorerImages: Set<ExplorerImage>
): String {
    return "SdGenExplorer ($projectName ${explorerImages.size})"
}