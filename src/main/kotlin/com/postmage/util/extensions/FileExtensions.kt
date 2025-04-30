package com.postmage.util.extensions

import com.postmage.model.posts.add_posts.AddPostPhotoModel
import com.postmage.util.photo_util.ImageUtil
import com.postmage.util.photo_util.PhotoType
import com.postmage.util.photo_util.PhotoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File


suspend fun ArrayList<AddPostPhotoModel>.writePhotoToDisk(userId: String, postId: String): Boolean =
    withContext(Dispatchers.IO) {
        return@withContext try {
            val parentPath = PhotoUtil.createPostPath(
                postId = postId,
                userId = userId
            )

            for (i in this@writePhotoToDisk) {
                val photoDir = File(parentPath, i.photoName)

                if (withContext(Dispatchers.IO) {
                        photoDir.createNewFile()
                    }) {
                    photoDir.writeBytes(i.photoByteArray)
                }

                if (!PhotoType.check(photoDir.path)) return@withContext false
            }
            true
        } catch (e: Exception) {
            false
        }
    }


suspend fun ByteArray.writePhotoToDisk(postId: String, photoName: String, userId: String): Boolean {
    return try {
        val parentPath = PhotoUtil.createPostPath(
            postId = postId,
            userId = userId
        )

        val photoDir = File(parentPath, photoName)

        if (withContext(Dispatchers.IO) {
                photoDir.createNewFile()
            }) {
            photoDir.writeBytes(this)
        }

        if (!PhotoType.check(photoDir.path)) return false
        return true
    } catch (e: Exception) {
        false
    }
}

suspend fun File.resizeImage(
    outputFile: File,
    ratio: Double
): File {
    val resizeImageByteArray = ImageUtil.resizeImage(
        inputFile = this,
        ratio = ratio
    )
    return ImageUtil.byteArrayToFile(
        byteArray = resizeImageByteArray,
        fileName = outputFile.path
    )
}

suspend fun File.deleteFileAsync(delayTimeMillis: Long = 0) = withContext(Dispatchers.IO) {
    if (!this@deleteFileAsync.exists()) return@withContext
    delay(delayTimeMillis)
    delete()
}