package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class PlatformRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get(CURRENT_MOBILE_VERSION) { getCurrentMobileVersion(call) }
            }
        }
    }

    private suspend fun getCurrentMobileVersion(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.platformVM.getCurrentMobileVersion(call)
        }
    }
}