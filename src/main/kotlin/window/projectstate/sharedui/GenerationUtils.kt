package window.projectstate.sharedui

import inference.GenerationRequest
import models.Checkpoint
import models.CommonScriptParams
import models.ScriptParams
import java.util.*

fun getGenerationRequests(
    outPictureDirectoryPath: String,
    promptText: String,
    seeds: List<Int>,
    ddimCount: Int,
    checkpointsSelected: Set<Checkpoint>,
    getScriptParams: (CommonScriptParams) -> List<ScriptParams>
): List<GenerationRequest> {
    return checkpointsSelected.flatMap { checkpointSelected ->
        seeds.flatMap { seed ->
            val uuid = UUID.randomUUID().toString()
            getScriptParams(
                CommonScriptParams(
                    prompt = promptText,
                    seed = seed,
                    fileNamePrefix = uuid + checkpointSelected.name,
                    ddimSteps = ddimCount,
                    outDir = outPictureDirectoryPath,
                    checkpoint = checkpointSelected,
                )
            ).map {
                GenerationRequest(it)
            }
        }
    }
}