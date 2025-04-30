package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class PostsRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get(POST) { getPost(call) }
                get(COMMENTS) { getComments(call) }
                get(USER_POSTS) { getUserPostsWithUserId(call) }
                put(UPDATE_POSTS) { updatePost(call) }
                post(GROUP_POSTS) { getGroupPost(call) }
                get(USERS_POSTS) { postOfFollowedUsers(call) }
                put(MAKE_PHOTO_PUBLIC) { putMakePhotoPublic(call) }
            }
        }
    }

    private suspend fun getPost(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.getPost(call)
        }
    }

    private suspend fun getComments(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.getComments(call)
        }
    }

    private suspend fun updatePost(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.updatePost(call)
        }
    }

    private suspend fun getGroupPost(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.getGroupPost(call)
        }
    }

    private suspend fun getUserPostsWithUserId(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.getUserPostsWithUserId(call)
        }
    }

    private suspend fun postOfFollowedUsers(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.postOfFollowedUsers(call)
        }
    }

    private suspend fun putMakePhotoPublic(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.putMakePhotoPublic(call)
        }
    }
}