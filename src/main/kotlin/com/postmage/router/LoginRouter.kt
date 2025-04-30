package com.postmage.router

import com.postmage.plugins.koin
import com.postmage.util.http_util.HttpRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

class LoginRouter : BaseRouter {

    override fun access(route: Route) {
        with(HttpRoute) {
            route {
                post(SIGN_IN) { signIn(call) }
                post(SIGN_UP) { signUp(call) }
                put(FORGOT_PASSWORD_SEND_MAIL) { forgotPasswordSendMail(call) }
                get(FORGOT_PASSWORD) { getForgotPassword(call) }
                post(FORGOT_PASSWORD) { postForgotPassword(call) }
                get(VERIFIER_MAIL) { getVerifierMail(call) }
            }
        }
    }

    private suspend fun signIn(call: ApplicationCall) {
        koin.loginVM.signIn(call)
    }

    private suspend fun signUp(call: ApplicationCall) {
        koin.loginVM.signUp(call)
    }

    private suspend fun postForgotPassword(call: ApplicationCall) {
        koin.loginVM.postForgotPassword(call)
    }

    private suspend fun getForgotPassword(call: ApplicationCall) {
        koin.loginVM.getForgotPassword(call)
    }

    private suspend fun forgotPasswordSendMail(call: ApplicationCall) {
        koin.loginVM.forgotPasswordSendMail(call)
    }

    private suspend fun getVerifierMail(call: ApplicationCall) {
        koin.loginVM.getVerifierMail(call)
    }
}