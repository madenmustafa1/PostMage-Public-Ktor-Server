package com.postmage.repo

import com.postmage.model.platform.MobilePlatformModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData
import com.postmage.service.platform.PlatformInterface
import com.postmage.service.platform.PlatformService

class PlatformRepository(
    private val platformService: PlatformService,
) : PlatformInterface {
    override suspend fun getCurrentMobileVersion(headers: UserRequestHeaders): ResponseData<MobilePlatformModel> {
        return platformService.getCurrentMobileVersion(headers)
    }
}