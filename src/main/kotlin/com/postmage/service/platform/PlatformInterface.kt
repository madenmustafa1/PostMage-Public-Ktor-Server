package com.postmage.service.platform

import com.postmage.model.platform.MobilePlatformModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData

interface PlatformInterface {
    suspend fun getCurrentMobileVersion(headers: UserRequestHeaders): ResponseData<MobilePlatformModel>

}