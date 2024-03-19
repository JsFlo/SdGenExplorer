package window.projectstate.imagedetail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.kmpalette.PaletteState
import com.kmpalette.rememberPaletteState
import inference.GenerationRequest
import models.Checkpoint
import models.ExplorerImage
import models.ProjectConfig
import models.ScriptParams.Img2ImgParams
import util.getDarkMutedColor
import util.getLightMutedColor
import util.getLightVibrantColor
import window.projectstate.sharedui.*
import java.io.File

private const val TITLE = "Image Detail"
private const val GENERATE_BUTTON_TEXT = "Generate From Image"

@Composable
fun ImageDetailWindow(
    explorerImage: ExplorerImage,
    checkpoints: Set<Checkpoint>,
    outPictureDirectoryPath: String,
    queueRequests: (List<GenerationRequest>) -> Unit,
    onCloseImageDetail: () -> Unit,
) {
    Window(
        title = TITLE,
        onCloseRequest = onCloseImageDetail,
        state = WindowState(placement = WindowPlacement.Maximized)
    ) {
        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                ExplorerImageDetailView(explorerImage, checkpoints, outPictureDirectoryPath) { requests ->
                    queueRequests(requests)
                    onCloseImageDetail()
                }
            }

        }
    }
}

@Composable
private fun ExplorerImageDetailView(
    explorerImage: ExplorerImage,
    checkpoints: Set<Checkpoint>,
    outPictureDirectoryPath: String,
    onGenerateImg2ImgRequests: (List<GenerationRequest>) -> Unit
) {
    val file = File(explorerImage.fileName)
    if (file.exists()) {
        val imageBitmap: ImageBitmap = remember(file) {
            loadImageBitmap(file.inputStream())
        }
        val paletteState: PaletteState<ImageBitmap> = rememberPaletteState()
        LaunchedEffect(imageBitmap) {
            paletteState.generate(imageBitmap)
        }
        val textColor = paletteState.getDarkMutedColor(Color.Black)
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(6f).background(paletteState.getLightVibrantColor(Color.Black)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = BitmapPainter(image = imageBitmap),
                    modifier = Modifier.weight(1f).padding(top = 16.dp).border(
                        width = 8.dp,
                        color = paletteState.getDarkMutedColor(Color.LightGray),
                    ),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillHeight,
                    contentDescription = null
                )
                Text(
                    text = explorerImage.fileName,
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    color = textColor,
                    fontSize = TextUnit(18f, TextUnitType.Sp)
                )
                sourceView(
                    explorerImage.source,
                    Modifier.fillMaxWidth().heightIn(min = 250.dp),
                    paletteState
                )
            }

            val state = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.weight(4f).background(paletteState.getLightMutedColor(Color.Black)).fillMaxHeight(),
                state
            ) {
                item {
                    Column {
                        val checkpointsSelected =
                            rememberSaveable { mutableStateOf(checkpoints.filter { it.autoSelected }.toSet()) }
                        val promptText = rememberSaveable { mutableStateOf("") }
                        val seeds = rememberSaveable { mutableStateOf(emptyList<Int>()) }
                        val ddimCount = rememberSaveable { mutableStateOf(50) }

                        prompt(promptText, modifier = Modifier.fillMaxWidth().heightIn(256.dp).padding(16.dp))
                        seedSection(seeds, Modifier.fillMaxWidth(), textColor)
                        ddimSetion(ddimCount, textColor)

                        Row(modifier = Modifier.fillMaxWidth()) {
                            CheckpointSelector(
                                checkpointsSelected,
                                checkpoints,
                                textColor,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        GenerateButton {
                            val generationRequests = getGenerationRequests(
                                fileName = explorerImage.fileName,
                                outPictureDirectoryPath = outPictureDirectoryPath,
                                promptText = promptText.value,
                                seeds = seeds.value,
                                ddimCount = ddimCount.value,
                                checkpointsSelected = checkpointsSelected.value
                            )
                            onGenerateImg2ImgRequests(generationRequests)
                        }
                    }

                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

@Composable
private fun GenerateButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier.wrapContentSize().fillMaxWidth().padding(16.dp),
        onClick = onClick
    ) { Text(GENERATE_BUTTON_TEXT) }
}

private fun getGenerationRequests(
    fileName: String,
    outPictureDirectoryPath: String,
    promptText: String,
    seeds: List<Int>,
    ddimCount: Int,
    checkpointsSelected: Set<Checkpoint>
): List<GenerationRequest> {
    return getGenerationRequests(
        outPictureDirectoryPath, promptText, seeds, ddimCount, checkpointsSelected
    ) { commonScriptParams ->
        listOf(Img2ImgParams(commonScriptParams, fileName))
    }
}