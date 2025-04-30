package com.postmage.repo

import com.postmage.enums.StatusCodeUtil
import com.postmage.model.hashtags.GetHashtagPostsRequestModel
import com.postmage.model.hashtags.GetHashtagPostsResponseModel
import com.postmage.model.hashtags.PopularHashtagsModel
import com.postmage.model.posts.add_posts.AddPostModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData
import com.postmage.service.hashtags.HashtagsInterface
import com.postmage.service.hashtags.HashtagsService
import com.postmage.util.strings.AppMessageUtil

class HashtagRepository(
    private val hashtagsService: HashtagsService,
) : HashtagsInterface {
    override suspend fun savePostHashtag(
        headers: UserRequestHeaders,
        body: AddPostModel,
        postObjectId: String
    ): Result<Any> {
        return hashtagsService.savePostHashtag(
            headers = headers,
            body = body,
            postObjectId = postObjectId
        )
    }

    override suspend fun getPopularHashtags(headers: UserRequestHeaders): ResponseData<ArrayList<PopularHashtagsModel>> {
        return hashtagsService.getPopularHashtags(headers = headers)
    }

    override suspend fun getHashtagPosts(
        headers: UserRequestHeaders,
        model: GetHashtagPostsRequestModel
    ): ResponseData<GetHashtagPostsResponseModel> {
        if (model.hashtagName.isNullOrEmpty()) {
            return sendErrorData(
                message = AppMessageUtil.getAppMessages(headers.language).modelIsNotValid,
                statusCode = StatusCodeUtil.BAD_REQUEST,
            )
        }

        return  hashtagsService.getHashtagPosts(headers = headers, model = model)
    }
}