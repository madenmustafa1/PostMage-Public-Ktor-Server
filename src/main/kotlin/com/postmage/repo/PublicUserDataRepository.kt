package com.postmage.repo

import com.postmage.enums.StatusCodeUtil
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.service.ResponseData
import com.postmage.service.public_user.PublicUserDataInterface
import com.postmage.service.public_user.PublicUserDataService
import com.postmage.util.strings.AppMessageUtil
import java.io.File

class PublicUserDataRepository(
    private val publicUserDataService: PublicUserDataService,
) : PublicUserDataInterface {
    override suspend fun getPostPhoto(postId: String): ResponseData<File> {
        if (postId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(LanguageType.EN).photoCannotBeEmpty,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        return publicUserDataService.getPostPhoto(postId = postId)
    }

    override suspend fun getPublicPhoto(userId: String, photoName: String): ResponseData<File> {
        if (photoName.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(LanguageType.EN).photoCannotBeEmpty,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        if (userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(LanguageType.EN).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        return publicUserDataService.getPublicPhoto(userId = userId, photoName = photoName)
    }

    override suspend fun getUserProfileInfoWithUsername(username: String): ResponseData<UserProfileInfoModel?> {
        if (username.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(LanguageType.EN).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )
        return publicUserDataService.getUserProfileInfoWithUsername(username)
    }

    override suspend fun getUserPostsWithUserName(username: String): ResponseData<List<GetUserPostModel>> {
        if (username.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(LanguageType.EN).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )
        return publicUserDataService.getUserPostsWithUserName(username)
    }
}