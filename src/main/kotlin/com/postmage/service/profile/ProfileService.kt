package com.postmage.service.profile

import com.mongodb.BasicDBObject
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.app.app_message.LanguageType
import com.postmage.util.extensions.findUser
import com.postmage.model.profile.user.*
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.util.DateUtil
import com.postmage.util.extensions.writePhotoToDisk
import com.postmage.util.http_util.verifyPassword
import com.postmage.util.photo_util.PhotoUtil
import com.postmage.util.strings.AppMessageUtil.getAppMessages
import org.litote.kmongo.div
import org.litote.kmongo.eq

class ProfileService(
    private val mongoDB: MongoInitialize
) : ProfileInterface {

    override suspend fun getProfileInfo(userId: String): ResponseData<UserProfileInfoModel?> {
        val query = BasicDBObject("userId", userId)

        var result: ResponseData<UserProfileInfoModel?>? = null

        mongoDB.getUserCollection
            .find(query)
            .limit(1)
            .findUser(showPassword = false) {
                result = ResponseData.success(it)
            }

        result?.let { return it }

        return sendErrorData(getAppMessages(LanguageType.EN).userNotFound)
    }

    override suspend fun getMyProfileInfo(headers: UserRequestHeaders): ResponseData<UserProfileInfoModel?> {
        val profile = getProfileInfo(headers.tokenData.userId)
        if (profile.data == null) return sendErrorData(getAppMessages(headers.language).userNotFound)
        return profile
    }

    //662b6340bc03aa6a563f6ef0
    override suspend fun getUserProfileInfo(
        headers: UserRequestHeaders,
        userId: String
    ): ResponseData<UserProfileInfoModel?> {
        val profile = getProfileInfo(userId)

        if (profile.data == null) return sendErrorData(getAppMessages(headers.language).userNotFound)

        //Kullanıcı başkasının profiline bakmak istiyorsa takip ediyor mu diye kontrol ediyor
        if (headers.tokenData.userId != userId) {
            if (profile.data.followers == null) return sendErrorData(getAppMessages(headers.language).accessDenied)

            val userFound = profile.data.followers.any { it.userId == headers.tokenData.userId }

            if (!userFound) return sendErrorData(getAppMessages(headers.language).accessDenied)
        }

        return profile
    }


    override suspend fun putMyProfileInfo(
        headers: UserRequestHeaders,
        body: UserProfileInfoModel
    ): ResponseData<Boolean> {
        val collection = mongoDB.getUserCollection
        val query = BasicDBObject("userId", headers.tokenData.userId)

        var result: ResponseData<Boolean>? = null

        collection.find(query).limit(1).findUser(showPassword = true) {
            if (it.userId != headers.tokenData.userId) {
                result = sendErrorData(getAppMessages(headers.language).accessDenied)
                return@findUser
            }
            body.groups?.let { group -> it.groups?.addAll(group) }
            body.gender?.let { gender -> it.gender = gender }
            body.phoneNumber?.let { phoneNumber -> it.phoneNumber = phoneNumber }
            body.nameSurname?.let { nameSurname -> it.nameSurname = nameSurname }
            body.followingSize?.let { followingSize -> it.followingSize = followingSize }
            body.followersSize?.let { followersSize -> it.followersSize = followersSize }
            body.profilePhotoUrl?.let { profilePhotoUrl -> it.profilePhotoUrl = profilePhotoUrl }

            body.userName?.let { userNameModel ->
                if (it.userName == null) {
                    userNameModel.editTime = DateUtil.getTimeNow()
                    it.userName = userNameModel

                    return@let
                }
                val username = userNameModel.userName?.trim()
                //Aynı kullanıcı adıysa işlem yapma
                if (it.userName!!.userName == username) {
                    return@let
                }

                val usernameIsEmpty =
                    collection.find(UserProfileInfoModel::userName / ProfileUsernameModel::userName eq username)
                        .limit(1).first()

                //Username kullanılıyorsa işlem yapma
                if (usernameIsEmpty != null) {
                    return@findUser
                }

                val previousUsernameModel = it.userName?.previousUsernames ?: arrayListOf()
                previousUsernameModel.add(
                    ProfilePreviousUsernameModel(
                        editTime = it.userName!!.editTime,
                        userName = it.userName!!.userName
                    )
                )

                it.userName!!.accessibility = userNameModel.accessibility
                it.userName!!.userName = username
                it.userName!!.editTime = DateUtil.getTimeNow()
                it.userName!!.previousUsernames = previousUsernameModel
            }

            collection.replaceOne(query, it)
            result = ResponseData.success(true)
        }
        result?.let { return it }

        return sendErrorData(getAppMessages(headers.language).userNotFound)
    }

    override suspend fun getMyFollowerData(headers: UserRequestHeaders): ResponseData<GetFollowersDataModel> {
        val userCollection = mongoDB.getUserCollection
        val collection = mongoDB.getUserCollection
        val query = BasicDBObject("userId", headers.tokenData.userId)

        var result: ResponseData<GetFollowersDataModel> = sendErrorData(
            getAppMessages(headers.language).userNotFound,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        val following: ArrayList<SingleFollowerDataModel> = arrayListOf()
        val followers: ArrayList<SingleFollowerDataModel> = arrayListOf()

        collection.find(query).limit(1).findUser(showPassword = false) {
            it.following?.forEach { otherUser ->
                otherUser.userId?.let {
                    //Get <User> collection
                    val userQuery = BasicDBObject("userId", otherUser.userId)

                    userCollection.find(userQuery).limit(1).forEach { user ->
                        following.add(
                            SingleFollowerDataModel(
                                nameSurname = user.nameSurname ?: "",
                                userId = user.userId ?: "",
                                photoUrl = user.profilePhotoUrl
                            )
                        )
                    }
                }
            }

            it.followers?.forEach { otherUser ->
                otherUser.userId?.let {
                    //Get <User> collection
                    val userQuery = BasicDBObject("userId", otherUser.userId)

                    userCollection.find(userQuery).limit(1).forEach { user ->
                        followers.add(
                            SingleFollowerDataModel(
                                nameSurname = user.nameSurname ?: "",
                                userId = user.userId ?: "",
                                photoUrl = user.profilePhotoUrl
                            )
                        )
                    }
                }
            }

            result = ResponseData.success(
                GetFollowersDataModel(
                    following = following,
                    followers = followers,
                )
            )
        }

        return result
    }

    override suspend fun putMyFollowerData(
        headers: UserRequestHeaders,
        body: SetFollowersDataModel
    ): ResponseData<Boolean> {
        val collection = mongoDB.getUserCollection
        val query = BasicDBObject("userId", headers.tokenData.userId)

        var result: ResponseData<Boolean>? = null

        collection.find(query).limit(1).findUser(showPassword = true) { model ->
            body.followers?.let { followers ->
                model.followers?.removeIf { model.userId == followers.userId }
                model.followers?.add(SingleFollowerDataModel(userId = followers.userId))
            }

            body.following?.let { following ->
                model.following?.removeIf { model.userId == following.userId }
                model.following?.add(SingleFollowerDataModel(userId = following.userId))

                //Find following user
                following.userId?.let { followingUserId ->
                    val fQuery = BasicDBObject("userId", followingUserId)
                    //Put followers data
                    collection.find(fQuery).limit(1).findUser(showPassword = true) { followingUserModel ->
                        followingUserModel.followers?.add(
                            SingleFollowerDataModel(userId = headers.tokenData.userId)
                        )

                        collection.replaceOne(fQuery, followingUserModel)
                    }
                }
            }

            collection.replaceOne(query, model)
            result = ResponseData.success(true)
        }

        result?.let { return it }

        return sendErrorData(getAppMessages(headers.language).userNotFound)
    }

    override suspend fun putMyProfilePhoto(
        headers: UserRequestHeaders,
        body: UpdateProfilePhotoModel
    ): ResponseData<Boolean> {
        val query = BasicDBObject("userId", headers.tokenData.userId)
        var isSuccess = false

        val result = body.photoBytes!!.writePhotoToDisk(
            postId = "profilePhoto",
            photoName = body.photoName!!,
            userId = headers.tokenData.userId
        )
        if (!result) return sendErrorData(
            getAppMessages(headers.language).serverError,
            statusCode = StatusCodeUtil.SERVER_ERROR
        )

        val parentPath = PhotoUtil.getPostPath(
            postId = "profilePhoto",
            userId = headers.tokenData.userId,
            getDesktopDir = false
        )

        mongoDB.getUserCollection.find(query).limit(1).findUser(showPassword = true) { model ->
            model.profilePhotoUrl = parentPath + "/" + body.photoName
            mongoDB.getUserCollection.replaceOne(query, model)
            isSuccess = true
        }

        return ResponseData.success(isSuccess)
    }

    override suspend fun putDeleteAccount(headers: UserRequestHeaders, body: DeleteUserModel): ResponseData<Boolean> {
        val query = BasicDBObject("userId", headers.tokenData.userId)
        var result: ResponseData<Boolean> = sendErrorData(
            getAppMessages(headers.language).userNotFound,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        mongoDB.getUserCollection.find(query)
            .limit(1).findUser(showPassword = true) { model ->
                if (body.mail!!.trim() != model.mail!!.trim()) {
                    result = sendErrorData(
                        getAppMessages(headers.language).emailOrPasswordIncorrect,
                        statusCode = StatusCodeUtil.BAD_REQUEST
                    )
                    return@findUser
                }

                val passwordVerified = body.password!!.verifyPassword(expected = model.password!!)
                if (!passwordVerified) {
                    result = sendErrorData(
                        getAppMessages(headers.language).emailOrPasswordIncorrect,
                        statusCode = StatusCodeUtil.BAD_REQUEST
                    )
                    return@findUser
                }

                model.isDeleted = true
                mongoDB.getUserCollection.replaceOne(query, model)
                result = ResponseData.success(true)
            }

        return result
    }

    override suspend fun postDeleteAccountWeb(body: DeleteUserModel): ResponseData<Boolean> {
        val appMessage = getAppMessages(LanguageType.EN)
        val query = BasicDBObject("mail", body.mail!!)
        var result: ResponseData<Boolean> = sendErrorData(
            appMessage.userNotFound,
            statusCode = StatusCodeUtil.BAD_REQUEST
        )

        mongoDB.getUserCollection.find(query)
            .limit(1)
            .findUser(showPassword = true) { model ->
                if (body.mail.trim() != model.mail!!.trim()) {
                    result = sendErrorData(
                        appMessage.emailOrPasswordIncorrect,
                        statusCode = StatusCodeUtil.BAD_REQUEST
                    )
                    return@findUser
                }

                val passwordVerified = body.password!!.verifyPassword(expected = model.password!!)
                if (!passwordVerified) {
                    result = sendErrorData(
                        appMessage.emailOrPasswordIncorrect,
                        statusCode = StatusCodeUtil.BAD_REQUEST
                    )
                    return@findUser
                }

                model.isDeleted = true
                mongoDB.getUserCollection.replaceOne(query, model)
                result = ResponseData.success(true)
            }

        return result
    }
}