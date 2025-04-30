package com.postmage.service.user_posts

import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters.`in`
import com.postmage.enums.PostType
import com.postmage.enums.StatusCodeUtil
import com.postmage.util.extensions.findUser
import com.postmage.model.group.GroupIdModel
import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.NotificationExtraData
import com.postmage.model.notification.NotificationType
import com.postmage.model.posts.add_posts.AddPostModel
import com.postmage.model.posts.followed_users.PostOfFollowedUsers
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.posts.get_posts.GetUserPostPhotoModel
import com.postmage.model.posts.add_posts.PostHashtagsModel
import com.postmage.model.posts.make_photo_public.MakePhotoPublicRequestModel
import com.postmage.model.posts.make_photo_public.MakePhotoPublicResultModel
import com.postmage.model.posts.update_posts.UpdateUserPostModel
import com.postmage.model.posts.update_posts.UserCommentModel
import com.postmage.model.profile.user.SingleFollowerDataModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.mongo_client.mongo_constants.MongoSort
import com.postmage.repo.NotificationRepository
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.service.hashtags.HashtagsService
import com.postmage.util.extensions.launchInIO
import com.postmage.util.extensions.writePhotoToDisk
import com.postmage.util.http_util.HttpRoute
import com.postmage.util.photo_util.PhotoUtil
import com.postmage.util.strings.AppMessageUtil
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserPostsService(
    private val mongoDB: MongoInitialize,
    private val notificationRepository: NotificationRepository
) : UserPostsInterface, KoinComponent {

    private val hashtagRepository : HashtagsService by inject()

    override suspend fun addPost(
        headers: UserRequestHeaders, body: AddPostModel, addPostType: PostType
    ): ResponseData<Boolean> {
        //Find user following data
        val userIdQuery = BasicDBObject("userId", headers.tokenData.userId)
        var userProfileInfoModel: UserProfileInfoModel? = null

        val postObjectId = ObjectId.get().toString()

        //Fotoğrafı kaydet
        val photoWriteDiskResult = body.photoList.writePhotoToDisk(
            postId = postObjectId, userId = headers.tokenData.userId
        )
        if (!photoWriteDiskResult) return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).photoSaveError, statusCode = StatusCodeUtil.SERVER_ERROR
        )

        mongoDB.getUserCollection.find(userIdQuery).findUser(showPassword = false) {
            userProfileInfoModel = it
        }

        if (userProfileInfoModel == null) {
            return sendErrorData(
                AppMessageUtil.getAppMessages(headers.language).userNotFound, statusCode = StatusCodeUtil.FORBIDDEN
            )
        }

        val photoList = arrayListOf<GetUserPostPhotoModel>()
        val parentPath = PhotoUtil.getPostPath(
            postId = postObjectId, userId = headers.tokenData.userId, getDesktopDir = false
        )

        body.photoList.forEach {
            photoList.add(
                GetUserPostPhotoModel(
                    id = ObjectId.get().toString(), photoName = parentPath + "/" + it.photoName
                )
            )
        }

        val model = GetUserPostModel(
            groupId = body.groupId,
            photoList = photoList,
            description = body.description,
            objectId = postObjectId,
            creationTime = body.creationTime,
            userId = headers.tokenData.userId,
            nameSurname = userProfileInfoModel!!.nameSurname,
            hashtags = ArrayList(
                body.hashtags.map { PostHashtagsModel(hashtag = it) }
            )
        )
        var isSuccess = false

        if (body.groupId.trim().isNotEmpty()) {
            //Get <Group> collection
            val groupQuery = BasicDBObject("groupId", body.groupId)
            mongoDB.getGroupsCollection.find(groupQuery).limit(1).forEach { groupModel ->
                //Users Control
                for (i in groupModel.groupUsers) {
                    if (i?.id == headers.tokenData.userId) {
                        mongoDB.getUsersPostsCollection.insertOne(model)
                        isSuccess = true
                        break
                    }
                }
            }
        } else mongoDB.getUsersPostsCollection.insertOne(model).also { isSuccess = true }

        hashtagRepository.savePostHashtag(headers, body, postObjectId)

        return ResponseData.success(isSuccess)
    }

    override suspend fun getUserPostsWithUserId(
        headers: UserRequestHeaders, userId: String
    ): ResponseData<List<GetUserPostModel>> {

        val query = BasicDBObject("userId", userId)
        val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)
        val userPostList = arrayListOf<GetUserPostModel>()

        var profilePhotoUrl: String? = null

        var postsIsAccess = true

        mongoDB.getUserCollection.find(query).limit(1).findUser(showPassword = false) { user ->
            profilePhotoUrl = user.profilePhotoUrl

            if (headers.tokenData.userId != userId) {
                postsIsAccess = user.followers?.any { it.userId == headers.tokenData.userId } ?: false
            }
        }

        if (!postsIsAccess) {
            return sendErrorData(
                message = AppMessageUtil.getAppMessages(headers.language).accessDenied,
                statusCode = StatusCodeUtil.FORBIDDEN,
            )
        }

        mongoDB.getUsersPostsCollection.find(query).sort(sortDescQuery).filter { post -> post.isDeleted == false }
            .forEach {
                userPostList.add(it.copy(profilePhotoUrl = profilePhotoUrl))
            }

        return ResponseData.success(userPostList)
    }

    override suspend fun getGroupPost(
        headers: UserRequestHeaders, body: GroupIdModel
    ): ResponseData<List<GetUserPostModel>> {
        //val query = BasicDBObject("groupId", body.groupId!!)

        //Get <UsersPosts> collection
        val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)
        val postList = arrayListOf<GetUserPostModel>()
        mongoDB.getUsersPostsCollection.find(`in`("groupId", body.groupId!!)).sort(sortDescQuery)
            .filter { post -> post.isDeleted == false }.forEach {
                //Get <Group> collection
                mongoDB.getGroupsCollection.find(`in`("groupId", it.groupId)).forEach { groupModel ->
                    //Users Control
                    for (i in groupModel.groupUsers) {
                        it.groupName = groupModel.groupName
                        if (i?.id == headers.tokenData.userId) {
                            var model = GetUserPostModel()

                            //Get <User> collection
                            it?.userId?.let { userId ->
                                val userQuery = BasicDBObject("userId", userId)
                                mongoDB.getUserCollection.find(userQuery).limit(1)
                                    .findUser(showPassword = false) { user ->
                                        model = it.copy(
                                            profilePhotoUrl = user.profilePhotoUrl
                                        )
                                    }
                            }

                            postList.add(model)
                        }
                    }
                }
            }

        return ResponseData.success(postList)
    }

    override suspend fun getPost(headers: UserRequestHeaders, postId: String?): ResponseData<GetUserPostModel> {
        //Get <UsersPosts> collection
        val findQuery = BasicDBObject("objectId", postId!!)
        val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)

        var model: GetUserPostModel? = null
        mongoDB.getUsersPostsCollection.find(findQuery).sort(sortDescQuery).forEach { model = it }

        model?.let {
            if (it.isDeleted == true) {
                return sendErrorData(
                    message = AppMessageUtil.getAppMessages(headers.language).accessDenied,
                    statusCode = StatusCodeUtil.FORBIDDEN,
                )
            }

            //Get <User> collection
            it.userId?.let {
                val userQuery = BasicDBObject("userId", it)
                mongoDB.getUserCollection.find(userQuery).limit(1).findUser(showPassword = false) { user ->
                    model?.profilePhotoUrl = user.profilePhotoUrl
                }
            }

            return ResponseData.success(model)
        }

        return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).postNotFound, statusCode = StatusCodeUtil.BAD_REQUEST
        )
    }

    override suspend fun getComments(
        headers: UserRequestHeaders, postId: String?
    ): ResponseData<ArrayList<UserCommentModel>?> {
        //Get <UsersPosts> collection
        val findQuery = BasicDBObject("objectId", postId!!)
        val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)

        var userPostModel: GetUserPostModel? = null

        //Find post
        mongoDB.getUsersPostsCollection.find(findQuery).limit(1).sort(sortDescQuery).forEach { userPostModel = it }

        val userCommentList: ArrayList<UserCommentModel> = arrayListOf()
        userPostModel?.let { userPost ->
            userPost.comment?.forEach { comment ->
                //Find user
                val userQuery = BasicDBObject("userId", comment.userId)
                mongoDB.getUserCollection.find(userQuery).limit(1).findUser(showPassword = false) { user ->
                    comment.photoName = user.profilePhotoUrl ?: ""
                    comment.nameSurname = user.nameSurname ?: ""
                    userCommentList.add(comment)
                }
            }
            return ResponseData.success(userCommentList)
        }

        return sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).postNotFound, statusCode = StatusCodeUtil.BAD_REQUEST
        )
    }

    override suspend fun updatePost(
        headers: UserRequestHeaders, body: UpdateUserPostModel
    ): ResponseData<GetUserPostModel> {
        var model: GetUserPostModel? = null

        //Get <UsersPosts> collection
        val query = BasicDBObject("objectId", body.objectId!!)
        mongoDB.getUsersPostsCollection.find(query).limit(1).forEach { post ->
            if (body.isDeleted == true) post.isDeleted = body.isDeleted!!
            if (body.description != null) post.description = body.description!!
            if (body.comment != null) {
                if (post.comment == null) post.comment = arrayListOf(body.comment!!)
                else post.comment!!.add(body.comment!!)

                //Kendi fotoğrafını değilse yorum yaptığı kullanıcıya bildirim at
                if (body.comment!!.userId != post.userId) {
                    launchInIO {
                        notificationRepository.sendNotification(
                            headers = headers, model = AppSendNotificationModel(
                                fromUserId = body.comment!!.userId,
                                toUserId = post.userId.toString(),
                                notificationType = NotificationType.COMMENT,
                                notificationExtraData = NotificationExtraData(postId = post.objectId)
                            )
                        )
                    }
                }
            }

            if (body.likeUserId != null) {

                var userAlreadyLikedIt = false

                if (post.likeUserId == null) {
                    post.likeUserId = arrayListOf(body.likeUserId!!)
                } else {
                    //Kullanıcı postu önceden beğendiyse beğeniyi kaldırıyor
                    userAlreadyLikedIt = post.likeUserId!!.removeIf { id ->
                        id == body.likeUserId
                    }
                    if (!userAlreadyLikedIt) post.likeUserId!!.add(body.likeUserId!!)
                }

                //Kendi fotoğrafını değilse beğendiği kullanıcıya bildirim at
                if (body.likeUserId != post.userId && !userAlreadyLikedIt) {
                    launchInIO {
                        notificationRepository.sendNotification(
                            headers = headers, model = AppSendNotificationModel(
                                fromUserId = body.likeUserId!!,
                                toUserId = post.userId.toString(),
                                notificationType = NotificationType.LIKE,
                                notificationExtraData = NotificationExtraData(postId = post.objectId)
                            )
                        )
                    }
                }
            }

            mongoDB.getUsersPostsCollection.replaceOne(query, post)
            model = post
        }

        model?.let {
            return ResponseData.success(model)
        } ?: run {
            return sendErrorData(
                AppMessageUtil.getAppMessages(headers.language).postNotFound,
                statusCode = StatusCodeUtil.BAD_REQUEST,
            )
        }
    }

    override suspend fun postOfFollowedUsers(
        headers: UserRequestHeaders, body: PostOfFollowedUsers
    ): ResponseData<List<GetUserPostModel>> {

        var response: ResponseData<List<GetUserPostModel>> = sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).accessDenied,
            statusCode = StatusCodeUtil.FORBIDDEN,
        )

        //Find user following data
        val userIdQuery = BasicDBObject("userId", headers.tokenData.userId)
        mongoDB.getUserCollection.find(userIdQuery).findUser(showPassword = false) {
            //Following Data
            val followingData: ArrayList<SingleFollowerDataModel> = arrayListOf()
            followingData.addAll(it.following ?: arrayListOf())

            val userIdList: ArrayList<String> = arrayListOf()
            userIdList.add(headers.tokenData.userId)
            followingData.forEach {
                it.userId?.let { userId -> userIdList.add(userId) }
            }

            //Get posts
            val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)
            val userPostList = arrayListOf<GetUserPostModel>()
            mongoDB.getUsersPostsCollection.find(`in`("userId", userIdList)).sort(sortDescQuery)
                .filter { post -> post.isDeleted == false }.forEach {
                    var model = GetUserPostModel()
                    //Get <User> collection
                    it?.userId?.let { userId ->
                        val userQuery = BasicDBObject("userId", userId)
                        mongoDB.getUserCollection.find(userQuery).limit(1).findUser(showPassword = false) { user ->
                            model = it.copy(
                                profilePhotoUrl = user.profilePhotoUrl
                            )
                        }
                    }

                    userPostList.add(model)
                }

            response = ResponseData.success(userPostList)
        }

        return response
    }

    override suspend fun putMakePhotoPublic(
        headers: UserRequestHeaders, body: MakePhotoPublicRequestModel
    ): ResponseData<MakePhotoPublicResultModel> {

        //Get <UsersPosts> collection
        val query = BasicDBObject("objectId", body.postId!!)

        var response: ResponseData<MakePhotoPublicResultModel> = sendErrorData(
            AppMessageUtil.getAppMessages(headers.language).postNotFound,
            statusCode = StatusCodeUtil.BAD_REQUEST,
        )

        mongoDB.getUsersPostsCollection.find(query).limit(1).forEach { post ->
            if (post.isDeleted == true || post.userId != headers.tokenData.userId) return@forEach


            //Make visible
            if (body.isVisible) {
                val url = HttpRoute.BASE_URL + HttpRoute.PUBLIC + HttpRoute.PHOTO + "?postId=" + post.objectId
                mongoDB.getUsersPostsCollection.replaceOne(query, post.copy(postIsPublic = true))

                response = ResponseData.success(
                    MakePhotoPublicResultModel(
                        isSuccess = true, postUrl = url
                    )
                )
            }

            //Make invisible
            if (!body.isVisible) {
                mongoDB.getUsersPostsCollection.replaceOne(query, post.copy(postIsPublic = false))
                response = ResponseData.success(
                    MakePhotoPublicResultModel(
                        isSuccess = true, postUrl = ""
                    )
                )
            }
        }

        return response
    }
}