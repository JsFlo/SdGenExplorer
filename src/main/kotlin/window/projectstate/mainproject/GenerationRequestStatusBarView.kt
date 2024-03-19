@file:OptIn(ExperimentalFoundationApi::class)

package window.projectstate.mainproject

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import inference.GenerationRequest
import inference.GenerationRequestResult
import inference.GenerationRequestServiceStatus
import models.ScriptParams

@Composable
fun GenerationRequestStatusBarView(
    generationRequestServiceStatus: GenerationRequestServiceStatus,
) {

    var isExpanded by rememberSaveable { mutableStateOf(false) }

    if (!isExpanded) {
        BottomToolbar(
            generationRequestServiceStatus,
            onExpand = {
                isExpanded = true
            }
        )
    }

//    Column(modifier = Modifier.background(Color.DarkGray).fillMaxWidth().wrapContentHeight().heightIn(max = 256.dp)) {
//        if(isExpanded) {
//            if (generationRequestServiceStatus.requestsQueued.isNotEmpty() || generationRequestServiceStatus.finishedRequests.isNotEmpty()) {
//                Row(modifier = Modifier.weight(1f)) {
//                    if (generationRequestServiceStatus.requestsQueued.isNotEmpty()) {
//                        RequestsListView("Requests Queued") {
//                            items(generationRequestServiceStatus.requestsQueued) {
//                                RequestView(it)
//                            }
//                        }
//                    }
//                    if (generationRequestServiceStatus.finishedRequests.isNotEmpty()) {
//                        RequestsListView("Finished Requests") {
//                            items(generationRequestServiceStatus.finishedRequests.reversed()) {
//                                RequestView(
//                                    it.generationRequest, when (it) {
//                                        is GenerationRequestResult.Error -> Color.Red
//                                        is GenerationRequestResult.Success -> Color.Green
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        if (generationRequestServiceStatus.currentRequestBeingProcessed != null) {
//            CurrentRequestView(generationRequestServiceStatus.currentRequestBeingProcessed)
//        }
//    }
}

@Composable
private fun BottomToolbar(
    generationRequestServiceStatus: GenerationRequestServiceStatus, onExpand: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(36.dp).background(Color.DarkGray).padding(4.dp)) {
        Row(modifier = Modifier.align(Alignment.Center).fillMaxWidth()) {

            Row(modifier = Modifier.weight(6f).padding(end = 4.dp)) {
                if (generationRequestServiceStatus.currentRequestBeingProcessed != null) {
                    Box(modifier = Modifier.fillMaxWidth().background(Color.Gray, shape = RoundedCornerShape(4.dp))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                            Text("Generating: ", color = Color.White)
                            Text(
                                text = generationRequestServiceStatus.currentRequestBeingProcessed.scriptParams.getText(),
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart))
                    }
                }
            }

            Row(modifier = Modifier.weight(2f).padding(start = 4.dp, end = 4.dp)) {
                if (generationRequestServiceStatus.requestsQueued.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.Gray, shape = RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            "Requests Queued: " + generationRequestServiceStatus.requestsQueued.size,
                            modifier = Modifier.padding(start = 4.dp),
                            color = Color.White
                        )
                    }
                }
            }

            Row(modifier = Modifier.weight(2f).padding(start = 4.dp, end = 4.dp)) {
                if (generationRequestServiceStatus.finishedRequests.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.Gray, shape = RoundedCornerShape(4.dp))
                    ) {
                        Text("Finished Requests: ", color = Color.White, modifier = Modifier.padding(start = 4.dp))
                        val successResults =
                            generationRequestServiceStatus.finishedRequests.filterIsInstance<GenerationRequestResult.Success>().size
                        val errorResults =
                            generationRequestServiceStatus.finishedRequests.filterIsInstance<GenerationRequestResult.Error>().size
                        if (successResults > 0) {
                            Text("($successResults)", color = Color.Green, modifier = Modifier.weight(1f))
                        }
                        if (errorResults > 0) {
                            Text("($errorResults)", color = Color.Red, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Icon(
                Icons.Default.KeyboardArrowUp,
                null,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp).onClick {
                    onExpand()
                }
            )
        }
    }
}

@Composable
private fun RowScope.RequestsListView(
    title: String,
    content: LazyListScope.() -> Unit,
) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            title,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).align(Alignment.CenterHorizontally),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).weight(1f)) {
            LazyColumn(
                modifier = Modifier.background(Color.Black, shape = RoundedCornerShape(8.dp)).weight(1f)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ColumnScope.CurrentRequestView(request: GenerationRequest) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "Generating",
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 4.dp, top = 8.dp),
            color = Color.White
        )
        Box {
            RequestView(request)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart))
        }
    }
}

@Composable
private fun RequestView(request: GenerationRequest, textColor: Color = Color.White) {
    Text(
        text = request.scriptParams.getText(),
        modifier = Modifier.fillMaxWidth().background(Color.Black).padding(8.dp),
        color = textColor,
        maxLines = 1
    )
}

private fun ScriptParams.getText(): String {
    return when (this) {
        is ScriptParams.Img2ImgParams -> "[Img2Img Request] Seed: ${commonScriptParams.seed}, Checkpoint: ${commonScriptParams.checkpoint.name}, Prompt: ${commonScriptParams.prompt}"
        is ScriptParams.Txt2ImgParams -> "[Txt2Img Request] Seed: ${commonScriptParams.seed}, Checkpoint: ${commonScriptParams.checkpoint.name}, Prompt: ${commonScriptParams.prompt}"
    }
}