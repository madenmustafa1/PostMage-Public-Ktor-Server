package com.postmage.service.profile

import com.postmage.model.profile.user.*
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData

interface ProfileInterface {

    suspend fun getMyProfileInfo(headers: UserRequestHeaders): ResponseData<UserProfileInfoModel?>

    suspend fun getUserProfileInfo(headers: UserRequestHeaders, userId: String): ResponseData<UserProfileInfoModel?>


    suspend fun getProfileInfo(userId: String): ResponseData<UserProfileInfoModel?>

    suspend fun putMyProfileInfo(headers: UserRequestHeaders, body: UserProfileInfoModel): ResponseData<Boolean>

    suspend fun getMyFollowerData(headers: UserRequestHeaders): ResponseData<GetFollowersDataModel>

    suspend fun putMyFollowerData(headers: UserRequestHeaders, body: SetFollowersDataModel): ResponseData<Boolean>

    suspend fun putMyProfilePhoto(headers: UserRequestHeaders, body: UpdateProfilePhotoModel): ResponseData<Boolean>

    suspend fun putDeleteAccount(headers: UserRequestHeaders, body: DeleteUserModel): ResponseData<Boolean>

    suspend fun postDeleteAccountWeb(body: DeleteUserModel): ResponseData<Boolean>

}