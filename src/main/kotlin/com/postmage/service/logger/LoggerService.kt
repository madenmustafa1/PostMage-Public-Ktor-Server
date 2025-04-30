package com.postmage.service.logger

import com.postmage.util.extensions.authToDataClass
import com.postmage.util.extensions.toMap
import com.postmage.model.app.logger.LoggerModel
import com.postmage.model.app.logger.SaveHttpLogModel
import com.postmage.mongo_client.MongoInitialize
import com.postmage.util.DateUtil
import io.ktor.server.request.*
import io.ktor.util.*

class LoggerService(
    private val mongoDB: MongoInitialize,
) : LoggerInterface {

    override suspend fun saveHttpLog(model: SaveHttpLogModel) {
        mongoDB.getHttpLog.insertOne(
            LoggerModel(
                ipAddress = model.request.local.remoteAddress,
                headers = model.request.headers.toMap(),
                url = model.request.path(),
                requestOrResponse = "",
                tokenData = model.request.headers["Authorization"]?.authToDataClass(),
                creationTime = DateUtil.getDateNow(),
                content = model.request.toMap()
            )
        )
    }

}