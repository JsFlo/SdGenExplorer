package window.projectstate

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import inference.GenerationRequestService
import inference.GenerationRequestServiceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.ExplorerImage
import project.SdGenProject
import window.projectstate.imagedetail.ImageDetailWindow
import window.projectstate.mainproject.MainProjectWindow
import window.projectstate.txt2imggenerate.Txt2ImgGenerateWindow
import java.util.concurrent.Executors

private val generationRequestService = GenerationRequestService.also {
    Executors.newFixedThreadPool(1).submit(it)
}

@Composable
fun ApplicationScope.ProjectStateWindow(
    currentProject: SdGenProject
) {
    val projectData = remember(currentProject) { mutableStateOf(currentProject.getProjectData()) }

    // update saved project when it's changed
    LaunchedEffect(projectData.value) {
        currentProject.setProjectData(projectData.value)
    }
    val config by remember(projectData.value) { mutableStateOf(projectData.value.config) }
    remember(config) {
        generationRequestService.config = config
    }

    val scope = rememberCoroutineScope()
    val showGeneratedImagesWindow: MutableState<Boolean> = remember { mutableStateOf(false) }
    val showImageDetailWindow: MutableState<ExplorerImage?> = remember { mutableStateOf(null) }
    val generationRequestServiceInformation = remember { mutableStateOf(GenerationRequestServiceStatus()) }

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            // listen for current requests to show status/progress/queue
            generationRequestService.serviceInformation.collect {
                generationRequestServiceInformation.value = it
            }
        }
        scope.launch(Dispatchers.IO) {
            // listen for generated images and add to project data
            generationRequestService.successfullyGeneratedExplorerImages.collect { newExplorerImages ->
                projectData.value = projectData.value.copy(
                    explorerImages = newExplorerImages + projectData.value.explorerImages
                )
            }
        }
    }

    MainProjectWindow(
        projectData,
        generationRequestServiceInformation.value,
        onGenerateImagesButtonClicked = {
            showGeneratedImagesWindow.value = true
        },
        onImageClicked = { explorerImage ->
            showImageDetailWindow.value = explorerImage
        }
    )

    if (showGeneratedImagesWindow.value) {
        Txt2ImgGenerateWindow(
            config.checkpoints,
            config.outPictureDirectoryPath,
            queueRequests = { requests ->
                generationRequestService.queueRequests(requests)
            },
            onCloseGenerateWindow = {
                showGeneratedImagesWindow.value = false
            }
        )
    }

    val explorerImageDetail = showImageDetailWindow.value
    if (explorerImageDetail != null) {
        ImageDetailWindow(
            explorerImageDetail,
            config.checkpoints,
            config.outPictureDirectoryPath,
            queueRequests = { requests ->
                generationRequestService.queueRequests(requests)
            },
            onCloseImageDetail = {
                showImageDetailWindow.value = null
            }
        )
    }
}