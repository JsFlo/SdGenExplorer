package models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectData(
    val projectName: String,
    val config: ProjectConfig,
    val explorerImages: Set<ExplorerImage>
)

@Serializable
data class ProjectConfig(
    val outPictureDirectoryPath: String,
    val txt2ImgProcessPath: String,
    val img2ImgProcessPath: String,
    val checkpoints: Set<Checkpoint>
)

@Serializable
data class Checkpoint(
    val name: String,
    val path: String,
    val configPath: String,
    val autoSelected: Boolean = false
)