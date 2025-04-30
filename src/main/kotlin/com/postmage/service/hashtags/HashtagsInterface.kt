package com.postmage.service.hashtags

import com.postmage.model.hashtags.GetHashtagPostsRequestModel
import com.postmage.model.hashtags.GetHashtagPostsResponseModel
import com.postmage.model.hashtags.PopularHashtagsModel
import com.postmage.model.posts.add_posts.AddPostModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData

interface HashtagsInterface {
    suspend fun savePostHashtag(
        headers: UserRequestHeaders,
        body: AddPostModel,
        postObjectId: String,
    ): Result<Any>

    suspend fun getPopularHashtags(headers: UserRequestHeaders,): ResponseData<ArrayList<PopularHashtagsModel>>

    suspend fun getHashtagPosts(headers: UserRequestHeaders, model: GetHashtagPostsRequestModel): ResponseData<GetHashtagPostsResponseModel>
}