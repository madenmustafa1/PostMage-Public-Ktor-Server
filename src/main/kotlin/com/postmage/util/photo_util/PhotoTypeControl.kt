package com.postmage.util.photo_util

import java.nio.file.Files
import java.nio.file.Paths

object PhotoType {

    fun check(filePath: String) = filePath.checkMimeType()

    private fun String.checkMimeType(): Boolean {
        val mimeType = Files.probeContentType(Paths.get(this))
        return mimeType.photoNameControl()
    }

    private fun String?.photoNameControl(): Boolean {
        this?.lowercase()?.let { photoName ->
            val mimeType = "image/"

            return when (photoName) {
                "${mimeType}${Type.PNG.value}",
                "${mimeType}${Type.JPEG.value}",
                "${mimeType}${Type.JPG.value}",
                "${mimeType}${Type.HEIC.value}",
                "${mimeType}${Type.HEIF.value}",
                "${mimeType}${Type.HEVC.value}",
                "${mimeType}${Type.WEBP.value}" -> true
                else -> false
            }
            //return photoName == "${mimeType}png" || photoName == "${mimeType}jpeg" || photoName == "${mimeType}jpg" || photoName == "${mimeType}heic"
        }

        return false
    }

    sealed class Type(val value: String) {
        object PNG : Type("png")
        object JPEG : Type("jpeg")
        object JPG : Type("jpg")
        object HEIC : Type("heic")
        object HEIF : Type("heif")
        object HEVC : Type("hevc")
        object WEBP : Type("webp")
    }
}