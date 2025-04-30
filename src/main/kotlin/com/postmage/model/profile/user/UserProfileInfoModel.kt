package com.postmage.model.profile.user

import com.postmage.enums.AppUserRole
import com.postmage.model.group.GroupInfoModel
import kotlinx.serialization.Serializable

// TODO: Burayı güvenli yap. Password herhangi bir yerden DB'de değiştirilemesin veya NULL atanmasın. Birinci öncelik!
@Serializable
data class UserProfileInfoModel(
    var nameSurname: String? = null,
    var mail: String? = null,
    var phoneNumber: String? = null,
    var gender: Int? = null,
    var profilePhotoUrl: String? = null,
    var userName: ProfileUsernameModel? = null,
    var userId: String? = null,
    var groups: ArrayList<GroupInfoModel?>? = null,
    var followersSize: Int? = null,
    var followingSize: Int? = null,
    var userRole: Int? = AppUserRole.USER.ordinal,
    var password: String? = null,
    val following: ArrayList<SingleFollowerDataModel>? = arrayListOf(),
    val followers: ArrayList<SingleFollowerDataModel>? = arrayListOf(),
    var creationTime: Long? = null,
    var isDeleted: Boolean? = false,
    var isEmailVerified: Boolean? = false
)
