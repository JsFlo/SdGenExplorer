package window.loadingstate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.kmpalette.PaletteState
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.delay
import util.getDarkMutedColor
import util.getLightMutedColor
import util.getLightVibrantColor
import java.io.File

private const val LAUNCH_DELAY_MILLIS = 3000L
private const val WINDOW_WIDTH = 800
private const val WINDOW_HEIGHT = 450
private const val LAUNCH_IMAGES_PATH = "launchImages"
private const val APP_TITLE = "SdGenExplorer"

@Composable
fun LoadingStateWindow(onFinishedLoading: () -> Unit) {
    LaunchedEffect(true) {
        delay(LAUNCH_DELAY_MILLIS)
        onFinishedLoading()
    }
    Window(
        onCloseRequest = {},
        undecorated = true,
        resizable = false,
        alwaysOnTop = true,
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(WINDOW_WIDTH.dp, WINDOW_HEIGHT.dp)
        )
    ) {
        MaterialTheme {
            Row(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                WindowContent()
            }
        }
    }
}

@Composable
private fun RowScope.WindowContent() {
    val imageDirectory by remember { mutableStateOf(File(LAUNCH_IMAGES_PATH)) }
    val imageBitmap = remember(imageDirectory.path) {
        val file = imageDirectory.listFiles()!!.random()
        loadImageBitmap(file.inputStream())
    }
    val paletteState: PaletteState<ImageBitmap> = rememberPaletteState()
    LaunchedEffect(imageBitmap) {
        paletteState.generate(imageBitmap)
    }
    Image(
        painter = BitmapPainter(image = imageBitmap),
        modifier = Modifier.fillMaxHeight(),
        contentScale = ContentScale.Fit,
        contentDescription = null
    )
    Box(
        modifier = Modifier.fillMaxSize().background(paletteState.getDarkMutedColor(Color.Black))
    ) {
        Text(
            text = APP_TITLE,
            fontWeight = FontWeight.ExtraBold,
            color = paletteState.getLightMutedColor(Color.White),
            fontSize = TextUnit(38f, TextUnitType.Sp),
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(start = 8.dp, bottom = 16.dp, end = 8.dp)
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
            color = paletteState.getLightVibrantColor(Color.White)
        )
    }
}