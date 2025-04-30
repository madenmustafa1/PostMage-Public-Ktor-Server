package com.postmage.util.photo_util

import com.postmage.util.file.Directory
import com.postmage.util.extensions.makeFolder
import java.io.File

object PhotoUtil {

    fun getPhotoFormat(path: String): String {
        val fileName = path.substringAfterLast('/')
        return fileName.substringAfterLast('.')
    }

    fun getPhotoName(path: String): String {
        val fileName = path.substringAfterLast('/')
        return fileName.substringBeforeLast('.')
    }

    fun isIosPhotoFormat(format: String): Boolean {
        if (format.lowercase().contains("heif")) return true
        if (format.lowercase().contains("heic")) return true
        return false
    }

    /**
     * @postId Post id
     * @userId Paylaşılan postun sahibi
     * @return Post file path
     */
    fun getPostPath(
        postId: String,
        userId: String,
        getDesktopDir: Boolean
    ): String {
        if (!getDesktopDir) return "/$userId/$postId"
        return Directory.userDesktopDir?.path + "/$userId/$postId"
    }

    /**
     * @postId Post id
     * @userId Paylaşılan postun sahibi
     * @createIsNotExist Bu değer true geçerse ve belirtilen path yoksa oluşturulur.
     * @return Oluşturulan file path
     */
    fun createPostPath(
        postId: String,
        userId: String
    ): String {
        val userFile = File(Directory.userDesktopDir?.path + "/$userId")

        if (!userFile.exists())
            makeFolder(folderName = userFile.path)

        makeFolder(folderName = userFile.path + "/$postId")

        return Directory.userDesktopDir?.path + "/$userId/$postId"
    }
}