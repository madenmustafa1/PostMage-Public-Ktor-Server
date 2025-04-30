package com.postmage.repo

import com.postmage.model.app.app_message.LanguageType
import com.postmage.util.extensions.genderControl
import com.postmage.util.extensions.isValidEmail
import com.postmage.model.login.password.ChangePasswordAccessTokenRequestModel
import com.postmage.model.login.password.ChangePasswordModel
import com.postmage.model.login.password.ForgotPasswordRequestModel
import com.postmage.model.login.password.ForgotPasswordSendMailRequestModel
import com.postmage.model.login.sign_in.SignInRequestModel
import com.postmage.model.login.sign_in.SignInResponseModel
import com.postmage.model.login.sign_up.SignUpRequestModel
import com.postmage.model.login.sign_up.SignUpResponseModel
import com.postmage.model.login.verifier_mail_model.VerifierMailRequestModel
import com.postmage.service.ResponseData
import com.postmage.service.login.LoginInterface
import com.postmage.util.AppMessages
import com.postmage.service.login.LoginService
import com.postmage.util.strings.AppMessageUtil

class LoginRepository(
    private val longinService: LoginService,
    private val appMessages: AppMessages
) : LoginInterface {

    override suspend fun singIn(
        languageType: LanguageType,
        signInRequestModel: SignInRequestModel
    ): ResponseData<SignInResponseModel?> {
        if (signInRequestModel.mail == null || signInRequestModel.password == null) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        if (!signInRequestModel.mail.isValidEmail()) return sendErrorData(AppMessageUtil.getAppMessages(languageType).notValidEmail)
        if (signInRequestModel.password.length < 4) return sendErrorData(AppMessageUtil.getAppMessages(languageType).passwordNotBeShort)
        return longinService.singIn(languageType, signInRequestModel)
    }

    override suspend fun singUp(
        languageType: LanguageType,
        signUpRequestModel: SignUpRequestModel
    ): ResponseData<SignUpResponseModel?> {
        if (!signUpRequestModel.mail.isValidEmail()) return sendErrorData(AppMessageUtil.getAppMessages(languageType).notValidEmail)
        if (signUpRequestModel.password.length < 4) return sendErrorData(AppMessageUtil.getAppMessages(languageType).passwordNotBeShort)
        if (signUpRequestModel.nameSurname.trim() == "") return sendErrorData(AppMessageUtil.getAppMessages(languageType).nameSurnameNotBeEmpty)
        if (signUpRequestModel.gender != null && !signUpRequestModel.gender.genderControl()) return sendErrorData(
            AppMessageUtil.getAppMessages(languageType).genderInvalidate
        )
        return longinService.singUp(languageType, signUpRequestModel)
    }

    override suspend fun changePassword(changePasswordModel: ChangePasswordModel): Boolean {
        return longinService.changePassword(changePasswordModel)
    }

    override suspend fun changePasswordWithAccessToken(model: ChangePasswordAccessTokenRequestModel): Boolean {
        return longinService.changePasswordWithAccessToken(model)
    }

    override suspend fun postForgotPassword(model: ForgotPasswordRequestModel): ResponseData<Boolean> {
        if (!model.mailAddress.isValidEmail()) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        if (model.objectId.trim().isEmpty()) return sendErrorData(appMessages.MODEL_IS_NOT_VALID)
        if (model.accessToken.trim().isEmpty()) return sendErrorData(appMessages.ACCESS_DENIED)
        if (model.reqPassword.trim().length < 5) return sendErrorData(appMessages.PASSWORD_NOT_BE_SHORT)
        return longinService.postForgotPassword(model)
    }

    override suspend fun forgotPasswordSendMail(model: ForgotPasswordSendMailRequestModel): ResponseData<Boolean> {
        if (!model.mailAddress.isValidEmail()) return sendErrorData(appMessages.NOT_VALID_EMAIL)
        return longinService.forgotPasswordSendMail(model)
    }

    override suspend fun putVerifierMail(
        languageType: LanguageType,
        model: VerifierMailRequestModel
    ): ResponseData<Boolean> {
        if (!model.mail.isValidEmail()) return sendErrorData(AppMessageUtil.getAppMessages(languageType).notValidEmail)
        return longinService.putVerifierMail(languageType, model)
    }
}