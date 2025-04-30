package com.postmage.model.group

import kotlinx.serialization.Serializable

@Serializable
data class GetMyGroupListResponseModel(
    val groupName: String,
    val photoName: String,
    val totalUser: Int,
    val isAdmin: Boolean,
    val groupUsersId: ArrayList<String> = arrayListOf(),
    val groupId: String
)
