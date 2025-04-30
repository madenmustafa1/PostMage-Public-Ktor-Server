package com.postmage.util.file

import com.postmage.util.photo_util.PhotoType

object FileControl {

    fun isAudio(fullName: String): Boolean {
        val extension = fullName.substringAfterLast('.', "").lowercase()
        return extension == "mp3" || extension == "wav"
    }


    fun isPhoto(fullName: String): Boolean {
        val png = PhotoType.Type.PNG.value
        val jpeg = PhotoType.Type.JPEG.value
        val jpg = PhotoType.Type.JPG.value
        val heic = PhotoType.Type.HEIC.value
        val heif = PhotoType.Type.HEIF.value
        val hevc = PhotoType.Type.HEVC.value
        val webp = PhotoType.Type.WEBP.value

        val name = fullName.substringAfterLast('.', "").lowercase()

        return name == png
                || name == jpeg
                || name == jpg
                || name == heic
                || name == heif
                || name == hevc
                || name == webp
    }
}