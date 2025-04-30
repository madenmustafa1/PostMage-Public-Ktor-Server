package com.postmage.util.extensions


import com.google.gson.Gson
import com.postmage.enums.AppUserRole
import com.postmage.util.PatternUtil
import com.postmage.util.http_util.TokenUtil
import com.postmage.model.login.token.TokenDataModel
import java.io.File
import java.nio.file.Paths


fun String.isValidEmail() = PatternUtil.EMAIL_ADDRESS.matcher(this).matches()
fun String.createToken(userID: String, userRole: AppUserRole) =
    TokenUtil.createToken(
        tToken = this,
        userID = userID,
        userRole = userRole
    )

fun String.verifyToken(vararg userRole: AppUserRole) = TokenUtil.verifyToken(
    token = this,
    userRole = userRole
)

fun String.authToDataClass(): TokenDataModel? {
    return try {
        val gson = Gson()
        gson.fromJson(TokenUtil.decodeToken(this), TokenDataModel::class.java)
    } catch (e: Exception) {
        null
    }
}


fun desktopHomeFolder(): String = System.getProperty("user.home")

fun makeFolder(folderName: String): File? {
    val path = Paths.get(folderName).toFile()
    path.mkdir()
    return path
}

fun String.capitalizeFirstWord(): String {
    if (this.length < 2) {
        return this
    }

    val firstChar = this[0].uppercase()
    val restOfTheString = this.substring(1)

    return firstChar + restOfTheString
}




