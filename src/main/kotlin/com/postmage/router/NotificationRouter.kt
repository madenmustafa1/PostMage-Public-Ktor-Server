package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class NotificationRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get("/test") { koin.notificationVM.sendNotification(this.call) }
                get(NOTIFICATION_LIST) { getNotificationList(call) }
            }
        }

    }

    private suspend fun getNotificationList(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.notificationVM.getNotificationList(call)
        }
    }
}