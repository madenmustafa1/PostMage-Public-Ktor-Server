package com.postmage.model.group

import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class GroupInfoModel(
    val groupId: String? = null,
    val groupName: String = "",
    var description: String = "",
    val photoName: String? = null,
    val adminIds: ArrayList<String> = arrayListOf(),
    val creationTime: Long = DateUtil.getTimeNow(),
    val groupUsers: ArrayList<GroupUsersModel?> = arrayListOf(),
)

@Serializable
data class GroupUsersModel(
    val name: String = "",
    val profileUrl: String? = null,
    val id: String = ""
)

@Serializable
data class GroupIdModel(
    val groupId: ArrayList<String?>? = arrayListOf()
)
