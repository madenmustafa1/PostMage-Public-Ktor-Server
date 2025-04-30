package com.postmage.model.login.sign_up

import com.postmage.enums.AppUserRole
import com.postmage.model.profile.user.SingleFollowerDataModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable
import kotlin.collections.ArrayList

@Serializable
data class SignUpRequestModel(
    val nameSurname: String,
    val mail: String,
    var password: String,
    val phoneNumber: String? = null,
    val gender: Int? = null,
    val profilePhotoUrl: String? = null,
    var userId: String? = null,
    val userRole: Int? = AppUserRole.USER.ordinal,
    val following: ArrayList<SingleFollowerDataModel> = arrayListOf(),
    val followers: ArrayList<SingleFollowerDataModel> = arrayListOf(),
)

fun SignUpRequestModel.toUserProfileInfoModel(): UserProfileInfoModel {
    return UserProfileInfoModel(
        nameSurname = this.nameSurname,
        password = this.password,
        mail = this.mail,
        phoneNumber = this.phoneNumber,
        gender = this.gender,
        creationTime = DateUtil.getTimeNow(),
        userId = this.userId,
        userRole = this.userRole,
        groups = arrayListOf(),
        following = arrayListOf(),
        followers = arrayListOf(),
        profilePhotoUrl = this.profilePhotoUrl,
        isDeleted = false
    )
}
