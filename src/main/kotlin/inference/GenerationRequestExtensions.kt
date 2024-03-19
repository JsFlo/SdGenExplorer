package inference

import models.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

fun GenerationRequest.toExplorerImages(): Set<ExplorerImage> {
    val newFilePaths = this.scriptParams.commonScriptParams.getFilePathsCreated()
    val source = this.scriptParams.getSource()

    return newFilePaths.filter { filePath ->
        File(filePath).exists()
    }.mapTo(mutableSetOf()) { filePath ->
        ExplorerImage(
            id = UUID.randomUUID().toString(),
            fileName = filePath,
            source = source
        )
    }
}

private fun CommonScriptParams.getFilePathsCreated(): List<String> {
    val filePaths = mutableListOf<String>()
    for (i in 0 until numberOfIterations) {
        for (s in 0 until numberOfSamples) {
            val filePath = "$outDir/${fileNamePrefix}_${seed}_${i}_${s}.png"
            filePaths.add(filePath)
        }
    }
    return filePaths
}

private fun ScriptParams.getSource(): ExplorerImageSource {
    return when (this) {
        is ScriptParams.Img2ImgParams -> ExplorerImageSource.GeneratedFromImg2Img(this)
        is ScriptParams.Txt2ImgParams -> ExplorerImageSource.GeneratedFromTxt2Img(this)
    }
}

fun GenerationRequest.processRequest(config: ProjectConfig): GenerationRequestResult {
    val pb = when (val scriptParams = this.scriptParams) {
        is ScriptParams.Img2ImgParams -> ProcessBuilder(
            config.img2ImgProcessPath,
            *scriptParams.getCommandArgs().toTypedArray()
        )

        is ScriptParams.Txt2ImgParams -> ProcessBuilder(
            config.txt2ImgProcessPath,
            *scriptParams.getCommandArgs().toTypedArray()
        )
    }
    val processResult = pb.getResult()
    return if (processResult.result == 0) {
        GenerationRequestResult.Success(this, processResult.inputStream)
    } else {
        GenerationRequestResult.Error(
            this,
            processResult.inputStream,
            processResult.errorStream
        )
    }
}

private fun ScriptParams.Txt2ImgParams.getCommandArgs(): List<String> {
    val args = commonScriptParams.getCommonCommandArgs().toMutableList()
    args.add("--W")
    args.add("$width")
    args.add("--H")
    args.add("$height")

    when (sampler) {
        ScriptParams.Sampler.DDIM -> Unit
        ScriptParams.Sampler.PLMS -> args.add("--plms")
        ScriptParams.Sampler.DPM -> args.add("--dpm_solver")
    }
    return args
}

private fun ScriptParams.Img2ImgParams.getCommandArgs(): List<String> {
    val args = commonScriptParams.getCommonCommandArgs().toMutableList()
    args.add("--init-img")
    args.add(initImagePath)
    return args
}

private fun CommonScriptParams.getCommonCommandArgs(): List<String> {
    val args = mutableListOf<String>()
    args.add("--prompt")
    args.add(prompt)
    args.add("--seed")
    args.add("$seed")
    args.add("--ddim_steps")
    args.add("$ddimSteps")
    args.add("--n_samples")
    args.add("$numberOfSamples")
    args.add("--n_iter")
    args.add("$numberOfIterations")
    args.add("--outdir")
    args.add(outDir)
    args.add("--filename")
    args.add(fileNamePrefix)
    args.add("--ckpt")
    args.add(checkpoint.path)
    args.add("--config")
    args.add(checkpoint.configPath)

    return args
}

private fun ProcessBuilder.getResult(): ProcessResult {
    println("Running command: " + this.command().joinToString(separator = " ") { it })

    return try {
        val process = this.start()
        ProcessResult(process.waitFor(), process.inputStream.toOutputString(), process.errorStream.toOutputString())
    } catch (e: Exception) {
        ProcessResult(2, "", e.toString())
    }.also {
        println(
            """
                Command Result:
                    Input Stream: ${it.inputStream}
                    Error Stream: ${it.errorStream}
            """.trimIndent()
        )
    }
}

private fun InputStream.toOutputString(): String {
    val reader = BufferedReader(InputStreamReader(this))
    val builder = StringBuilder()
    var line: String?
    while ((reader.readLine().also { line = it }) != null) {
        builder.append(line)
        builder.append(System.lineSeparator())
    }
    return builder.toString()
}

private data class ProcessResult(val result: Int, val inputStream: String?, val errorStream: String?)
