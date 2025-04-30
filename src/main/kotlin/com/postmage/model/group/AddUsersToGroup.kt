package com.postmage.model.group

import kotlinx.serialization.Serializable

@Serializable
data class UsersToGroupModel(
    val id: String,
    val groupId: String
)
