package window.projectstate.sharedui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.kmpalette.PaletteState
import models.Checkpoint
import models.ExplorerImageSource
import models.ProjectConfig
import util.getDarkMutedColor
import util.getLightVibrantColor
import util.getMutedColor
import util.getVibrantColor


@Composable
fun ColumnScope.prompt(
    promptText: MutableState<String>,
    modifier: Modifier,
    onValueChanged: (String) -> Unit = { promptText.value = it }
) {
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color.Black, Color.Red, Color.Blue)
        )
    }

    TextField(
        value = promptText.value,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = modifier,
        label = { Text("Prompt") },
        textStyle = TextStyle(brush)
    )
}

@Composable
fun ColumnScope.ddimSetion(
    ddimCount: MutableState<Int>,
    textColor: Color,
    onDdimChanged: (Int) -> Unit = { ddimCount.value = it}
) {
    TextField(
        value = ddimCount.value.toString(),
        colors = TextFieldDefaults.textFieldColors(textColor = textColor),
        onValueChange = {
            onDdimChanged(it.toInt())
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        label = { Text("Ddim Steps") }
    )
}

@Composable
fun ColumnScope.seedSection(
    seeds: MutableState<List<Int>>,
    modifier: Modifier,
    textColor: Color = Color.Black,
) {
    var seedStartText by rememberSaveable { mutableStateOf(1337) }
    var seedEndText by rememberSaveable { mutableStateOf(1337) }
    var shouldRandom by rememberSaveable { mutableStateOf(false) }
    var randomCount by rememberSaveable { mutableStateOf(2) }

    remember(seedStartText, seedEndText, shouldRandom, randomCount) {
        val seedRange = (seedStartText..seedEndText)
        seeds.value = if (shouldRandom) {
            seedRange.shuffled().take(randomCount)
        } else {
            seedRange.map { it }
        }
    }
    Row(modifier = modifier) {
        TextField(
            value = seedStartText.toString(),
            colors = TextFieldDefaults.textFieldColors(textColor = textColor),
            onValueChange = {
                seedStartText = it.toInt()
            },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
            label = { Text("Seed Start") }
        )

        TextField(
            value = seedEndText.toString(),
            colors = TextFieldDefaults.textFieldColors(textColor = textColor),
            onValueChange = {
                seedEndText = it.toInt()
            },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
            label = { Text("Seed End") }
        )

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp)
        ) {
            Checkbox(
                checked = shouldRandom,
                onCheckedChange = {
                    shouldRandom = it
                }
            )

            TextField(
                value = randomCount.toString(),
                colors = TextFieldDefaults.textFieldColors(textColor = textColor),
                onValueChange = {
                    randomCount = it.toInt()
                },
                enabled = shouldRandom,
                modifier = Modifier.fillMaxWidth().weight(1f),
                label = { Text("Random Count") }
            )

        }

    }
}

@Composable
fun CheckpointSelector(
    checkpointsSelected: MutableState<Set<Checkpoint>>,
    allCheckpoints: Set<Checkpoint>,
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
    onCheckedChanged: (checked: Boolean, checkpoint: Checkpoint) -> Unit = { checked, checkpoint ->
        val mutableSet = checkpointsSelected.value.toMutableSet()
        if (checked) {
            mutableSet.add(checkpoint)
        } else {
            mutableSet.remove(checkpoint)
        }
        checkpointsSelected.value = mutableSet
    }
) {
    Text("Checkpoints", modifier = Modifier.padding(8.dp))
    allCheckpoints.forEach { checkpoint ->
        Row(modifier = modifier) {
            Checkbox(
                checked = checkpointsSelected.value.contains(checkpoint),
                onCheckedChange = {
                    onCheckedChanged(it, checkpoint)
                }
            )
            Text(
                checkpoint.name,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                color = textColor
            )
        }
    }
}


@Composable
fun ColumnScope.sourceView(
    source: ExplorerImageSource,
    modifier: Modifier,
    paletteState: PaletteState<ImageBitmap>
) {
    Box(modifier.padding(16.dp)) {
        Box(
            modifier = modifier.background(
                paletteState.getDarkMutedColor(Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            ).fillMaxWidth()
        ) {
            when (source) {
                is ExplorerImageSource.GeneratedFromImg2Img -> generatedFromImg2ImgSource(source, paletteState)
                is ExplorerImageSource.GeneratedFromTxt2Img -> generatedFromTxt2ImgSource(source, paletteState)
                ExplorerImageSource.NotGenerated -> notGeneratedSource(paletteState)
            }
        }
    }
}


@Composable
private fun BoxScope.generatedFromImg2ImgSource(
    source: ExplorerImageSource.GeneratedFromImg2Img,
    paletteState: PaletteState<ImageBitmap>
) {
    with(source.img2ImgParams) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "img2img",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.End).padding(4.dp),
                color = paletteState.getMutedColor(Color.White)
            )
            Text(
                commonScriptParams.prompt,
                fontSize = TextUnit(28f, TextUnitType.Sp),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp),
                color = paletteState.getLightVibrantColor(Color.White)
            )
            Spacer(Modifier.padding(8.dp))
            LabelAndValue("Generated From Image: ", initImagePath, paletteState)
            LabelAndValue("Seed: ", commonScriptParams.seed.toString(), paletteState)
            LabelAndValue(
                "Checkpoint: ",
                "${commonScriptParams.checkpoint.name} (${commonScriptParams.checkpoint.path})",
                paletteState
            )
            LabelAndValue("Ddim Steps: ", commonScriptParams.ddimSteps.toString(), paletteState)
            LabelAndValue("Samples: ", commonScriptParams.numberOfSamples.toString(), paletteState)
            LabelAndValue("Iterations: ", commonScriptParams.numberOfIterations.toString(), paletteState)
        }
    }
}

@Composable
private fun BoxScope.generatedFromTxt2ImgSource(
    source: ExplorerImageSource.GeneratedFromTxt2Img,
    paletteState: PaletteState<ImageBitmap>
) {
    with(source.txt2ImgParams) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "txt2Img",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.End).padding(4.dp),
                color = paletteState.getMutedColor(Color.White)
            )
            Text(
                commonScriptParams.prompt,
                fontSize = TextUnit(28f, TextUnitType.Sp),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp),
                color = paletteState.getLightVibrantColor(Color.White)
            )
            Spacer(Modifier.padding(8.dp))
            LabelAndValue("Seed: ", commonScriptParams.seed.toString(), paletteState)
            LabelAndValue(
                "Checkpoint: ",
                "${commonScriptParams.checkpoint.name} (${commonScriptParams.checkpoint.path})",
                paletteState
            )
            LabelAndValue("Ddim Steps: ", commonScriptParams.ddimSteps.toString(), paletteState)
            LabelAndValue("Samples: ", commonScriptParams.numberOfSamples.toString(), paletteState)
            LabelAndValue("Iterations: ", commonScriptParams.numberOfIterations.toString(), paletteState)
        }
    }
}

@Composable
private fun BoxScope.notGeneratedSource(paletteState: PaletteState<ImageBitmap>) {
    Text(
        text = "Not Generated",
        modifier = Modifier.align(Alignment.Center).padding(8.dp),
        color = paletteState.getLightVibrantColor(Color.White),
        fontWeight = FontWeight.ExtraBold,
        fontSize = TextUnit(28f, TextUnitType.Sp)
    )
}


@Composable
private fun LabelAndValue(
    label: String,
    value: String,
    paletteState: PaletteState<ImageBitmap>,
    modifier: Modifier = Modifier.fillMaxWidth().padding(4.dp)
) {
    Row(modifier = modifier) {
        Text(
            label,
            color = paletteState.getVibrantColor()
        )
        Text(
            value,
            color = paletteState.getLightVibrantColor(Color.White)
        )
    }
}