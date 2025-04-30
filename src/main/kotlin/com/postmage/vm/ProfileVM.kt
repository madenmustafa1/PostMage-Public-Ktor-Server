package com.postmage.vm

import com.postmage.model.profile.user.DeleteUserModel
import com.postmage.model.profile.user.SetFollowersDataModel
import com.postmage.model.profile.user.UpdateProfilePhotoModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.model.user_config.getLanguageType
import com.postmage.model.user_config.toHeaders
import com.postmage.repo.ProfileRepository
import com.postmage.util.AppMessages
import com.postmage.util.UuidUtil
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.http_util.html_file.DeleteAccountPage
import com.postmage.util.http_util.html_file.ResultPage
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ProfileVM(
    private val repository: ProfileRepository,
    private val appMessages: AppMessages
) {

    suspend fun getMyProfileInfo(call: ApplicationCall) {
        runCatchableException {
            val result = repository.getMyProfileInfo(call.request.toHeaders())
            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getUserProfileInfo(call: ApplicationCall) {
        runCatchableException {
            val userId = call.request.queryParameters["userId"] ?: ""
            val result = repository.getUserProfileInfo(call.request.toHeaders(), userId)
            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun putUserProfileInfo(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<UserProfileInfoModel>()
            val result = repository.putMyProfileInfo(call.request.toHeaders(), body)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getMyFollowerData(call: ApplicationCall) {
        runCatchableException {
            val result = repository.getMyFollowerData(call.request.toHeaders())
            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: appMessages.SERVER_ERROR
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun putMyFollowerData(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<SetFollowersDataModel>()
            val result = repository.putMyFollowerData(call.request.toHeaders(), body)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: appMessages.SERVER_ERROR
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun putMyProfilePhoto(call: ApplicationCall) {
        runCatchableException {
            val model = UpdateProfilePhotoModel()

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        model.photoName = UuidUtil.createUuid().toString() + part.originalFileName as String
                        model.photoBytes = part.streamProvider().readBytes()
                    }

                    else -> {}
                }
            }

            val result = repository.putMyProfilePhoto(call.request.toHeaders(), model)
            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun putDeleteAccount(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<DeleteUserModel>()
            val result = repository.putDeleteAccount(call.request.toHeaders(), body)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: appMessages.SERVER_ERROR
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getDeleteAccountWeb(call: ApplicationCall) {
        try {
            call.respondText(DeleteAccountPage.formPage(), ContentType.Text.Html)
        } catch (e: Exception) {
            call.respondText(ResultPage.get("Account could not be deleted.", false), ContentType.Text.Html)
        }
    }

    suspend fun postDeleteAccountWeb(call: ApplicationCall) {
        try {
            val parameters = call.receiveParameters()
            val password = parameters["password"]
            val email = parameters["email"]

            val model = DeleteUserModel(
                mail = email,
                password = password
            )

            val result = repository.postDeleteAccountWeb(model)

            var message = "Account deleted."
            if (result.data != true) message = "Account could not be deleted."
            call.respondText(ResultPage.get(message, result.data == true), ContentType.Text.Html)
            call.respondText(ResultPage.get("Account deleted.", result.data == true), ContentType.Text.Html)
        } catch (e: Exception) {
            call.respondText(ResultPage.get("Account could not be deleted.", false), ContentType.Text.Html)
        }
    }

}