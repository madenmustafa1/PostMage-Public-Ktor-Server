package com.postmage.model.posts.get_posts


import com.postmage.model.posts.add_posts.PostHashtagsModel
import com.postmage.model.posts.update_posts.UserCommentModel
import kotlinx.serialization.Serializable
import kotlin.collections.ArrayList

@Serializable
data class GetUserPostModel(
    var photoList: ArrayList<GetUserPostPhotoModel>? = null,
    var photoName: String = "",
    var description: String = "",
    var groupId: String = "",
    var creationTime: Long? = null,
    var likeUserId: ArrayList<String>? = null,
    var comment: ArrayList<UserCommentModel>? = null,
    var objectId: String? = null,
    var userId: String? = null,
    var nameSurname: String? = null,
    var groupName: String? = null,
    var isDeleted: Boolean? = false,
    var profilePhotoUrl: String? = null,
    var postIsPublic: Boolean? = false,
    val hashtags: ArrayList<PostHashtagsModel>? = arrayListOf()
)