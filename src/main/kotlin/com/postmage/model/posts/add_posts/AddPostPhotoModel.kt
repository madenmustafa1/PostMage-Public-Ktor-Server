package com.postmage.model.posts.add_posts

import kotlinx.serialization.Serializable

@Serializable
data class AddPostPhotoModel(
    val photoName: String,
    val photoByteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddPostPhotoModel

        if (photoName != other.photoName) return false
        if (!photoByteArray.contentEquals(other.photoByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoName.hashCode()
        result = 31 * result + photoByteArray.contentHashCode()
        return result
    }
}