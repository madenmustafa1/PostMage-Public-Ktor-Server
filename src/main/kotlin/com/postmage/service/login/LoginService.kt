package com.postmage.service.login

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.BasicDBObject
import com.postmage.enums.AppUserRole
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.app.app_message.LanguageType
import com.postmage.util.extensions.createToken
import com.postmage.util.extensions.findUser
import com.postmage.model.mail.SendMailModel
import com.postmage.model.login.password.ChangePasswordAccessTokenRequestModel
import com.postmage.model.login.password.ChangePasswordModel
import com.postmage.model.login.password.ForgotPasswordRequestModel
import com.postmage.model.login.password.ForgotPasswordSendMailRequestModel
import com.postmage.model.app.security.SecurityCollectionModel
import com.postmage.model.login.sign_in.SignInRequestModel
import com.postmage.model.login.sign_in.SignInResponseModel
import com.postmage.model.login.sign_up.SignUpRequestModel
import com.postmage.model.login.sign_up.SignUpResponseModel
import com.postmage.model.login.sign_up.toUserProfileInfoModel
import com.postmage.model.login.verifier_mail_model.VerifierMailRequestModel
import com.postmage.mongo_client.MongoInitialize
import com.postmage.repo.sendErrorData
import org.bson.types.ObjectId
import com.postmage.service.ErrorMessage
import com.postmage.service.ResponseData
import com.postmage.util.AppMessages
import com.postmage.util.Constants
import com.postmage.util.http_util.HttpRoute
import com.postmage.util.http_util.verifyPassword
import com.postmage.util.mail.SendMail
import com.postmage.util.strings.AppMessageUtil
import com.postmage.util.strings.MailMessages

class LoginService(
    private val mongoDB: MongoInitialize
) : LoginInterface {

    override suspend fun singIn(
        languageType: LanguageType,
        signInRequestModel: SignInRequestModel
    ): ResponseData<SignInResponseModel?> {
        try {
            val collection = mongoDB.getUserCollection
            val query = BasicDBObject("mail", signInRequestModel.mail!!)

            var result: ResponseData<SignInResponseModel?>? = null

            collection.find(query).limit(1).findUser(showPassword = true) { model ->

                val passwordVerified = signInRequestModel.password!!.verifyPassword(expected = model.password ?: "")
                if (!passwordVerified) return@findUser

                val token = signInRequestModel.mail.createToken(
                    model.userId.toString(),
                    userRole = AppUserRole.values()[model.userRole ?: AppUserRole.USER.ordinal]
                )

                if (model.isEmailVerified == false) {
                    result = ResponseData.error(
                        ErrorMessage(
                            AppMessageUtil.getAppMessages(languageType).sendMailVerifierAgain,
                            statusCode = StatusCodeUtil.FORBIDDEN
                        ),
                        data = null
                    )

                    SendMail.verifierMail(
                        mail = model.mail ?: "",
                        userId = model.userId.toString(),
                        token = token ?: "",
                        languageType = languageType
                    )

                    return@findUser
                }

                result = ResponseData.success(
                    SignInResponseModel(
                        token = token,
                        isSuccess = true,
                        userId = model.userId.toString()
                    )
                )

            }

            result?.let { return it }

            return ResponseData.error(
                ErrorMessage(
                    AppMessageUtil.getAppMessages(languageType).emailOrPasswordIncorrect,
                    statusCode = 404
                ), null
            )
        } catch (e: Exception) {
            return ResponseData.error(
                ErrorMessage(
                    AppMessageUtil.getAppMessages(languageType).serverError,
                    statusCode = 500
                ), null
            )
        }
    }

    override suspend fun singUp(
        languageType: LanguageType,
        signUpRequestModel: SignUpRequestModel
    ): ResponseData<SignUpResponseModel?> {
        return try {
            val collection = mongoDB.getUserCollection

            val query = BasicDBObject("mail", signUpRequestModel.mail)
            repeat(collection.find(query).limit(1).count()) {
                return sendErrorData(AppMessageUtil.getAppMessages(languageType).emailNotUnique)
            }

            val password = signUpRequestModel.password
            val bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray())
            val userId = ObjectId.get()
            signUpRequestModel.userId = userId.toString()
            signUpRequestModel.password = bcryptHashString

            collection.insertOne(signUpRequestModel.toUserProfileInfoModel())

            val token = signUpRequestModel.mail.createToken(userId.toString(), userRole = AppUserRole.USER)

            SendMail.verifierMail(
                mail = signUpRequestModel.mail,
                userId = userId.toString(),
                token = token ?: "",
                languageType = languageType
            )

            ResponseData.success(
                SignUpResponseModel(
                    token = token,
                    isSuccess = true,
                    userId = userId.toString()
                )
            )
        } catch (e: Exception) {
            ResponseData.error(ErrorMessage(AppMessageUtil.getAppMessages(languageType).serverError), null)
        }
    }

    override suspend fun changePassword(changePasswordModel: ChangePasswordModel): Boolean {
        println("LoginService " + (false))
        return false
    }

    override suspend fun changePasswordWithAccessToken(model: ChangePasswordAccessTokenRequestModel): Boolean {
        //Salt requested password
        val reqPassword = BCrypt.withDefaults().hashToString(12, model.reqPassword.toCharArray())

        val collection = mongoDB.getUserCollection
        val query = BasicDBObject("mail", model.mailAddress)
        collection.find(query).limit(1).findUser(showPassword = true) {
            it.password = reqPassword
            collection.replaceOne(query, it)
        }

        return true
    }

    override suspend fun postForgotPassword(model: ForgotPasswordRequestModel): ResponseData<Boolean> {
        val collection = mongoDB.getSecurityCollection
        val query = BasicDBObject("objectId", model.objectId)
        var isSuccess = false

        collection.find(query).limit(1).forEach {
            if (it.enable && it.accessToken == model.accessToken) {
                isSuccess = true
                it.enable = false
                collection.replaceOne(query, it)
            }
        }

        if (isSuccess) {
            isSuccess = changePasswordWithAccessToken(
                ChangePasswordAccessTokenRequestModel(
                    mailAddress = model.mailAddress,
                    accessToken = model.accessToken,
                    reqPassword = model.reqPassword
                )
            )
        }

        return ResponseData.success(isSuccess)
    }

    override suspend fun forgotPasswordSendMail(model: ForgotPasswordSendMailRequestModel): ResponseData<Boolean> {
        val objectId = ObjectId.get()
        //Create access token
        val accessToken = BCrypt.withDefaults().hashToString(6, objectId.toString().toCharArray())

        mongoDB.getSecurityCollection.insertOne(
            SecurityCollectionModel(
                accessToken = accessToken,
                enable = true,
                objectId = objectId.toString()
            )
        )

        val httpQuery = "?mail=${model.mailAddress}&id=$objectId&accessToken=$accessToken"
        val changePasswordUrl = HttpRoute.BASE_URL + HttpRoute.FORGOT_PASSWORD + httpQuery
        val message = MailMessages.FORGOT_PASSWORD_MESSAGE.replace("__LINK__", changePasswordUrl)

        val result = SendMail.execute(
            SendMailModel(
                mailAddress = model.mailAddress,
                title = Constants.APP_NAME,
                message = message
            )
        )

        return ResponseData.success(result)
    }

    override suspend fun putVerifierMail(
        languageType: LanguageType,
        model: VerifierMailRequestModel
    ): ResponseData<Boolean> {
        val collection = mongoDB.getUserCollection
        val query = BasicDBObject("userId", model.userId)

        var result: ResponseData<Boolean> = sendErrorData(AppMessageUtil.getAppMessages(languageType).userNotFound)

        collection.find(query).limit(1).findUser(showPassword = true) {
            it.isEmailVerified = true
            collection.replaceOne(query, it)
            result = ResponseData.success(true)
        }

        return result
    }

}



