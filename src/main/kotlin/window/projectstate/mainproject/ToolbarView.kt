package window.projectstate.mainproject

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
@Preview
fun ToolbarView(
    onAddImagesButtonClicked: () -> Unit,
    onGenerateImages: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.DarkGray)
    ) {
        val addExistingImagesText by remember { mutableStateOf("Add Existing Images") }
        OutlinedButton(
            modifier = Modifier.padding(Dp(16f)),
            onClick = {
                onAddImagesButtonClicked()
            }) {
            Text(addExistingImagesText)
        }
        OutlinedButton(
            modifier = Modifier.padding(Dp(16f)),
            onClick = {
                onGenerateImages()
            }
        ) {
            Text("Generate Images")
        }

    }
}