package com.postmage.model.posts.add_posts


import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class AddPostModel(
    var photoList: ArrayList<AddPostPhotoModel> = arrayListOf(),
    var description: String = "",
    var groupId: String = "",
    val creationTime: Long = DateUtil.getTimeNow(),
    val isDeleted: Boolean = false,
    var hashtags: ArrayList<String> = arrayListOf(),
    var audioModel: AddPostAudioModel = AddPostAudioModel()
)