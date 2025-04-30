package com.postmage.repo

import com.postmage.enums.StatusCodeUtil
import com.postmage.util.extensions.writePhotoToDisk
import com.postmage.model.group.*
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData
import com.postmage.service.group.GroupInterface
import com.postmage.service.group.GroupService
import com.postmage.util.AppMessages

class GroupRepository(
    private val groupService: GroupService,
    private val appMessages: AppMessages
) : GroupInterface {
    override suspend fun createGroup(headers: UserRequestHeaders, body: CreateGroupRequestModel): ResponseData<GroupInfoModel> {
        if (body.photoBytes == null) return sendErrorData(
            appMessages.PHOTO_CANNOT_BE_EMPTY,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        if (body.photoName == null) return sendErrorData(
            appMessages.PHOTO_NAME_CANNOT_BE_EMPTY,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        if (body.groupName == null) return sendErrorData(
            appMessages.GROUP_NAME_NOT_BE_NULL,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        return groupService.createGroup(headers, body)
    }

    override suspend fun addUsersToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean> {
        if (headers.tokenData.userId == body.id) return sendErrorData(
            appMessages.WRONG_USER_ID,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )
        return groupService.addUsersToGroup(headers, body)
    }

    override suspend fun removeUsersToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean> {
        /*
        if (userId.authToDataClass()!!.userId == body.id) return sendErrorData(
            appMessages.WRONG_USER_ID,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )
         */
        return groupService.removeUsersToGroup(headers, body)
    }

    override suspend fun addAdminToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean> {
        if (headers.tokenData.userId == body.id) return sendErrorData(
            appMessages.WRONG_USER_ID,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )
        return groupService.addAdminToGroup(headers, body)
    }

    override suspend fun getMyGroupList(headers: UserRequestHeaders): ResponseData<List<GetMyGroupListResponseModel>> {
        if (headers.tokenData.userId.isEmpty()) return sendErrorData(
            appMessages.USER_ID_NOT_BE_NULL,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )
        return groupService.getMyGroupList(headers)
    }

    override suspend fun getMyGroupInfo(headers: UserRequestHeaders, groupId: String): ResponseData<List<GroupUsersModel>> {
        if (headers.tokenData.userId.isEmpty()) return sendErrorData(
            appMessages.USER_ID_NOT_BE_NULL,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )
        if (groupId.isEmpty()) return sendErrorData(
            appMessages.GROUP_ID_NOT_BE_NULL,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )
        return groupService.getMyGroupInfo(headers, groupId)
    }
}