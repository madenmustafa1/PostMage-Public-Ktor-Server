package com.postmage.repo

import com.postmage.model.app.logger.SaveHttpLogModel
import com.postmage.service.logger.LoggerInterface
import com.postmage.service.logger.LoggerService
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.request.*

class LoggerRepository(
    private val loggerService: LoggerService,
) : LoggerInterface {
    override suspend fun saveHttpLog(model: SaveHttpLogModel) {
        try {
            with(HttpRoute) {
                if (
                    model.request.path().contains(SIGN_IN) ||
                    model.request.path().contains(SIGN_UP) ||
                    model.request.path().contains(FORGOT_PASSWORD) ||
                    model.request.path().contains(DELETE_ACCOUNT_WEB) ||
                    model.request.path().contains(DELETE_ACCOUNT) ||
                    model.request.path().contains(DOWNLOAD) ||
                    model.request.path().contains(IMAGE)

                ) return
            }

            loggerService.saveHttpLog(model)
        } catch (_: Exception) {
        }
    }
}