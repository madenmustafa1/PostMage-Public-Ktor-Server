package com.postmage.model.group

import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupRequestModel(
    var groupName: String? = null,
    var description: String = "",
    var adminIds: ArrayList<String> = arrayListOf(),
    var creationTime: Long? = DateUtil.getTimeNow(),
    var groupUsers: ArrayList<GroupUsersModel> = arrayListOf(),
    var photoName: String? = null,
    var photoBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateGroupRequestModel

        if (groupName != other.groupName) return false
        if (description != other.description) return false
        if (adminIds != other.adminIds) return false
        if (creationTime != other.creationTime) return false
        if (groupUsers != other.groupUsers) return false
        if (photoName != other.photoName) return false
        if (photoBytes != null) {
            if (other.photoBytes == null) return false
            if (!photoBytes.contentEquals(other.photoBytes)) return false
        } else if (other.photoBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupName?.hashCode() ?: 0
        result = 31 * result + description.hashCode()
        result = 31 * result + adminIds.hashCode()
        result = 31 * result + (creationTime?.hashCode() ?: 0)
        result = 31 * result + groupUsers.hashCode()
        result = 31 * result + (photoName?.hashCode() ?: 0)
        result = 31 * result + (photoBytes?.contentHashCode() ?: 0)
        return result
    }

}
