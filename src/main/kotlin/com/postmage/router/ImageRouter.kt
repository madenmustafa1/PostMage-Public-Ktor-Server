package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class ImageRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get(DOWNLOAD) {
                    accessManager(call, role = userRouteRole().toTypedArray()) {
                        downloadPhoto(call)
                    }
                }
            }
        }

    }

    private suspend fun downloadPhoto(call: ApplicationCall) {
        koin.imageVM.downloadPhoto(call)
    }


}