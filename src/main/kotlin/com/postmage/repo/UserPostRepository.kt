package com.postmage.repo

import com.postmage.enums.PostType
import com.postmage.enums.StatusCodeUtil
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
import com.postmage.service.user_posts.UserPostsInterface
import com.postmage.service.user_posts.UserPostsService
import com.postmage.util.strings.AppMessageUtil

class UserPostRepository(
    private val userPostsService: UserPostsService,
) : UserPostsInterface {

    override suspend fun addPost(
        headers: UserRequestHeaders,
        body: AddPostModel,
        addPostType: PostType
    ): ResponseData<Boolean> {
        try {
            if (addPostType == PostType.ADD_GROUP) {
                if (body.groupId.trim().isEmpty()) return sendErrorData(
                    AppMessageUtil.getAppMessages(headers.language).groupIdNotBeNull,
                    statusCode = StatusCodeUtil.BAD_REQUEST,
                )
            }

            if (body.photoList.isEmpty()) return sendErrorData(
                AppMessageUtil.getAppMessages(headers.language).photoCannotBeEmpty,
                statusCode = StatusCodeUtil.BAD_REQUEST,
            )

            if (body.photoList.any { it.photoByteArray.isEmpty() }) return sendErrorData(
                AppMessageUtil.getAppMessages(headers.language).photoCannotBeEmpty,
                statusCode = StatusCodeUtil.BAD_REQUEST,
            )

            for (i in body.photoList) {
                if (i.photoByteArray.isEmpty()) return sendErrorData(
                    AppMessageUtil.getAppMessages(headers.language).photoCannotBeEmpty,
                    statusCode = StatusCodeUtil.BAD_REQUEST,
                )
                if (i.photoName.isEmpty()) return sendErrorData(
                    AppMessageUtil.getAppMessages(headers.language).photoNameCannotBeEmpty,
                    statusCode = StatusCodeUtil.BAD_REQUEST,
                )
            }

            return userPostsService.addPost(headers, body)
        } catch (e: Exception) {
            return sendErrorData(
                AppMessageUtil.getAppMessages(headers.language).serverError,
                statusCode = StatusCodeUtil.SERVER_ERROR
            )
        }
    }

    override suspend fun getUserPostsWithUserId(headers: UserRequestHeaders, userId: String): ResponseData<List<GetUserPostModel>> {
        if (headers.tokenData.userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        if (userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        return userPostsService.getUserPostsWithUserId(headers, userId)
    }

    override suspend fun getGroupPost(
        headers: UserRequestHeaders,
        body: GroupIdModel
    ): ResponseData<List<GetUserPostModel>> {
        if (body.groupId.isNullOrEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).groupIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        return userPostsService.getGroupPost(headers, body)
    }

    override suspend fun getPost(headers: UserRequestHeaders, postId: String?): ResponseData<GetUserPostModel> {
        if (headers.tokenData.userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        if (postId.isNullOrEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).postNotFound,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        return userPostsService.getPost(headers, postId)
    }

    override suspend fun getComments(
        headers: UserRequestHeaders,
        postId: String?
    ): ResponseData<ArrayList<UserCommentModel>?> {
        if (headers.tokenData.userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        if (postId.isNullOrEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).postNotFound,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        return userPostsService.getComments(headers, postId)
    }

    override suspend fun updatePost(
        headers: UserRequestHeaders,
        body: UpdateUserPostModel
    ): ResponseData<GetUserPostModel> {
        if (body.objectId.isNullOrEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        return userPostsService.updatePost(headers, body)
    }

    override suspend fun postOfFollowedUsers(
        headers: UserRequestHeaders,
        body: PostOfFollowedUsers
    ): ResponseData<List<GetUserPostModel>> {
        if (body.limit > 100) body.limit = 100
        return userPostsService.postOfFollowedUsers(headers, body)
    }

    override suspend fun putMakePhotoPublic(
        headers: UserRequestHeaders,
        body: MakePhotoPublicRequestModel
    ): ResponseData<MakePhotoPublicResultModel> {
        if (headers.tokenData.userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        if (body.postId.isNullOrEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).postIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )
        return userPostsService.putMakePhotoPublic(headers, body)
    }

}