package com.postmage.service.image

import com.postmage.model.image.DownloadPhotoRequestModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData
import java.io.File

interface ImageInterface {

    suspend fun downloadPhoto(headers: UserRequestHeaders, body: DownloadPhotoRequestModel): ResponseData<File>
    suspend fun downloadPublicPhoto(photo: File): ResponseData<File>

}