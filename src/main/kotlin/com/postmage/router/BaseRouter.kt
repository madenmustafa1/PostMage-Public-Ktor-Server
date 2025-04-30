package com.postmage.router

import com.postmage.BUILD_TYPE
import com.postmage.model.app.build_type.AppBuildType
import com.postmage.util.http_util.HttpRoute
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.io.File


interface BaseRouter {
    fun access(route: Route)
}

class BaseRouterImpl {

    private val _loginRouter = LoginRouter()
    private val _formDataRouter = FormDataRouter()
    private val _profileRouter = ProfileRouter()
    private val _postsRouter = PostsRouter()
    private val _groupRouter = GroupRouter()
    private val _imageRouter = ImageRouter()
    private val _publicUserData = PublicUserDataRouter()
    private val _notificationRouter = NotificationRouter()
    private val _platformRouter = PlatformRouter()
    private val _hashtagRouter = HashtagRouter()
    private val _webRouter = WebRouter()

    fun access(routing: Routing) {
        with(HttpRoute) {
            routing {
                route(API) {
                    //Login
                    route("") { _loginRouter.access(route = this) }

                    //Form Builder
                    formData { _formDataRouter.access(route = this@routing) }

                    //Profile
                    route(PROFILE) { _profileRouter.access(route = this) }

                    //Posts
                    route(POSTS) { _postsRouter.access(route = this) }

                    //Group
                    route(GROUP) { _groupRouter.access(route = this) }

                    //Image
                    route(IMAGE) { _imageRouter.access(route = this) }

                    //Public
                    route(PUBLIC) { _publicUserData.access(route = this) }

                    //Notification
                    route(NOTIFICATION) { _notificationRouter.access(this) }

                    //PLATFORM
                    route(PLATFORM) { _platformRouter.access(this) }

                    //Hashtags
                    route(HASHTAGS) { _hashtagRouter.access(this) }
                }

                //Static File
                static {
                    resource(PRIVACY_POLICY, "/static/privacy_policy.html")
                    resource(SUPPORT, "/static/support_page.html")
                    //resource("/", "/static/home_page.html")
                }

                _webRouter.access(this)
            }
        }
    }
}

