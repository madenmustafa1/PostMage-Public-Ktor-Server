package com.postmage.model.hashtags

import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class HashtagModel(
    val id: String,
    val name: String,
    val categoryName: String?,
    val week: Int,
    val year: Int?,
    val creationTime: Long = DateUtil.getTimeNow(),
    val creationDate: String = DateUtil.getDateNow(),
    val editTime: Long,
    val totalCount: Long,
    val creatorUserId: String,
    val hashtags: ArrayList<UserHashtagModel>,
)