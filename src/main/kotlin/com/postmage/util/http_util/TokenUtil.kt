package com.postmage.util.http_util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.postmage.enums.AppUserRole
import com.postmage.util.extensions.authToDataClass
import com.postmage.util.Constants
import java.util.*


object TokenUtil {
    fun createToken(tToken: String, userID: String, userRole: AppUserRole): String? {
        val uuid = UUID.randomUUID()
        val token = try {
            val algorithm = Algorithm.HMAC256(Constants.SERVER_TOKEN_SECRET_KEY)
            JWT.create()
                .withIssuer("auth0")
                .withClaim("token", tToken)
                .withClaim("uuid", uuid.toString())
                .withClaim("userId", userID)
                .withClaim("userRole", userRole.ordinal)
                .withExpiresAt(expiryDate())
                .sign(algorithm)
        } catch (exception: JWTCreationException) {
            null
        }
        return token
    }

    private fun expiryDate(): Date {
        val calendar = Calendar.getInstance()
        val calFebruary = Calendar.getInstance()
        calFebruary[Calendar.MONTH] = Calendar.FEBRUARY

        calendar.add(Calendar.MONTH, 1)
        calendar[Calendar.DATE] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        return calendar.time
    }


    fun verifyToken(token: String?, vararg userRole: AppUserRole): Boolean {
        if (token == null) return false
        return try {
            val replaceAuth = token.replace("Bearer ", "")

            val model = replaceAuth.authToDataClass() ?: return false

            val algorithm = Algorithm.HMAC256(Constants.SERVER_TOKEN_SECRET_KEY)
            val verifier: JWTVerifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .withClaim("userId", model.userId)
                .build() //Reusable verifier instance
            val jwt: DecodedJWT = verifier.verify(replaceAuth)

            if (!userRole.contains(AppUserRole.values()[model.userRole])) return false
            true
        } catch (exception: JWTVerificationException) {
            //Invalid signature/claims
            false
        }
    }

    fun decodeToken(jwt: String): String {
        val replaceAuth = jwt.replace("Bearer ", "")
        val parts = replaceAuth.split(".")
        return try {
            val charset = charset("UTF-8")
            val header = String(Base64.getUrlDecoder().decode(parts[0].toByteArray(charset)), charset)
            val payload = String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset)
            "$header"
            "$payload"
        } catch (e: Exception) {
            "Error parsing JWT: $e"
        }
    }
}