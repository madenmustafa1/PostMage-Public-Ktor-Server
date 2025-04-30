package com.postmage.router

import com.postmage.BUILD_TYPE
import com.postmage.model.app.build_type.AppBuildType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

class WebRouter: BaseRouter {
    override fun access(route: Route) {
        route {
            get("/") {
                val file = when(BUILD_TYPE) {
                    AppBuildType.DEV, AppBuildType.TEST -> File("web/index.html")
                    AppBuildType.PROD -> File("/app/web/index.html")
                }
                call.respondFile(file)
            }

            get("/web/") {
                val file = when(BUILD_TYPE) {
                    AppBuildType.DEV, AppBuildType.TEST -> File("web/index.html")
                    AppBuildType.PROD -> File("/app/web/index.html")
                }
                call.respondFile(file)
            }

            get("/p") {
                val file = when(BUILD_TYPE) {
                    AppBuildType.DEV, AppBuildType.TEST -> File("web/index.html")
                    AppBuildType.PROD -> File("/app/web/index.html")
                }
                call.respondFile(file)
            }

            get("/web/p") {
                val file = when(BUILD_TYPE) {
                    AppBuildType.DEV, AppBuildType.TEST -> File("web/index.html")
                    AppBuildType.PROD -> File("/app/web/index.html")
                }
                call.respondFile(file)
            }

            get("/web/{...}") {
                val relativePath = call.request.uri

                val file = when(BUILD_TYPE) {
                    AppBuildType.DEV, AppBuildType.TEST -> File(relativePath.replaceFirst("/", ""))
                    AppBuildType.PROD -> File("/app$relativePath")
                }

                if (file.exists() && file.isFile) {
                    call.respondFile(file)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }


            get("/{...}") {
                val relativePath = call.request.uri

                val file = when(BUILD_TYPE) {
                    AppBuildType.DEV, AppBuildType.TEST -> File(relativePath.replaceFirst("/", "web/"))
                    AppBuildType.PROD -> File("/app/web/$relativePath")
                }

                if (file.exists() && file.isFile) {
                    call.respondFile(file)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }

}