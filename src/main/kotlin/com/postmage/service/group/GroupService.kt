package com.postmage.service.group

import com.mongodb.BasicDBObject
import com.postmage.enums.StatusCodeUtil
import com.postmage.util.extensions.findUser
import com.postmage.model.group.*
import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.NotificationExtraData
import com.postmage.model.notification.NotificationType
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.repo.NotificationRepository
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.util.AppMessages
import com.postmage.util.extensions.launchInIO
import com.postmage.util.extensions.writePhotoToDisk
import com.postmage.util.photo_util.PhotoUtil
import org.bson.types.ObjectId

class GroupService(
    private val mongoDB: MongoInitialize,
    private val appMessages: AppMessages,
    private val notificationRepository: NotificationRepository
) : GroupInterface {

    override suspend fun createGroup(
        headers: UserRequestHeaders,
        body: CreateGroupRequestModel
    ): ResponseData<GroupInfoModel> {
        var model: GroupInfoModel? = null
        var isSuccess = false

        val postObjectId = ObjectId.get().toString()

        //Fotoğrafı kaydet
        val photoWriteDiskResult = body.photoBytes!!.writePhotoToDisk(
            postId = postObjectId,
            photoName = body.photoName!!,
            userId = headers.tokenData.userId
        )
        if (!photoWriteDiskResult) return sendErrorData(
            appMessages.PHOTO_SAVE_ERROR,
            statusCode = StatusCodeUtil.SERVER_ERROR
        )

        val parentPath = PhotoUtil.getPostPath(
            postId = postObjectId,
            userId = headers.tokenData.userId,
            getDesktopDir = false
        )

        //Get <User> collection
        val query = BasicDBObject("userId", headers.tokenData.userId)
        mongoDB.getUserCollection.find(query).limit(1).findUser(showPassword = true) { userModel ->
            model = GroupInfoModel(
                groupId = ObjectId.get().toString(),
                photoName = parentPath + "/" + body.photoName,
                description = body.description,
                //creationTime = body.creationTime,
                groupUsers = arrayListOf(
                    //Add admin
                    GroupUsersModel(
                        name = userModel.nameSurname ?: "",
                        id = headers.tokenData.userId,
                        profileUrl = userModel.profilePhotoUrl ?: ""
                    )
                ),
                groupName = body.groupName!!,
                adminIds = arrayListOf(headers.tokenData.userId),
            )
            //Set <Group> collection
            mongoDB.getGroupsCollection.insertOne(model!!)

            //Set <User> collection
            userModel.groups?.add(model!!)
            mongoDB.getUserCollection.replaceOne(query, userModel)
            isSuccess = true
        }

        if (isSuccess) return ResponseData.success(model)

        return sendErrorData(appMessages.USER_NOT_FOUND, StatusCodeUtil.BAD_REQUEST)
    }

    override suspend fun addUsersToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean> {
        var isSuccess = false
        //Get <Group> collection
        val groupQuery = BasicDBObject("groupId", body.groupId)
        mongoDB.getGroupsCollection.find(groupQuery).limit(1).forEach { groupModel ->
            //Admin Control
            for (i in groupModel.adminIds) {
                if (i == headers.tokenData.userId) {
                    //Find users <User> collection
                    val userQuery = BasicDBObject("userId", body.id)
                    mongoDB.getUserCollection.find(userQuery).limit(1).findUser(showPassword = true) { userModel ->
                        //Add users
                        val model = GroupUsersModel(
                            name = userModel.nameSurname ?: "",
                            id = userModel.userId!!,
                            profileUrl = userModel.profilePhotoUrl ?: ""
                        )

                        //Duplicate <Group> group control
                        groupModel.groupUsers
                            .removeIf { it?.id == body.id }
                            .also {
                                groupModel.groupUsers.add(model)
                            }

                        //Set <Group> collection
                        mongoDB.getGroupsCollection.replaceOne(groupQuery, groupModel)

                        //Duplicate <User> group control
                        userModel.groups?.let { _ ->
                            userModel.groups!!
                                .removeIf { it?.groupId == body.groupId }
                                .also {
                                    userModel.groups!!.add(groupModel)
                                }
                        }

                        mongoDB.getUserCollection.replaceOne(userQuery, userModel)

                        launchInIO {
                            //Kullanıcı gruba eklendiğinde bildirim gönder
                            notificationRepository.sendNotification(
                                headers = headers,
                                model = AppSendNotificationModel(
                                    fromUserId = headers.tokenData.userId,
                                    toUserId = userModel.userId!!,
                                    notificationType = NotificationType.GROUP_ADDED,
                                    notificationExtraData = NotificationExtraData(
                                        groupId = body.groupId,
                                        name = groupModel.groupName
                                    )
                                )
                            )
                        }
                    }
                    isSuccess = true
                    break
                }
            }
        }

        if (isSuccess) return ResponseData.success(true)

        return sendErrorData(
            appMessages.ACCESS_DENIED,
            statusCode = StatusCodeUtil.FORBIDDEN
        )
    }

    override suspend fun removeUsersToGroup(
        headers: UserRequestHeaders,
        body: UsersToGroupModel
    ): ResponseData<Boolean> {
        var isSuccess = false
        //Get <Group> collection
        val groupQuery = BasicDBObject("groupId", body.groupId)
        mongoDB.getGroupsCollection.find(groupQuery).limit(1).forEach { groupModel ->
            //Admin Control
            for (i in groupModel.adminIds) {
                if (i == headers.tokenData.userId) {
                    //Set <Group> collection
                    groupModel.groupUsers.removeIf { it?.id == body.id }
                    groupModel.adminIds.removeIf { it == body.id }

                    //Eğer gruptan son admin çıkarsa bütün kullanıcılar admin yapılıyor.
                    if (groupModel.adminIds.isEmpty()) {
                        groupModel.groupUsers.forEach {
                            it?.id?.let { groupUserId -> groupModel.adminIds.add(groupUserId) }
                        }
                    }

                    mongoDB.getGroupsCollection.replaceOne(groupQuery, groupModel)

                    //Find users <User> collection
                    val userQuery = BasicDBObject("userId", body.id)
                    mongoDB.getUserCollection.find(userQuery).limit(1).findUser(showPassword = true) { userModel ->
                        //Set <User> group control
                        userModel.groups?.let { _ ->
                            userModel.groups!!.removeIf { it?.groupId == body.groupId }
                        }
                        mongoDB.getUserCollection.replaceOne(userQuery, userModel)
                    }
                    isSuccess = true
                    break
                }
            }
        }

        if (isSuccess) return ResponseData.success(true)

        return sendErrorData(
            appMessages.ACCESS_DENIED,
            statusCode = StatusCodeUtil.FORBIDDEN
        )
    }

    override suspend fun addAdminToGroup(headers: UserRequestHeaders, body: UsersToGroupModel): ResponseData<Boolean> {
        var isSuccess = false
        //Get <Group> collection
        val groupQuery = BasicDBObject("groupId", body.groupId)
        mongoDB.getGroupsCollection.find(groupQuery).limit(1).forEach { groupModel ->
            //Admin Control
            for (i in groupModel.adminIds) {
                if (i == headers.tokenData.userId) {
                    //Set <Group> collection
                    groupModel
                        .adminIds.removeIf { it == body.id }
                        .also { groupModel.adminIds.add(body.id) }

                    mongoDB.getGroupsCollection.replaceOne(groupQuery, groupModel)

                    isSuccess = true
                    break
                }
            }
        }

        if (isSuccess) return ResponseData.success(true)

        return sendErrorData(
            appMessages.ACCESS_DENIED,
            statusCode = StatusCodeUtil.FORBIDDEN
        )
    }

    override suspend fun getMyGroupList(headers: UserRequestHeaders): ResponseData<List<GetMyGroupListResponseModel>> {
        val query = BasicDBObject("userId", headers.tokenData.userId)

        val groupList = arrayListOf<GetMyGroupListResponseModel>()
        //Get <User> collection
        mongoDB.getUserCollection.find(query).limit(1).findUser(showPassword = false) {
            it.groups?.forEach { group ->
                val groupUsersId = arrayListOf<String>()

                group?.let {

                    for (groupUsers in group.groupUsers) {
                        groupUsers?.let {
                            groupUsersId.add(groupUsers.id)
                        }
                    }

                    groupList.add(
                        GetMyGroupListResponseModel(
                            groupName = group.groupName,
                            photoName = group.photoName ?: "",
                            groupUsersId = groupUsersId,
                            isAdmin = group.adminIds.contains(headers.tokenData.userId),
                            totalUser = group.groupUsers.size,
                            groupId = group.groupId ?: ""
                        )
                    )
                }
            }
        }

        return ResponseData.success(groupList)
    }

    override suspend fun getMyGroupInfo(
        headers: UserRequestHeaders,
        groupId: String
    ): ResponseData<List<GroupUsersModel>> {
        val userCollection = mongoDB.getUserCollection

        val groupCollection = mongoDB.getGroupsCollection
        val groupQuery = BasicDBObject("groupId", groupId)

        val groupUsersModel = arrayListOf<GroupUsersModel>()

        //Get <Group> collection
        groupCollection.find(groupQuery).limit(1).forEach {
            it.groupUsers.forEach { groupUser ->
                //Get <User> collection
                val userQuery = BasicDBObject("userId", groupUser?.id ?: "-1")

                userCollection.find(userQuery).limit(1).findUser(showPassword = false) { user ->
                    groupUsersModel.add(
                        GroupUsersModel(
                            name = user.nameSurname ?: "",
                            id = user.userId ?: "",
                            profileUrl = user.profilePhotoUrl
                        )
                    )
                }

            }
        }

        return ResponseData.success(groupUsersModel)
    }
}