package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class ProfileRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                get(USER_PROFILE) { getUserProfile(call) }
                put(USER_PROFILE) { putUserProfileInfo(call) }
                get(FOLLOWER) { getMyFollowerData(call) }
                put(FOLLOWER) { putMyFollowerData(call) }
                put(DELETE_ACCOUNT) { putDeleteAccount(call) }
                get(DELETE_ACCOUNT_WEB) { getDeleteAccountWeb(call) }
                post(DELETE_ACCOUNT_WEB) { postDeleteAccountWeb(call) }
            }
        }
    }

    private suspend fun getUserProfile(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.getUserProfileInfo(call)
        }
    }

    private suspend fun getMyProfileInfo(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.getMyProfileInfo(call)
        }
    }

    private suspend fun putUserProfileInfo(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.putUserProfileInfo(call)
        }
    }

    private suspend fun getMyFollowerData(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.getMyFollowerData(call)
        }
    }

    private suspend fun putMyFollowerData(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.putMyFollowerData(call)
        }
    }

    private suspend fun putDeleteAccount(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.profileVM.putDeleteAccount(call)
        }
    }

    private suspend fun getDeleteAccountWeb(call: ApplicationCall) {
        koin.profileVM.getDeleteAccountWeb(call)
    }

    private suspend fun postDeleteAccountWeb(call: ApplicationCall) {
        koin.profileVM.postDeleteAccountWeb(call)
    }

}