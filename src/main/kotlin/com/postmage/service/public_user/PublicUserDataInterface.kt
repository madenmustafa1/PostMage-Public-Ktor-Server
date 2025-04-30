package com.postmage.service.public_user

import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.service.ResponseData
import java.io.File

interface PublicUserDataInterface {
    suspend fun getPostPhoto(postId: String): ResponseData<File>
    suspend fun getPublicPhoto(userId: String, photoName: String): ResponseData<File>
    suspend fun getUserProfileInfoWithUsername(username: String): ResponseData<UserProfileInfoModel?>
    suspend fun getUserPostsWithUserName(username: String): ResponseData<List<GetUserPostModel>>

}