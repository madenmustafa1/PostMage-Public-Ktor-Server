package com.postmage.model.group

import kotlinx.serialization.Serializable

@Serializable
data class UserGroupPost(
    val groupIds: ArrayList<String>?
)