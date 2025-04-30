package com.postmage.model.notification

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class NotificationModel(
    val fromUserId: String,
    var creationTime: Long? = null,
    var nameSurname: String? = null,
    var photoUrl: String? = null,
    @BsonId val notificationId: String,
    var notificationType: Int? = NotificationType.UNKNOWN.value,
    var isRead: Boolean = false,
    var userId: String,
    var isDeleted: Boolean = false,
    var notificationExtraData: NotificationExtraData? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotificationModel) return false

        return fromUserId == other.fromUserId
    }

    override fun hashCode(): Int {
        return fromUserId.hashCode()
    }

}