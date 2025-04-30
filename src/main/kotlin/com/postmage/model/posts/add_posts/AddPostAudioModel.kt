package com.postmage.model.posts.add_posts

import kotlinx.serialization.Serializable

@Serializable
data class AddPostAudioModel(
    val audioName: String? = null,
    val userDefinedName: String? = null,
    val audioByteArray: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddPostAudioModel

        if (audioName != other.audioName) return false
        if (userDefinedName != other.userDefinedName) return false
        if (!audioByteArray.contentEquals(other.audioByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = audioName.hashCode()
        result = 31 * result + userDefinedName.hashCode()
        result = 31 * result + audioByteArray.contentHashCode()
        return result
    }
}