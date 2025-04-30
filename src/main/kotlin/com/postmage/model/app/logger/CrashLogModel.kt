package com.postmage.model.app.logger

import org.bson.types.ObjectId

data class CrashLogModel(
    val stackTrace: List<StackTraceElement>?,
    val message: String?,
    val objectId: ObjectId = ObjectId.get(),
    val creationTime: String? = null
)