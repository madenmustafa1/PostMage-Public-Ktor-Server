package com.postmage.service.notification

import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.Updates
import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.NotificationModel
import com.postmage.model.notification.NotificationType
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.mongo_client.mongo_constants.MongoSort
import com.postmage.plugins.koin
import com.postmage.service.ResponseData
import com.postmage.service.profile.ProfileService
import com.postmage.util.DateUtil
import com.postmage.util.strings.AppMessageUtil.getAppMessages
import org.bson.types.ObjectId

class NotificationService(
    private val mongoDB: MongoInitialize,
    private val profileRepository: ProfileService
) : NotificationInterface {

    override suspend fun sendNotification(headers: UserRequestHeaders, model: AppSendNotificationModel) {
        //Bildirimi giden kullanıcıya kaydet
        mongoDB.getNotifications.insertOne(
            NotificationModel(
                fromUserId = model.fromUserId,
                creationTime = DateUtil.getTimeNow(),
                notificationType = model.notificationType.value,
                userId = model.toUserId,
                notificationId = ObjectId.get().toString(),
                notificationExtraData = model.notificationExtraData
            )
        )

        var notificationModel = model.copy()

        //Mesaj body'si boşsa notification tipine göre doldur
        if (model.body == null) {
            val profile = profileRepository.getProfileInfo(model.fromUserId).data

            when (model.notificationType) {
                NotificationType.LIKE -> {
                    notificationModel = notificationModel.copy(
                        body = getAppMessages(headers.language).likedPhoto.replace(
                            "__NAME__",
                            profile?.nameSurname ?: ""
                        )
                    )
                }

                NotificationType.COMMENT -> {
                    notificationModel = notificationModel.copy(
                        body = getAppMessages(headers.language).commentPhoto.replace(
                            "__NAME__",
                            profile?.nameSurname ?: ""
                        )
                    )

                }

                NotificationType.GROUP_ADDED -> {
                    val message = getAppMessages(headers.language).groupAdded.replace(
                        "__NAME__",
                        profile?.nameSurname ?: ""
                    ).replace(
                        "__GROUP_NAME__",
                        model.notificationExtraData.name ?: ""
                    )

                    notificationModel = notificationModel.copy(
                        body = message,
                        notificationExtraData = notificationModel.notificationExtraData.copy(
                            message = message
                        )
                    )
                }

                else -> {}
            }
        }


        //Bildirimi at
        koin.appNotification.sendNotification(notificationModel)
    }


    override suspend fun getNotificationList(headers: UserRequestHeaders): ResponseData<ArrayList<NotificationModel>> {
        val notificationSize = 30
        val query = BasicDBObject("userId", headers.tokenData.userId)

        val notificationList: ArrayList<NotificationModel> = arrayListOf()
        val modifiedNotifications = ArrayList<UpdateOneModel<NotificationModel>>()

        //Kullanıcının bildirimlerini getir
        mongoDB
            .getNotifications
            .find(query)
            .sort(BasicDBObject("creationTime", MongoSort.DESC))
            .limit(notificationSize)
            .filter { !it.isDeleted }
            .forEach {
                notificationList.add(it)

                //Response'da gönderilen bildirimleri okundu olarak işaretle
                modifiedNotifications.add(
                    UpdateOneModel(
                        Filters.eq("_id", it.notificationId),
                        Updates.set("isRead", true)
                    )
                )
            }

        if (modifiedNotifications.isNotEmpty()) {
            mongoDB.getNotifications.bulkWrite(modifiedNotifications)
        }

        //Güncel bildirim atan kullanıcı bilgilerini getir
        val usersProfileInfoMap = hashMapOf<String, UserProfileInfoModel>()

        notificationList.toHashSet().forEach {
            val data = profileRepository.getProfileInfo(it.fromUserId)
            data.data?.let { profile ->
                usersProfileInfoMap[it.fromUserId] = profile
            }
        }

        //Güncel bilgileri listedeki değerlerle değiştir
        notificationList.forEach {
            usersProfileInfoMap[it.fromUserId]?.let { profileInfo ->
                it.photoUrl = profileInfo.profilePhotoUrl
                it.nameSurname = profileInfo.nameSurname
            }
        }

        return ResponseData.success(notificationList)
    }

}