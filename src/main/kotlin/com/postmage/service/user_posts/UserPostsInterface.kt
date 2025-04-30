package com.postmage.service.user_posts

import com.postmage.enums.PostType
import com.postmage.model.group.GroupIdModel
import com.postmage.model.posts.add_posts.AddPostModel
import com.postmage.model.posts.followed_users.PostOfFollowedUsers
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.posts.make_photo_public.MakePhotoPublicRequestModel
import com.postmage.model.posts.make_photo_public.MakePhotoPublicResultModel
import com.postmage.model.posts.update_posts.UpdateUserPostModel
import com.postmage.model.posts.update_posts.UserCommentModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData


interface UserPostsInterface {
    suspend fun addPost(
        headers: UserRequestHeaders,
        body: AddPostModel,
        addPostType: PostType = PostType.ADD_PERSONAL
    ): ResponseData<Boolean>

    suspend fun getUserPostsWithUserId(headers: UserRequestHeaders, userId: String): ResponseData<List<GetUserPostModel>>
    suspend fun getGroupPost(headers: UserRequestHeaders, body: GroupIdModel): ResponseData<List<GetUserPostModel>>
    suspend fun getPost(headers: UserRequestHeaders, postId: String?): ResponseData<GetUserPostModel>
    suspend fun getComments(headers: UserRequestHeaders, postId: String?): ResponseData<ArrayList<UserCommentModel>?>
    suspend fun updatePost(headers: UserRequestHeaders, body: UpdateUserPostModel): ResponseData<GetUserPostModel>
    suspend fun postOfFollowedUsers(headers: UserRequestHeaders, body: PostOfFollowedUsers): ResponseData<List<GetUserPostModel>>
    suspend fun putMakePhotoPublic(headers: UserRequestHeaders, body: MakePhotoPublicRequestModel): ResponseData<MakePhotoPublicResultModel>

}