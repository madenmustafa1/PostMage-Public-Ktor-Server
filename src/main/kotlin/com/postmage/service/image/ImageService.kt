package com.postmage.service.image

import com.mongodb.BasicDBObject
import com.postmage.enums.ImageQuality
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.image.DownloadPhotoRequestModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.util.AppMessages
import com.postmage.util.file.Directory
import com.postmage.util.UuidUtil
import com.postmage.util.extensions.resizeImage
import com.postmage.util.strings.AppMessageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageService(
    private val mongoDB: MongoInitialize,
) : ImageInterface {
    override suspend fun downloadPhoto(
        headers: UserRequestHeaders,
        body: DownloadPhotoRequestModel
    ): ResponseData<File> {
        val inputFile = File(Directory.userDesktopDir?.path, body.photoName)
        var outputFile = inputFile

        if (withContext(Dispatchers.IO) { inputFile.isFile }) {
            //Fotoğrafın kalitesi düşürülüyor. (Client isterse)
            if (body.imageQuality != ImageQuality.SOURCE) {
                val outputTempFile = File(
                    Directory.userDesktopDir?.path,
                    Directory.tempFiles + "/" + UuidUtil.createUuid().toString() + System.currentTimeMillis()
                        .toString() + ".jpg"
                )

                outputFile = inputFile.resizeImage(
                    ratio = body.imageQuality.ratio,
                    outputFile = outputTempFile
                )
            }

            if (body.objectId != null) {
                val query = BasicDBObject("objectId", body.objectId)
                repeat(mongoDB.getUsersPostsCollection.find(query).limit(1).count()) {
                    return ResponseData.success(outputFile)
                }
            } else return ResponseData.success(outputFile)

        } else return sendErrorData(AppMessageUtil.getAppMessages(headers.language).photoNotFound)

        return sendErrorData(AppMessageUtil.getAppMessages(headers.language).accessDenied)
    }

    override suspend fun downloadPublicPhoto(
        photo: File
    ): ResponseData<File> {
        return if (withContext(Dispatchers.IO) { photo.isFile }) {
            ResponseData.success(photo)
        } else sendErrorData(AppMessageUtil.getAppMessages(LanguageType.EN).photoNotFound)
    }
}


/*
package com.postmage.service.image

import com.mongodb.BasicDBObject
import com.postmage.enums.ImageQuality
import com.postmage.model.image.DownloadPhotoRequestModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.util.AppMessages
import com.postmage.util.file.Directory
import com.postmage.util.UuidUtil
import com.postmage.util.extensions.resizeImage
import com.postmage.util.photo_util.PhotoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageService(
    private val mongoDB: MongoInitialize,
    private val appMessages: AppMessages
) : ImageInterface {
    override suspend fun downloadPhoto(
        headers: UserRequestHeaders,
        body: DownloadPhotoRequestModel
    ): ResponseData<File> = withContext(Dispatchers.IO) {



        val query = BasicDBObject("objectId", body.objectId!!)
        var postOwnerUserId: String? = null

        mongoDB.getUsersPostsCollection
            .find(query)
            .limit(1).forEach {
                postOwnerUserId = it.userId
            }

        if (postOwnerUserId == null) return@withContext sendErrorData(appMessages.PHOTO_NOT_FOUND)

        val postPath = PhotoUtil.getPostPath(
            postId = body.objectId,
            userId = postOwnerUserId!!
        )

        val inputFile = File(postPath, body.photoName)
        var outputFile = inputFile

        if (inputFile.isFile) {

            //Fotoğrafın kalitesi düşürülüyor. (Client isterse)
            if (body.imageQuality != ImageQuality.SOURCE) {
                val outputTempFile = File(
                    postPath,
                    UuidUtil.createUuid().toString() + System.currentTimeMillis().toString() + ".jpg"
                )

                outputFile = inputFile.resizeImage(
                    ratio = body.imageQuality.ratio,
                    outputFile = outputTempFile
                )
            }

            return@withContext ResponseData.success(outputFile)
        } else return@withContext sendErrorData(appMessages.PHOTO_NOT_FOUND)

    }

    override suspend fun downloadPublicPhoto(
        photo: File
    ): ResponseData<File> {
        return if (withContext(Dispatchers.IO) { photo.isDirectory }) {
            ResponseData.success(photo)
        } else sendErrorData(appMessages.PHOTO_NOT_FOUND)
    }
}
 */