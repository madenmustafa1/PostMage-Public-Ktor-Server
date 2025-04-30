package com.postmage.service.public_user

import com.mongodb.BasicDBObject
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.profile.user.ProfileUsernameModel
import com.postmage.model.profile.user.UserProfileInfoModel
import com.postmage.mongo_client.MongoInitialize
import com.postmage.mongo_client.mongo_constants.MongoSort
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.util.extensions.findUser
import com.postmage.util.file.Directory
import com.postmage.util.photo_util.PhotoUtil
import com.postmage.util.strings.AppMessageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.div
import org.litote.kmongo.eq
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class PublicUserDataService(
    private val mongoDB: MongoInitialize
) : PublicUserDataInterface {

    override suspend fun getPostPhoto(postId: String): ResponseData<File> = withContext(Dispatchers.IO) {
        val query = BasicDBObject("objectId", postId)
        var postModel: GetUserPostModel? = null

        mongoDB
            .getUsersPostsCollection
            .find(query)
            .limit(1)
            .forEach { post ->
                if (post.postIsPublic == true) postModel = post
            }

        if (postModel != null) {
            val file = File(
                PhotoUtil.getPostPath(
                    postId = postModel!!.objectId ?: "",
                    userId = postModel!!.userId ?: "",
                    getDesktopDir = true
                )
            )

            val zipFileName = file.path + "/tempZip__${System.currentTimeMillis()}.zip"
            val zipFile = File(zipFileName)

            // Klasördeki Fotoğraflar zip haline getiriliyor.
            file.listFiles()?.let { files ->
                FileOutputStream(zipFileName).use { fos ->
                    ZipOutputStream(fos).use { zos ->
                        files.forEach { file ->
                            val entry = ZipEntry(file.name)
                            zos.putNextEntry(entry)
                            FileInputStream(file).copyTo(zos)
                            zos.closeEntry()
                        }
                    }
                }
            }

            return@withContext ResponseData.success(zipFile)
        }

        return@withContext sendErrorData(AppMessageUtil.getAppMessages(LanguageType.EN).accessDenied)
    }

    override suspend fun getPublicPhoto(userId: String, photoName: String): ResponseData<File> {
        val query = BasicDBObject("userId", userId)
        var userProfile: UserProfileInfoModel? = null

        mongoDB.getUserCollection
            .find(query)
            .limit(1)
            .findUser(showPassword = false) {
                if (it.userName?.accessibility == true) userProfile = it
            }

        if (userProfile == null)
            return sendErrorData(AppMessageUtil.getAppMessages(LanguageType.EN).accessDenied)


        val inputFile = File(Directory.userDesktopDir?.path, photoName)

        return if (withContext(Dispatchers.IO) { inputFile.isFile }) {
            ResponseData.success(inputFile)

        } else sendErrorData(AppMessageUtil.getAppMessages(LanguageType.EN).photoNotFound)
    }

    override suspend fun getUserProfileInfoWithUsername(username: String): ResponseData<UserProfileInfoModel?> {
        var result: ResponseData<UserProfileInfoModel?>? = null

        mongoDB.getUserCollection
            .find(UserProfileInfoModel::userName / ProfileUsernameModel::userName eq username)
            .limit(1)
            .findUser(showPassword = false) {
                if (it.userName?.accessibility == true) {
                    result = ResponseData.success(it)
                }
            }

        result?.let { return it }
        return sendErrorData(AppMessageUtil.getAppMessages(LanguageType.EN).userNotFound)
    }

    override suspend fun getUserPostsWithUserName(username: String): ResponseData<List<GetUserPostModel>> {
        val sortDescQuery = BasicDBObject("creationTime", MongoSort.DESC)
        val userPostList = arrayListOf<GetUserPostModel>()

        var profilePhotoUrl: String? = null
        var userId = ""
        var postsIsAccess = true

        mongoDB.getUserCollection
            .find(UserProfileInfoModel::userName / ProfileUsernameModel::userName eq username)
            .limit(1).findUser(showPassword = false) { user ->
                profilePhotoUrl = user.profilePhotoUrl
                userId = user.userId ?: ""
                postsIsAccess = user.userName?.accessibility == true
            }

        if (!postsIsAccess) {
            return sendErrorData(
                message = AppMessageUtil.getAppMessages(LanguageType.EN).accessDenied,
                statusCode = StatusCodeUtil.FORBIDDEN,
            )
        }

        val query = BasicDBObject("userId", userId)

        mongoDB.getUsersPostsCollection.find(query).sort(sortDescQuery).filter { post -> post.isDeleted == false }
            .forEach {
                userPostList.add(it.copy(profilePhotoUrl = profilePhotoUrl))
            }

        return ResponseData.success(userPostList)
    }


}