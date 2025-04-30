package com.postmage.util.exception

import com.postmage.model.app.logger.CrashLogModel
import com.postmage.mongo_client.MongoInitialize
import com.postmage.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExceptionLogger(private val mongoDB: MongoInitialize) {

    fun execute(throwable: Throwable?) {
        if (throwable == null) return

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                var stackTraceList = throwable.stackTrace.toList()

                if (stackTraceList.size > 5)
                    stackTraceList = stackTraceList.take(5)

                val model = CrashLogModel(
                    stackTrace = stackTraceList,
                    message = throwable.message,
                    creationTime = DateUtil.getDateNow(),
                )

                mongoDB.getCrashLog.insertOne(model)
            }
        }
    }

}