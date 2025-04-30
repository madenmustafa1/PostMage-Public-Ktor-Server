package com.postmage.plugins

import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*
import io.ktor.server.application.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> koin.httpLogVM.filterHttpLog(call) }
    }
}