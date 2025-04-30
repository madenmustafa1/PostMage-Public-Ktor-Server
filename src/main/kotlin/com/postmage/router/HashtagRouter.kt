package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*


class HashtagRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get(POPULAR) { getPopularHashtags(call) }
                get(HASHTAG_POST) { getHashtagPostsWithId(call) }
            }
        }
    }

    private suspend fun getPopularHashtags(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.hashtagVM.getPopularHashtags(call)
        }
    }

    private suspend fun getHashtagPostsWithId(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.hashtagVM.getHashtagPosts(call)
        }
    }

}