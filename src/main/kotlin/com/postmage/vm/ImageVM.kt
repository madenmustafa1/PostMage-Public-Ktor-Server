package com.postmage.vm

import com.postmage.enums.ImageQuality
import com.postmage.enums.getImageQuality
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.image.DownloadPhotoRequestModel
import com.postmage.model.user_config.getLanguageType
import com.postmage.model.user_config.toHeaders
import com.postmage.repo.ImageRepository
import com.postmage.util.AppMessages
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.extensions.deleteFileAsync
import com.postmage.util.http_util.sendException
import com.postmage.util.photo_util.PhotoUtil
import com.postmage.util.strings.AppMessageUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class ImageVM(
    private val repository: ImageRepository
) {

    suspend fun downloadPhoto(call: ApplicationCall) {
        runCatchableException {
            val body = DownloadPhotoRequestModel(
                photoName = call.request.queryParameters["photoName"]!!,
                objectId = call.request.queryParameters["objectId"],
                imageQuality = getImageQuality(
                    call.request.queryParameters["quality"]?.toIntOrNull()
                ),
            )

            if (PhotoUtil.isIosPhotoFormat(body.photoName)) {
                body.imageQuality = ImageQuality.SOURCE
            }

            val result = repository.downloadPhoto(call.request.toHeaders(), body)
            result.data?.let {
                call.respondFile(it)
                call.response.status(HttpStatusCode.OK)

                if (body.imageQuality != ImageQuality.SOURCE)
                    it.deleteFileAsync(100)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: AppMessageUtil.getAppMessages(LanguageType.EN).serverError
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }
}