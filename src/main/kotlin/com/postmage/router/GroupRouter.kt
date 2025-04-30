package com.postmage.router

import com.postmage.controller.accessManager
import com.postmage.enums.userRouteRole
import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import com.postmage.vm.GroupVM
import io.ktor.server.application.*
import io.ktor.server.routing.*

class GroupRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                post(CREATE_GROUP) { createGroup(call) }
                put(ADD_USERS_GROUP) { usersToGroup(call, GroupVM.UsersToGroupRequestType.ADD_USER) }
                put(ADD_ADMIN_GROUP) { usersToGroup(call, GroupVM.UsersToGroupRequestType.ADD_ADMIN) }
                put(REMOVE_USERS_GROUP) { usersToGroup(call, GroupVM.UsersToGroupRequestType.REMOVE) }
                get(MY_GROUP_LIST) { getMyGroupList(call) }
                get(MY_GROUP_INFO) { getMyGroupInfo(call) }
            }
        }
    }

    private suspend fun createGroup(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.groupVM.createGroup(call)
        }
    }

    private suspend fun usersToGroup(call: ApplicationCall, requestType: GroupVM.UsersToGroupRequestType) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.groupVM.usersToGroup(call, requestType)
        }
    }

    private suspend fun getMyGroupList(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.groupVM.getMyGroupList(call)
        }
    }

    private suspend fun getMyGroupInfo(call: ApplicationCall) {
        accessManager(call, role = userRouteRole().toTypedArray()) {
            koin.groupVM.getMyGroupInfo(call)
        }
    }

}