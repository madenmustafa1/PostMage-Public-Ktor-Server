package com.postmage.vm

import com.postmage.model.app.logger.SaveHttpLogModel
import com.postmage.repo.LoggerRepository
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoggerVM(
    private val loggerRepository: LoggerRepository
) {

    fun filterHttpLog(call: ApplicationCall): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            val maxSizeInBytes = 100 * 1024
            val isResponseRequestOverload = isRequestOverload(call, maxSizeInBytes)

            if(isResponseRequestOverload) return@launch

            loggerRepository.saveHttpLog(
                SaveHttpLogModel(request = call.request)
            )
        }

        return call.request.path().startsWith("/")
    }


    private suspend fun isRequestOverload(response: ApplicationCall, maxSize: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            (response.request.contentLength() ?: 0) > maxSize
        } catch (e: ContentTransformationException) {
            false
        }
    }
}