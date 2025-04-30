package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.PostType
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.client.request.forms.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

class FormDataRouter : BaseRouter {

    override fun access(route: Route) {

        with(HttpRoute) {
            formData {
                route.route( API +POSTS) {
                    post(ADD_POSTS) { addPosts(call) }
                    post(ADD_POSTS_TO_GROUP) { addPostToGroup(call) }
                }

                route.route(API + PROFILE) {
                    put(PROFILE_PHOTO) { putMyProfilePhoto(call) }
                }
            }
        }
    }

    private suspend fun addPosts(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.addPost(call)
        }
    }

    private suspend fun addPostToGroup(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.usersPostsVM.addPost(call, PostType.ADD_GROUP)
        }
    }

    private suspend fun putMyProfilePhoto(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.putMyProfilePhoto(call)
        }
    }

}