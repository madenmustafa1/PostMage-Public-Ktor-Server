package com.postmage

import com.postmage.di.injectModule
import com.postmage.model.app.build_type.AppBuildType
import io.ktor.server.application.*
import com.postmage.plugins.*
import com.postmage.util.initializer.appInitializer
import io.ktor.http.*
import org.koin.core.context.startKoin
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*

val BUILD_TYPE: AppBuildType = AppBuildType.DEV

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowCredentials = true
        anyHost()
    }
    injectKoin()
    configureSerialization()
    configureMonitoring()
    configureRouting()
    appInitializer()
}

/*
fun changePostRouteName() {
    CoroutineScope(Dispatchers.IO).launch {
        val mongoDB = MongoInitialize()
        val modifiedList = ArrayList<UpdateOneModel<GetUserPostModel>>()

        mongoDB.getUsersPostsCollection.find().forEach {

            val targetPath = PhotoUtil.getPostPath(
                postId = it.objectId!!,
                userId = it.userId!!,
                getDesktopDir = false
            )

            modifiedList.add(
                UpdateOneModel(
                    Filters.eq("objectId", it.objectId),
                    Updates.set(
                        "photoList", arrayListOf(
                            GetUserPostPhotoModel(
                                id = ObjectId.get().toString(),
                                photoName = targetPath + "/" + it.photoName
                            )
                        )
                    )
                )
            )
        }

        mongoDB.getUsersPostsCollection.bulkWrite(modifiedList)

        photoChangeRoute()
    }
}

suspend fun photoChangeRoute() = withContext(Dispatchers.IO) {
    val mongoDB = MongoInitialize()

    mongoDB.getUserCollection.find().forEach {
        val query = BasicDBObject("userId", it.userId!!)

        mongoDB.getUsersPostsCollection.find(query).forEach { postModel ->

            val sourcePath = File(Directory.userDesktopDir?.path, postModel.photoName)

            PhotoUtil.createPostPath(
                postId = postModel.objectId!!,
                userId = postModel.userId!!
            )

            val targetPath = PhotoUtil.getPostPath(
                postId = postModel.objectId!!,
                userId = postModel.userId!!,
                getDesktopDir = true
            )

            Files.copy(sourcePath.toPath(), File(targetPath + "/" + postModel.photoName).toPath(), StandardCopyOption.REPLACE_EXISTING)
            sourcePath.delete()
        }
    }
}
 */

fun injectKoin() {
    startKoin {
        printLogger()
        modules(injectModule())
    }
}