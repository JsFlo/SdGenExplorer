package models

import kotlinx.serialization.Serializable
import models.ScriptParams.Img2ImgParams
import models.ScriptParams.Txt2ImgParams

@Serializable
data class ExplorerImage(
    val id: String,
    val fileName: String,
    val source: ExplorerImageSource,
    val isFavorite: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExplorerImage

        return fileName == other.fileName && isFavorite == other.isFavorite
    }

    override fun hashCode(): Int {
        return fileName.hashCode() + isFavorite.hashCode()
    }
}

@Serializable
sealed class ExplorerImageSource {
    @Serializable
    data class GeneratedFromTxt2Img(
        val txt2ImgParams: Txt2ImgParams
    ) : ExplorerImageSource()

    @Serializable
    data class GeneratedFromImg2Img(
        val img2ImgParams: Img2ImgParams
    ) : ExplorerImageSource()

    @Serializable
    data object NotGenerated : ExplorerImageSource()
}

@Serializable
data class CommonScriptParams(
    val prompt: String,
    val seed: Int,
    val fileNamePrefix: String,
    val outDir: String,
    val checkpoint: Checkpoint,
    val ddimSteps: Int = 150,
    val numberOfSamples: Int = 1,
    val numberOfIterations: Int = 1,
)

@Serializable
sealed class ScriptParams {
    abstract val commonScriptParams: CommonScriptParams

    @Serializable
    data class Txt2ImgParams(
        override val commonScriptParams: CommonScriptParams,
        val width: Int = 512,
        val height: Int = 512,
        val sampler: Sampler = Sampler.PLMS,
    ) : ScriptParams()

    @Serializable
    enum class Sampler {
        DDIM, PLMS, DPM
    }

    @Serializable
    data class Img2ImgParams(
        override val commonScriptParams: CommonScriptParams,
        val initImagePath: String,
    ) : ScriptParams()
}


