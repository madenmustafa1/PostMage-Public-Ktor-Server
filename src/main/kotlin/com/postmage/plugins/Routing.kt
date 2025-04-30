package com.postmage.plugins

import com.postmage.di.KoinApplication
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

val koin = KoinApplication()

fun Application.configureRouting() {
    routing {
        install(CachingHeaders) {
            options { call, content ->
                val maxAge = CacheControl.MaxAge(maxAgeSeconds = 48 * 60 * 60)
                val res = when (content.contentType?.withoutParameters()) {
                    ContentType.Text.Plain,
                    ContentType.Text.Html,
                    ContentType.Text.CSS,
                    ContentType.Application.JavaScript,
                    ContentType.Application.Json,
                    ContentType.Application.Wasm ->
                        CachingOptions(
                            cacheControl = maxAge
                        )

                    ContentType.parse("application/x-font-ttf"),
                    ContentType.parse("font/ttf") ->
                        CachingOptions(
                            cacheControl = maxAge
                        )

                    else -> null
                }

                return@options res
            }
        }

        koin.baseRouter.access(routing = this)
    }
}


