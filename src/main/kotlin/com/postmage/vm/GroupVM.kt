package com.postmage.vm

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.group.UsersToGroupModel
import com.postmage.model.group.CreateGroupRequestModel
import com.postmage.model.user_config.toHeaders
import com.postmage.plugins.koin
import com.postmage.repo.GroupRepository
import com.postmage.util.AppMessages
import com.postmage.util.UuidUtil
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class GroupVM(
    private val repository: GroupRepository,
    private val appMessages: AppMessages
) {

    suspend fun createGroup(call: ApplicationCall) {
        try {
            val model = CreateGroupRequestModel()

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "groupName" -> model.groupName = part.value
                            "description" -> model.description = part.value
                        }
                    }

                    is PartData.FileItem -> {
                        model.photoName = UuidUtil.createUuid().toString() + part.originalFileName as String
                        model.photoBytes = part.streamProvider().readBytes()
                    }

                    else -> {}
                }
            }

            val result = repository.createGroup(call.request.toHeaders(), model)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )

        } catch (e: NullPointerException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.UNAUTHORIZED,
                errorMessage = koin.appMessages.UNAUTHORIZED
            )
        } catch (e: MismatchedInputException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: Exception) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.SERVER_ERROR,
                errorMessage = appMessages.SERVER_ERROR
            )
        }
    }

    suspend fun usersToGroup(call: ApplicationCall, requestType: UsersToGroupRequestType) {
        try {
            val body = call.receive<UsersToGroupModel>()

            val result = when(requestType) {
                UsersToGroupRequestType.ADD_USER ->
                    repository.addUsersToGroup(call.request.toHeaders(), body)

                UsersToGroupRequestType.REMOVE ->
                    repository.removeUsersToGroup(call.request.toHeaders(), body)

                UsersToGroupRequestType.ADD_ADMIN ->
                    repository.addAdminToGroup(call.request.toHeaders(), body)
            }

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        } catch (e: NullPointerException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.UNAUTHORIZED,
                errorMessage = koin.appMessages.UNAUTHORIZED
            )
        } catch (e: MismatchedInputException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: CannotTransformContentToTypeException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: Exception) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.SERVER_ERROR,
                errorMessage = koin.appMessages.SERVER_ERROR
            )
        }
    }

    suspend fun getMyGroupList(call: ApplicationCall) {
        try {
            val result = repository.getMyGroupList(call.request.toHeaders())

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        } catch (e: NullPointerException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.UNAUTHORIZED,
                errorMessage = koin.appMessages.UNAUTHORIZED
            )
        } catch (e: MismatchedInputException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: CannotTransformContentToTypeException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: Exception) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.SERVER_ERROR,
                errorMessage = koin.appMessages.SERVER_ERROR
            )
        }
    }

    suspend fun getMyGroupInfo(call: ApplicationCall) {
        try {
            val groupId = call.request.queryParameters["groupId"] ?: ""

            val result = repository.getMyGroupInfo(call.request.toHeaders(), groupId)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        } catch (e: NullPointerException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.UNAUTHORIZED,
                errorMessage = koin.appMessages.UNAUTHORIZED
            )
        } catch (e: MismatchedInputException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: CannotTransformContentToTypeException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: Exception) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.SERVER_ERROR,
                errorMessage = koin.appMessages.SERVER_ERROR
            )
        }
    }

    enum class UsersToGroupRequestType {
        ADD_USER,
        REMOVE,
        ADD_ADMIN
    }
}