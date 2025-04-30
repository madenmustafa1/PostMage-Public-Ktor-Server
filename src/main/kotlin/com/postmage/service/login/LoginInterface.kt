package com.postmage.service.login

import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.login.password.ChangePasswordAccessTokenRequestModel
import com.postmage.model.login.password.ChangePasswordModel
import com.postmage.model.login.password.ForgotPasswordRequestModel
import com.postmage.model.login.password.ForgotPasswordSendMailRequestModel
import com.postmage.model.login.sign_in.SignInRequestModel
import com.postmage.model.login.sign_in.SignInResponseModel
import com.postmage.model.login.sign_up.SignUpRequestModel
import com.postmage.model.login.sign_up.SignUpResponseModel
import com.postmage.model.login.verifier_mail_model.VerifierMailRequestModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData

interface LoginInterface {

    suspend fun singIn(languageType: LanguageType, signInRequestModel: SignInRequestModel): ResponseData<SignInResponseModel?>
    suspend fun singUp(languageType: LanguageType, signUpRequestModel: SignUpRequestModel): ResponseData<SignUpResponseModel?>
    suspend fun changePassword(changePasswordModel: ChangePasswordModel): Boolean
    suspend fun changePasswordWithAccessToken(model: ChangePasswordAccessTokenRequestModel): Boolean
    suspend fun postForgotPassword(model: ForgotPasswordRequestModel): ResponseData<Boolean>
    suspend fun forgotPasswordSendMail(model: ForgotPasswordSendMailRequestModel): ResponseData<Boolean>
    suspend fun putVerifierMail(languageType: LanguageType, model: VerifierMailRequestModel): ResponseData<Boolean>

}