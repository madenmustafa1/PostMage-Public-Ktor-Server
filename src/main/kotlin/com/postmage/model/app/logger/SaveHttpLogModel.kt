package com.postmage.model.app.logger

import io.ktor.server.request.*

data class SaveHttpLogModel(
    val request: ApplicationRequest
)