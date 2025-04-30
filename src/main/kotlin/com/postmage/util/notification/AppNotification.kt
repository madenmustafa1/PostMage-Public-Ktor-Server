package com.postmage.util.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.gson.Gson
import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.fcm.FCMAppNotification
import com.postmage.model.notification.fcm.FCMMessage
import com.postmage.model.notification.fcm.FCMNotification
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import java.io.FileInputStream
import kotlin.time.Duration.Companion.seconds

class AppNotification {

    private val projectName = "postmage-firebase"
    private val gson = Gson()

    fun init() {
        val serviceAccount = FileInputStream("fcm-service-account.json")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        FirebaseApp.initializeApp(options)
        getAccessToken()
    }

    private fun getAccessToken() {
        val messagingScope = "https://www.googleapis.com/auth/firebase.messaging"
        val scopes = arrayOf(messagingScope)

        val googleCredentials: GoogleCredentials =
            GoogleCredentials.fromStream(FileInputStream("fcm-service-account.json")).createScoped(*scopes)
        googleCredentials.refresh()

        TOKEN = googleCredentials.accessToken.tokenValue
    }


    suspend fun sendNotification(model: AppSendNotificationModel, retry: Boolean = true) {
        val fcmUrl = "https://fcm.googleapis.com/v1/projects/$projectName/messages:send"

        val notification = FCMAppNotification(
            message = FCMMessage(
                topic = model.toUserId,
                notification = FCMNotification(
                    title = model.title,
                    body = model.body ?: ""
                )
            )
        )

        val client = HttpClient(Apache)
        try {
            val response = client.post {
                url(fcmUrl)
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $TOKEN")
                header("X-GFE-SSL", "yes")
                setBody(TextContent(gson.toJson(notification), contentType = ContentType.Application.Json))
            }

            if (response.status == HttpStatusCode.Unauthorized && retry) {
                getAccessToken()
                delay(1.seconds)
                sendNotification(model = model, retry = false)
            }

        } catch (_: Exception) {
        } finally {
            client.close()
        }
    }

    companion object {
        private var TOKEN: String = ""
    }
}

/*
val message = """
{
    "message": {
        "topic": "${model.to}",
        "notification": {
            "title": "${model.title}",
            "body": "${model.body}"
        }
    }
}
""".trimIndent()
 */


