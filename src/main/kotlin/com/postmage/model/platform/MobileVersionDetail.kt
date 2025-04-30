package com.postmage.model.platform

import kotlinx.serialization.Serializable

@Serializable
data class MobileVersionDetail(
    val versionCode: Int,
    val versionName: String,
    val versionUpdateRequired: Boolean
)