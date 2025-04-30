package com.postmage.model.app.security

import org.bson.types.ObjectId

data class SecurityCollectionModel(
    val accessToken: String? = null,
    var enable: Boolean = false,
    val userId: String? = null,
    val objectId: String? = null
)
