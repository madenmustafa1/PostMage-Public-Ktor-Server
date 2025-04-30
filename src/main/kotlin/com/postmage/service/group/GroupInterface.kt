package com.postmage.service.group

import com.postmage.model.group.*
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData

interface GroupInterface {

    suspend fun createGroup(headers: UserRequestHeaders, body: CreateGroupRequestModel): ResponseData<GroupInfoModel>
    suspend fun addUsersToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean>
    suspend fun removeUsersToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean>
    suspend fun addAdminToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean>
    suspend fun getMyGroupList(headers: UserRequestHeaders): ResponseData<List<GetMyGroupListResponseModel>>
    suspend fun getMyGroupInfo(headers: UserRequestHeaders, groupId: String): ResponseData<List<GroupUsersModel>>
}