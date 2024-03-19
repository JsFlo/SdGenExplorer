package inference

import kotlinx.coroutines.flow.*
import models.ExplorerImage
import models.ProjectConfig
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

data class GenerationRequestServiceStatus(
    val requestsQueued: List<GenerationRequest> = emptyList(),
    val finishedRequests: List<GenerationRequestResult> = emptyList(),
    val currentRequestBeingProcessed: GenerationRequest? = null
)

object GenerationRequestService : Runnable {
    private const val LOG_TAG = "[GenerationRequestService]"

    private val generationRequestQueue: BlockingQueue<GenerationRequest> = LinkedBlockingQueue()
    private val finishedRequests: MutableList<GenerationRequestResult> = mutableListOf()
    private var currentRequest: GenerationRequest? = null
    private val _serviceInformation: MutableSharedFlow<GenerationRequestServiceStatus> =
        MutableStateFlow(GenerationRequestServiceStatus())

    private val successfullyGeneratedFlow: MutableSharedFlow<GenerationRequest?> = MutableStateFlow(null)

    var config: ProjectConfig? = null
    val serviceInformation: SharedFlow<GenerationRequestServiceStatus> = _serviceInformation
    val successfullyGeneratedExplorerImages: Flow<Set<ExplorerImage>> =
        successfullyGeneratedFlow.mapNotNull { request ->
            request?.let { request.toExplorerImages() }
        }

    fun queueRequests(requests: List<GenerationRequest>) {
        generationRequestQueue.addAll(requests)
        emitUpdate()
    }

    override fun run() {
        try {
            while (true) {
                val requestToBeProcessed = generationRequestQueue.take()
                val currentConfig = config
                if (currentConfig != null) {
                    consume(requestToBeProcessed, currentConfig)
                }
            }
        } catch (e: Exception) {
            println("\n\n $LOG_TAG Exception: $e \n\n")
        }
    }

    private fun consume(requestToBeProcessed: GenerationRequest, config: ProjectConfig) {
        currentRequest = requestToBeProcessed
        emitUpdate()

        println("$LOG_TAG Starting to process: " + requestToBeProcessed)
        val generationRequestResult = requestToBeProcessed.processRequest(config)
        println("$LOG_TAG Finished processing with result: " + generationRequestResult)

        currentRequest = null
        if(generationRequestResult is GenerationRequestResult.Success) {
            successfullyGeneratedFlow.tryEmit(requestToBeProcessed)
        }
        finishedRequests.add(generationRequestResult)
        emitUpdate()
    }

    private fun emitUpdate() {
        val requestsQueued = kotlin.runCatching { generationRequestQueue.toList() }.getOrNull() ?: emptyList()
        _serviceInformation.tryEmit(
            GenerationRequestServiceStatus(
                requestsQueued = requestsQueued,
                finishedRequests = finishedRequests,
                currentRequestBeingProcessed = currentRequest
            )
        )
    }
}