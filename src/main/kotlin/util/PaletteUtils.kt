package util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.kmpalette.PaletteState

fun PaletteState<ImageBitmap>.getDarkMutedColor(defaultColor: Color = Color.Black): Color {
   return palette?.getDarkMutedColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}

fun PaletteState<ImageBitmap>.getDarkVibrantColor(defaultColor: Color = Color.Black): Color {
    return palette?.getDarkVibrantColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}

fun PaletteState<ImageBitmap>.getDominantColor(defaultColor: Color = Color.Black): Color {
    return palette?.getDominantColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}

fun PaletteState<ImageBitmap>.getLightMutedColor(defaultColor: Color = Color.Black): Color {
    return palette?.getLightMutedColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}

fun PaletteState<ImageBitmap>.getLightVibrantColor(defaultColor: Color = Color.Black): Color {
    return palette?.getLightVibrantColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}

fun PaletteState<ImageBitmap>.getMutedColor(defaultColor: Color = Color.Black): Color {
    return palette?.getMutedColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}

fun PaletteState<ImageBitmap>.getVibrantColor(defaultColor: Color = Color.Black): Color {
    return palette?.getVibrantColor(defaultColor.toArgb())?.let { Color(it) } ?: defaultColor
}