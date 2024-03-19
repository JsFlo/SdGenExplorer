package inference

import kotlinx.serialization.Serializable
import models.ScriptParams

@Serializable
data class GenerationRequest(val scriptParams: ScriptParams)

sealed class GenerationRequestResult(open val generationRequest: GenerationRequest) {
    data class Success(
        override val generationRequest: GenerationRequest,
        val inputStream: String?
    ) : GenerationRequestResult(generationRequest)

    data class Error(
        override val generationRequest: GenerationRequest,
        val inputStream: String?,
        val errorStream: String?
    ) : GenerationRequestResult(generationRequest)
}