package com.postmage.model.profile.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfilePhotoModel(
    var photoName: String? = null,
    var photoBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateProfilePhotoModel

        if (photoBytes != null) {
            if (other.photoBytes == null) return false
            if (!photoBytes.contentEquals(other.photoBytes)) return false
        } else if (other.photoBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoBytes?.contentHashCode() ?: 0
        result *= 31
        return result
    }
}
