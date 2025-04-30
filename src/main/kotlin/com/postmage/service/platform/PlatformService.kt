package com.postmage.service.platform

import com.mongodb.BasicDBObject
import com.postmage.model.platform.MobilePlatformModel
import com.postmage.model.platform.MobileVersionDetail
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.mongo_client.mongo_constants.MongoSort
import com.postmage.service.ResponseData

class PlatformService(
    private val mongoDB: MongoInitialize,
) : PlatformInterface {
    override suspend fun getCurrentMobileVersion(headers: UserRequestHeaders): ResponseData<MobilePlatformModel> {
        val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)
        var result: ResponseData<MobilePlatformModel>? = null

        mongoDB.getMobilePlatform.find().sort(sortDescQuery).limit(1).forEach {
            result = ResponseData.success(it)
        }

        if (result != null) return result!!

        //Eğer mobil version oluşturulmadıysa oluştur.
        val mobileVersionDetail = MobileVersionDetail(
            versionCode = 1,
            versionName = "-1.0.0",
            versionUpdateRequired = false
        )

        val mobilePlatformModel = MobilePlatformModel(
            ios = mobileVersionDetail,
            android = mobileVersionDetail,
        )

        mongoDB.getMobilePlatform.insertOne(mobilePlatformModel)

        return ResponseData.success(mobilePlatformModel)
    }
}