package window.projectstate.txt2imggenerate

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import inference.GenerationRequest
import models.Checkpoint
import models.ScriptParams.Sampler
import models.ScriptParams.Txt2ImgParams
import window.projectstate.sharedui.CheckpointSelector
import window.projectstate.sharedui.ddimSetion
import window.projectstate.sharedui.prompt
import window.projectstate.sharedui.seedSection

private const val GENERATE_BUTTON_TEXT = "Generate"
private const val SAMPLERS_TITLE_TEXT = "Samplers"
private const val WINDOW_TITLE = "Generate Images"
private const val WINDOW_WIDTH = 800
private const val WINDOW_HEIGHT = 800

@Composable
fun ApplicationScope.Txt2ImgGenerateWindow(
    checkpoints: Set<Checkpoint>,
    outPictureDirectoryPath: String,
    queueRequests: (List<GenerationRequest>) -> Unit,
    onCloseGenerateWindow: () -> Unit,
) {
    Window(
        title = WINDOW_TITLE,
        onCloseRequest = onCloseGenerateWindow,
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(WINDOW_WIDTH.dp, WINDOW_HEIGHT.dp)
        )
    ) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Txt2ImgCreateRequestView(checkpoints, outPictureDirectoryPath) { requests ->
                    queueRequests(requests)
                    onCloseGenerateWindow()
                }
            }
        }
    }
}

@Composable
fun ColumnScope.Txt2ImgCreateRequestView(
    checkpoints: Set<Checkpoint>,
    outPictureDirectoryPath: String,
    onGenerate: (txt2ImgRequests: List<GenerationRequest>) -> Unit
) {
    val checkpointsSelected = rememberSaveable {
        mutableStateOf(checkpoints.filter { it.autoSelected }.toSet())
    }
    val promptText = rememberSaveable { mutableStateOf("") }
    val seeds = rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val ddimCount = rememberSaveable { mutableStateOf(50) }
    val samplersSelected = rememberSaveable { mutableStateOf(setOf(Sampler.PLMS)) }

    prompt(promptText, Modifier.fillMaxWidth().weight(1f).heightIn(min = 128.dp))
    seedSection(seeds, Modifier.fillMaxWidth())
    ddimSetion(ddimCount = ddimCount, textColor = Color.Black)
    Row(modifier = Modifier.fillMaxWidth()) {
        CheckpointSelector(checkpointsSelected, checkpoints, modifier = Modifier.weight(1f))
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        SamplerSelector(selectedSamplers = samplersSelected, modifier = Modifier.weight(1f))
    }

    GenerateButton {
        onGenerate(
            getGenerationRequests(
                samplersSelected.value,
                outPictureDirectoryPath,
                promptText.value,
                seeds.value,
                ddimCount.value,
                checkpointsSelected.value
            )
        )
    }
}

@Composable
private fun SamplerSelector(
    selectedSamplers: MutableState<Set<Sampler>>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onSamplerSelected: (Boolean, Sampler) -> Unit = { checked, sampler ->
        val mutableSet = selectedSamplers.value.toMutableSet()
        if (checked) {
            mutableSet.add(sampler)
        } else {
            mutableSet.remove(sampler)
        }
        selectedSamplers.value = mutableSet
    }
) {
    Text(SAMPLERS_TITLE_TEXT, modifier = Modifier.padding(8.dp))
    listOf(Sampler.DDIM, Sampler.PLMS, Sampler.DPM).forEach { sampler ->
        Row(modifier = modifier) {
            Checkbox(
                checked = selectedSamplers.value.contains(sampler),
                onCheckedChange = {
                    onSamplerSelected(it, sampler)
                }
            )
            Text(
                sampler.toString(),
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun GenerateButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier.wrapContentSize().fillMaxWidth().padding(16.dp),
        onClick = onClick
    ) {
        Text(GENERATE_BUTTON_TEXT)
    }
}

private fun getGenerationRequests(
    samplers: Set<Sampler>,
    outPictureDirectoryPath: String,
    promptText: String,
    seeds: List<Int>,
    ddimCount: Int,
    checkpointsSelected: Set<Checkpoint>
): List<GenerationRequest> {
    return window.projectstate.sharedui.getGenerationRequests(
        outPictureDirectoryPath, promptText, seeds, ddimCount, checkpointsSelected
    ) { commonScriptParams ->
        samplers.map { sampler ->
            Txt2ImgParams(commonScriptParams, sampler = sampler)
        }
    }
}