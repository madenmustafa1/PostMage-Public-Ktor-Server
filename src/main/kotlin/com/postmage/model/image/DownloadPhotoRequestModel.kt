package com.postmage.model.image

import com.postmage.enums.ImageQuality
import kotlinx.serialization.Serializable

@Serializable
data class DownloadPhotoRequestModel(
    val objectId: String?,
    val photoName: String,
    var imageQuality: ImageQuality = ImageQuality.SOURCE
)