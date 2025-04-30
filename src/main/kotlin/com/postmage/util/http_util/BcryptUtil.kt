package com.postmage.util.http_util

import at.favre.lib.crypto.bcrypt.BCrypt

class BcryptUtil {

    fun verifyPassword(request: String, expected: String): Boolean {
        val bcryptResult = BCrypt.verifyer().verify(request.toCharArray(), expected)
        return bcryptResult.verified
    }

}

fun String.verifyPassword(expected: String): Boolean {
    return BcryptUtil().verifyPassword(request = this, expected = expected)
}