package com.postmage.repo

import com.postmage.enums.StatusCodeUtil
import com.postmage.model.image.DownloadPhotoRequestModel
import com.postmage.model.user_config.UserRequestHeaders

import com.postmage.service.ResponseData
import com.postmage.service.image.ImageInterface
import com.postmage.service.image.ImageService
import com.postmage.util.AppMessages
import java.io.File

class ImageRepository(
    private val imageService: ImageService,
    private val appMessages: AppMessages
): ImageInterface {
    override suspend fun downloadPhoto(headers: UserRequestHeaders, body: DownloadPhotoRequestModel): ResponseData<File> {
        return imageService.downloadPhoto(headers, body)
    }

    override suspend fun downloadPublicPhoto(photo: File): ResponseData<File> {
        if (photo.path.isEmpty()) return sendErrorData(
            appMessages.POST_ID_NOT_BE_NULL,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        return imageService.downloadPublicPhoto(photo)
    }
}