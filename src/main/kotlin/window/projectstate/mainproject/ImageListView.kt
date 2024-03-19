@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class, ExperimentalFoundationApi::class
)

package window.projectstate.mainproject

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.kmpalette.PaletteState
import com.kmpalette.rememberPaletteState
import models.ExplorerImage
import util.getDarkMutedColor
import util.getLightMutedColor
import window.projectstate.sharedui.sourceView
import java.io.File

@Composable
@Preview
fun ColumnScope.ImageListView(
    explorerImages: Set<ExplorerImage>,
    onFavoriteImage: (checked: Boolean, explorerImage: ExplorerImage) -> Unit,
    onImageClicked: (explorerImage: ExplorerImage) -> Unit,
    onImageRemoved: (explorerImage: ExplorerImage) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(512.dp),
        content = {
            items(explorerImages.toList()) { item: ExplorerImage ->
                ImageView(item, onFavoriteImage, onImageClicked, onImageRemoved)
            }
        }
    )
}

@Composable
fun ImageView(
    explorerImage: ExplorerImage,
    onFavoriteImage: (checked: Boolean, explorerImage: ExplorerImage) -> Unit,
    onImageClicked: (explorerImage: ExplorerImage) -> Unit,
    onRemoveImage: (explorerImage: ExplorerImage) -> Unit
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

        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = paletteState.getDarkMutedColor(Color.Black)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Image(
                    painter = BitmapPainter(image = imageBitmap),
                    modifier = Modifier.onClick {
                        onImageClicked(explorerImage)
                    }.border(
                        width = 8.dp,
                        color = paletteState.getLightMutedColor(Color.LightGray)
                    ).align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )

                Spacer(Modifier.padding(16.dp))

                Column(
                    modifier = Modifier.background(
                        paletteState.getLightMutedColor(Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    ).fillMaxWidth()
                ) {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            if (explorerImage.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            "",
                            modifier = Modifier.size(64.dp, 64.dp).padding(16.dp).align(Alignment.CenterVertically)
                                .onClick {
                                    onFavoriteImage(!explorerImage.isFavorite, explorerImage)
                                },
                            tint = paletteState.getDarkMutedColor(Color.Red)
                        )
                        Text(
                            text = explorerImage.fileName,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp).weight(1f)
                                .weight(1f).align(Alignment.CenterVertically),
                            color = paletteState.getDarkMutedColor(Color.Black),
                            fontSize = TextUnit(18f, TextUnitType.Sp)
                        )
                        Icon(
                            Icons.Rounded.Delete,
                            "",
                            modifier = Modifier.size(64.dp, 64.dp).padding(16.dp).align(Alignment.CenterVertically)
                                .onClick {
                                    onRemoveImage(explorerImage)
                                },
                            tint = paletteState.getDarkMutedColor(Color.Red)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        sourceView(explorerImage.source, Modifier.fillMaxWidth(), paletteState)
                    }

                }

            }

        }
    }
}