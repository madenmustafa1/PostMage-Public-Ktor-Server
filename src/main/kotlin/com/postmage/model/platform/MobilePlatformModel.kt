package com.postmage.model.platform

import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class MobilePlatformModel(
    val creationTime: String = DateUtil.getDateNow(),
    val ios: MobileVersionDetail,
    val android: MobileVersionDetail,
)