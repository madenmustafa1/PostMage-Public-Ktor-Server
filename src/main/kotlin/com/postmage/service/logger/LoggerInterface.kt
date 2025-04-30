package com.postmage.service.logger

import com.postmage.model.app.logger.SaveHttpLogModel

interface LoggerInterface {
    suspend fun saveHttpLog(model: SaveHttpLogModel)
}