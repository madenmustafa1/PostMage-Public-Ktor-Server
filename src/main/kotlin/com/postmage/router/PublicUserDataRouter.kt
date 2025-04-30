package com.postmage.router

import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class PublicUserDataRouter : BaseRouter  {
    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get(PHOTO) { getPhoto(call) }
                get(PUBLIC_PHOTO) { getPublicPhoto(call) }
                get(USER_PROFILE_WITH_USERNAME) { getUserProfileWithUsername(call) }
                get(POSTS_WITH_USER_NAME) { getPostsWithUserName(call) }
            }
        }
    }

    private suspend fun getPhoto(call: ApplicationCall) {
        koin.publicUserDataVM.getPhoto(call)
    }

    private suspend fun getPublicPhoto(call: ApplicationCall) {
        koin.publicUserDataVM.getPublicPhoto(call)
    }

    private suspend fun getUserProfileWithUsername(call: ApplicationCall) {
        koin.publicUserDataVM.getUserProfileInfoWithUsername(call)
    }

    private suspend fun getPostsWithUserName(call: ApplicationCall) {
        koin.publicUserDataVM.getPostsWithUserName(call)
    }
}