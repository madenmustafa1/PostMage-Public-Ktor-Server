package com.postmage.mongo_client

import com.mongodb.client.MongoClient
import com.postmage.BUILD_TYPE
import com.postmage.model.app.build_type.AppBuildType
import com.postmage.model.group.GroupInfoModel
import com.postmage.model.app.logger.CrashLogModel
import com.postmage.model.app.logger.LoggerModel
import com.postmage.model.notification.NotificationModel
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.model.app.security.SecurityCollectionModel
import com.postmage.model.platform.MobilePlatformModel
import com.postmage.model.hashtags.HashtagModel
import com.postmage.mongo_client.db_router.DBRouter
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class MongoInitialize {

    //private val client = KMongo.createClient("mongodb://172.17.0.3")
    //private val client = KMongo.createClient() //Local

    private fun getMongoClient(): MongoClient {
        return when (BUILD_TYPE) {
            AppBuildType.DEV, AppBuildType.TEST -> KMongo.createClient() //Local
            AppBuildType.PROD -> KMongo.createClient("mongodb://172.17.0.3")
        }
    }

    private val database = getMongoClient().getDatabase(DBRouter.DB_NAME)
    val getUserCollection = database.getCollection<UserProfileInfoModel>(DBRouter.USERS)
    val getUsersPostsCollection = database.getCollection<GetUserPostModel>(DBRouter.USERS_POSTS)
    val getGroupsCollection = database.getCollection<GroupInfoModel>(DBRouter.GROUPS)
    val getSecurityCollection = database.getCollection<SecurityCollectionModel>(DBRouter.SECURITY)
    val getHttpLog = database.getCollection<LoggerModel>(DBRouter.HTTP_LOG)
    val getNotifications = database.getCollection<NotificationModel>(DBRouter.NOTIFICATIONS)
    val getCrashLog = database.getCollection<CrashLogModel>(DBRouter.CRASH_LOG)
    val getMobilePlatform = database.getCollection<MobilePlatformModel>(DBRouter.MOBILE_PLATFORM)
    val getHashtags = database.getCollection<HashtagModel>(DBRouter.HASHTAGS)
}