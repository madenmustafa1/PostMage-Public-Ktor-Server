package com.postmage.vm

import com.postmage.enums.PostType
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.group.GroupIdModel
import com.postmage.model.posts.add_posts.AddPostAudioModel
import com.postmage.model.posts.add_posts.AddPostModel
import com.postmage.model.posts.add_posts.AddPostPhotoModel
import com.postmage.model.posts.followed_users.PostOfFollowedUsers
import com.postmage.model.posts.make_photo_public.MakePhotoPublicRequestModel
import com.postmage.model.posts.update_posts.UpdateUserPostModel
import com.postmage.model.user_config.getLanguageType
import com.postmage.model.user_config.toHeaders
import com.postmage.plugins.koin
import com.postmage.repo.UserPostRepository
import com.postmage.util.UuidUtil
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.file.FileControl
import com.postmage.util.http_util.GsonUtil
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UserPostsVM(
    private val repository: UserPostRepository
) {

    suspend fun addPost(call: ApplicationCall, addPostType: PostType = PostType.ADD_PERSONAL) {
        runCatchableException {
            val model = AddPostModel()

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "description" -> model.description = part.value
                            "groupId" -> model.groupId = part.value
                            "hashtags" -> model.hashtags = GsonUtil
                                .jsonToGsonArray<ArrayList<String>>(json = part.value) ?: arrayListOf()
                            "audioName" ->
                                model.audioModel = model.audioModel.copy(userDefinedName = part.value)
                        }
                    }

                    is PartData.FileItem -> {
                        val photoName = part.originalFileName ?: ""
                        if (FileControl.isPhoto(photoName)) {
                            model.photoList.add(
                                AddPostPhotoModel(
                                    photoName = UuidUtil.createUuid().toString() + part.originalFileName as String,
                                    photoByteArray = part.streamProvider().readBytes()
                                )
                            )
                        }

                        //Şarkı eklenmesi yapılacak
                        if (FileControl.isAudio(photoName)) {
                            model.audioModel = model.audioModel.copy(
                                audioName = UuidUtil.createUuid().toString() + part.originalFileName as String,
                                audioByteArray = part.streamProvider().readBytes()
                            )
                        }
                    }

                    else -> {}
                }
                part.dispose()
            }

            val result = repository.addPost(call.request.toHeaders(), model, addPostType)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getUserPostsWithUserId(call: ApplicationCall) {
        runCatchableException {
            val userId = call.request.queryParameters["userId"] ?: ""
            val result = repository.getUserPostsWithUserId(call.request.toHeaders(), userId)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getPost(call: ApplicationCall) {
        runCatchableException {
            val result = repository.getPost(
                call.request.toHeaders(),
                call.request.queryParameters["postId"] ?: ""
            )

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getComments(call: ApplicationCall) {
        runCatchableException {
            val result = repository.getComments(
                call.request.toHeaders(),
                call.request.queryParameters["postId"] ?: ""
            )

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun updatePost(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<UpdateUserPostModel>()
            val result = repository.updatePost(call.request.toHeaders(), body)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getGroupPost(call: ApplicationCall) {
        runCatchableException {
            val body = call.receive<GroupIdModel>()
            val result = repository.getGroupPost(call.request.toHeaders(), body)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun postOfFollowedUsers(call: ApplicationCall) {
        runCatchableException {
            var body: PostOfFollowedUsers? = null
            kotlin.runCatching { body = call.receiveNullable<PostOfFollowedUsers>() }.getOrNull()
            //var body= call.receiveOrNull<PostOfFollowedUsers>()
            if (body == null) body = PostOfFollowedUsers(limit = 100)
            val result = repository.postOfFollowedUsers(call.request.toHeaders(), body!!)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun putMakePhotoPublic(call: ApplicationCall) {
        runCatchableException {
            var body: MakePhotoPublicRequestModel? = null
            kotlin.runCatching { body = call.receiveNullable<MakePhotoPublicRequestModel>() }.getOrNull()

            if (body == null) {
                sendException(
                    call = call,
                    statusCode = StatusCodeUtil.BAD_REQUEST,
                    errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
                )
                return@runCatchableException
            }

            val result = repository.putMakePhotoPublic(call.request.toHeaders(), body!!)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

}