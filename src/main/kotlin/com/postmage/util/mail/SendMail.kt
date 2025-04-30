package com.postmage.util.mail

import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.mail.SendMailModel
import com.postmage.util.Constants
import com.postmage.util.http_util.HttpRoute
import com.postmage.util.strings.AppMessageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.simplejavamail.api.email.Email
import org.simplejavamail.email.EmailBuilder

object SendMail {

    //Kullanıcıya hesabını doğrulaması için mail gönderiliyor.
    fun verifierMail(
        mail: String,
        userId: String,
        token: String,
        languageType: LanguageType
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val httpQuery =
                "?mail=${mail}&id=${userId}&accessToken=$token&language=${languageType.value}"
            val link = HttpRoute.BASE_URL + HttpRoute.VERIFIER_MAIL + httpQuery //"http://0.0.0.0:8080/"

            execute(
                SendMailModel(
                    mailAddress = mail,
                    title = Constants.APP_NAME,
                    message = AppMessageUtil.getAppMessages(languageType).verifierMail.replace("__LINK__", link)
                )
            )
        }
    }

    suspend fun execute(model: SendMailModel): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {

            // E-posta oluştur
            val email: Email = EmailBuilder.startingBlank()
                .from(Constants.APP_NAME, MailConstants.emailUsername)
                .to(Constants.APP_NAME, model.mailAddress)
                .withSubject(model.title)
                .withPlainText(model.message)
                .buildEmail()

            // E-postayı gönder
            MailConstants.mailer.sendMail(email)
            true
        } catch (e: java.net.ConnectException) {
            false
        } catch (e: Exception) {
            false
        }
    }

}




