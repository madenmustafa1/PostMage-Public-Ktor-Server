package com.postmage.repo

import com.postmage.enums.StatusCodeUtil
import com.postmage.model.app.app_message.LanguageType
import com.postmage.util.extensions.isValidEmail
import com.postmage.model.profile.user.*
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData
import com.postmage.util.AppMessages
import com.postmage.service.profile.ProfileInterface
import com.postmage.service.profile.ProfileService
import com.postmage.util.strings.AppMessageUtil

class ProfileRepository(
    private val profileService: ProfileService,
    private val appMessages: AppMessages
): ProfileInterface {
    override suspend fun getMyProfileInfo(headers: UserRequestHeaders): ResponseData<UserProfileInfoModel?> {
        return profileService.getMyProfileInfo(headers)
    }

    override suspend fun getUserProfileInfo(
        headers: UserRequestHeaders,
        userId: String
    ): ResponseData<UserProfileInfoModel?> {
        if (userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )
        return profileService.getUserProfileInfo(headers, userId)
    }


    override suspend fun getProfileInfo(userId: String): ResponseData<UserProfileInfoModel?> {
        if (userId.isEmpty()) return sendErrorData(
            AppMessageUtil.getAppMessages(LanguageType.EN).userIdNotBeNull,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )
        return profileService.getProfileInfo(userId)
    }

    override suspend fun putMyProfileInfo(headers: UserRequestHeaders, body: UserProfileInfoModel): ResponseData<Boolean> {
        return profileService.putMyProfileInfo(headers, body)
    }

    override suspend fun getMyFollowerData(headers: UserRequestHeaders): ResponseData<GetFollowersDataModel> {
        return profileService.getMyFollowerData(headers)
    }

    override suspend fun putMyFollowerData(headers: UserRequestHeaders, body: SetFollowersDataModel): ResponseData<Boolean> {
        return profileService.putMyFollowerData(headers, body)
    }

    override suspend fun putDeleteAccount(headers: UserRequestHeaders, body: DeleteUserModel): ResponseData<Boolean> {
        if (body.mail == null || body.password == null) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        if (!body.mail.isValidEmail()) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        if (body.password.length < 4) return sendErrorData(appMessages.PASSWORD_NOT_BE_SHORT)
        return profileService.putDeleteAccount(headers, body)
    }

    override suspend fun postDeleteAccountWeb(body: DeleteUserModel): ResponseData<Boolean> {
        if (body.mail == null || body.password == null) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        if (!body.mail.isValidEmail()) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        if (body.password.length < 4) return sendErrorData(appMessages.PASSWORD_NOT_BE_SHORT)
        return profileService.postDeleteAccountWeb(body)
    }

    override suspend fun putMyProfilePhoto(headers: UserRequestHeaders, body: UpdateProfilePhotoModel): ResponseData<Boolean> {
        try {
            if (body.photoBytes == null) return sendErrorData(
                appMessages.PHOTO_CANNOT_BE_EMPTY,
                statusCode = StatusCodeUtil.BAD_REQUEST,
            )

            if (body.photoName == null) return sendErrorData(
                appMessages.PHOTO_NAME_CANNOT_BE_EMPTY,
                statusCode = StatusCodeUtil.BAD_REQUEST,
            )

            return profileService.putMyProfilePhoto(headers, body)
        } catch (e: Exception) {
            return sendErrorData(
                appMessages.SERVER_ERROR,
                statusCode = StatusCodeUtil.SERVER_ERROR
            )
        }
    }
}