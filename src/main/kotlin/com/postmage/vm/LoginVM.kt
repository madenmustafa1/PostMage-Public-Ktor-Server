package com.postmage.vm

import com.postmage.enums.StatusCodeUtil
import com.postmage.enums.userRouteRole
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.login.password.ForgotPasswordRequestModel
import com.postmage.model.login.password.ForgotPasswordSendMailRequestModel
import com.postmage.model.login.sign_in.SignInRequestModel
import com.postmage.model.login.sign_up.SignUpRequestModel
import com.postmage.model.login.verifier_mail_model.VerifierMailRequestModel
import com.postmage.model.user_config.getLanguageType
import com.postmage.model.user_config.verifyClientTokenSecret
import com.postmage.plugins.koin
import com.postmage.repo.LoginRepository
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.service.Status
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.extensions.verifyToken
import com.postmage.util.http_util.HttpRoute
import com.postmage.util.http_util.html_file.ChangePasswordPage
import com.postmage.util.http_util.html_file.ResultPage
import com.postmage.util.http_util.sendException
import com.postmage.util.strings.AppMessageUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class LoginVM(
    private val repository: LoginRepository
) {

    suspend fun signIn(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<SignInRequestModel>()

            if (!call.request.verifyClientTokenSecret()) {
                sendException(
                    call = call,
                    statusCode = StatusCodeUtil.FORBIDDEN,
                    errorMessage = AppMessageUtil.getAppMessages(call.request.getLanguageType()).accessDenied
                )
                return@runCatchableException
            }

            val result = repository.singIn(call.request.getLanguageType(), body)
            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            call.response.status(StatusCodeUtil.errHandle(result.message?.statusCode ?: 500))
            call.respond(result.message ?: "")
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun signUp(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<SignUpRequestModel>()

            if (!call.request.verifyClientTokenSecret()) {
                sendException(
                    call = call,
                    statusCode = StatusCodeUtil.FORBIDDEN,
                    errorMessage = AppMessageUtil.getAppMessages(call.request.getLanguageType()).accessDenied
                )
                return@runCatchableException
            }

            val result = repository.singUp(call.request.getLanguageType(), body)
            if (result.status != Status.SUCCESS) {
                call.response.status(StatusCodeUtil.errHandle(result.message?.statusCode ?: 500))
                call.respond(result.message ?: koin.appMessages.SERVER_ERROR)
                return@runCatchableException
            }

            (result.message ?: result.data)?.let { call.respond(it) }
            call.response.status(HttpStatusCode.OK)
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun forgotPasswordSendMail(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<ForgotPasswordSendMailRequestModel>()
            val result = repository.forgotPasswordSendMail(body)

            if (result.status != Status.SUCCESS) {
                call.response.status(StatusCodeUtil.errHandle(result.message?.statusCode ?: 500))
                call.respond(result.message ?: koin.appMessages.SERVER_ERROR)
                return@runCatchableException
            }

            (result.message ?: result.data)?.let { call.respond(it) }
            call.response.status(HttpStatusCode.OK)
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getForgotPassword(call: ApplicationCall) {
        try {
            val accessToken = call.request.queryParameters["accessToken"]
            val id = call.request.queryParameters["id"]
            val mailAddress = call.request.queryParameters["mail"]

            val httpQuery = "?mail=$mailAddress&id=$id&accessToken=$accessToken"
            val changePasswordUrl = HttpRoute.BASE_URL + HttpRoute.FORGOT_PASSWORD + httpQuery

            call.respondText(ChangePasswordPage.formPage(actionUrl = changePasswordUrl), ContentType.Text.Html)
        } catch (e: Exception) {
            call.respondText(ResultPage.get("Your password change operation has failed.", false), ContentType.Text.Html)
        }
    }

    suspend fun postForgotPassword(call: ApplicationCall) {
        try {
            val parameters = call.receiveParameters()
            val password = parameters["password"]
            val result = repository.postForgotPassword(
                ForgotPasswordRequestModel(
                    objectId = call.request.queryParameters["id"] ?: "",
                    mailAddress = call.request.queryParameters["mail"] ?: "",
                    accessToken = call.request.queryParameters["accessToken"] ?: "",
                    reqPassword = password ?: ""
                )
            )

            var message = "Your password change operation has been completed successfully."
            if (result.data != true) message = "Your password change operation has failed."
            call.respondText(ResultPage.get(message, result.data == true), ContentType.Text.Html)
        } catch (e: Exception) {
            call.respondText(ResultPage.get("Your password change operation has failed.", false), ContentType.Text.Html)
        }
    }

    suspend fun getVerifierMail(call: ApplicationCall) {
        val languageType = LanguageType.getLanguageType(call.request.queryParameters["language"] ?: "")
        val appMessages = AppMessageUtil.getAppMessages(languageType)

        try {
            val token = call.request.queryParameters["accessToken"] ?: ""
            val tokenVerifierResult = token.verifyToken(userRole = userRouteRole().toTypedArray())


            var result: ResponseData<Boolean> = sendErrorData(appMessages.accessDenied)

            if (tokenVerifierResult) {
                result = repository.putVerifierMail(
                    languageType,
                    VerifierMailRequestModel(
                        userId = call.request.queryParameters["id"] ?: "",
                        mail = call.request.queryParameters["mail"] ?: "",
                        accessToken = token
                    )
                )
            }

            var message = appMessages.verifierMailSuccess
            if (result.data != true) message = appMessages.accessDenied
            call.respondText(
                ResultPage.get(message, result.data == true, titleStr = appMessages.mailVerifier),
                ContentType.Text.Html
            )
        } catch (e: Exception) {
            call.respondText(
                ResultPage.get(appMessages.serverError, false, titleStr = appMessages.mailVerifier),
                ContentType.Text.Html
            )
        }
    }

}