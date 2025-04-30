package com.postmage.model.profile.user

import kotlinx.serialization.Serializable

@Serializable
data class SingleFollowerDataModel(
    val nameSurname: String? = null,
    val userId: String? = null,
    val photoUrl: String? = null
)

@Serializable
data class GetFollowersDataModel(
    val following: ArrayList<SingleFollowerDataModel>? = null,
    val followers: ArrayList<SingleFollowerDataModel>? = null,
)

@Serializable
data class SetFollowersDataModel(
    val following: SingleFollowerDataModel? = null,
    val followers: SingleFollowerDataModel? = null,
)