package com.postmage.util.mail

import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.mailer.MailerBuilder

object MailConstants {

    private const val GOOGLE_SMTP_HOST = "smtp.gmail.com"

    const val emailUsername = "mail@mail.com"
    const val emailPassword = "password"

    val mailer: Mailer = MailerBuilder
        .withSMTPServer(GOOGLE_SMTP_HOST, 587, emailUsername, emailPassword)
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .withSessionTimeout(500 * 1000)
        .buildMailer()

}