package com.postmage.model.app.logger

import com.postmage.model.login.token.TokenDataModel
import com.postmage.util.DateUtil
import org.bson.types.ObjectId

data class LoggerModel(
    val ipAddress: String? = null,
    val headers: Map<String, Any?>? = null,
    val url: String,
    val requestOrResponse: String? = null,
    val objectId: ObjectId = ObjectId.get(),
    val creationTime: String? = null,
    val tokenData: TokenDataModel? = null,
    val content: Map<String, Any?>? = null
)