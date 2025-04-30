package com.postmage.util.http_util

object HttpRoute {

    //BaseUrl
    //const val BASE_URL = "http://0.0.0.0:8080/"
    const val BASE_URL = "https://www.post-mage.com/api/"

    //API
    const val API = "/api"

    //Login
    const val SIGN_IN: String = "sign-in"
    const val SIGN_UP: String = "sign-up"
    const val FORGOT_PASSWORD: String = "forgot-password"
    const val FORGOT_PASSWORD_SEND_MAIL: String = "forgot-password-send-mail"
    const val VERIFIER_MAIL = "/verifier-mail"

    //PROFILE
    const val PROFILE = "/profile"
    const val USER_PROFILE: String = "/user-profile"
    const val FOLLOWER: String = "/follower-data"
    const val PROFILE_PHOTO: String = "/profile-photo"
    const val DELETE_ACCOUNT: String = "/delete_account"
    const val DELETE_ACCOUNT_WEB: String = "/delete-account-web"


    //Posts
    const val POSTS = "/user-posts"
    const val POST = "/get-post"
    const val COMMENTS = "/get-comments"
    const val USER_POSTS = "/user-post-with-userId"
    const val GROUP_POSTS = "/group-posts"
    const val ADD_POSTS = "/add-posts"
    const val UPDATE_POSTS = "/update-posts"
    const val ADD_POSTS_TO_GROUP = "/add-posts-group"
    const val USERS_POSTS: String = "/followed-users-posts"
    const val MAKE_PHOTO_PUBLIC: String = "/make-photo-public"

    //Public
    const val PUBLIC = "/public"
    const val PHOTO = "/photo" //Kullanıcının linkle indreceği url
    const val PUBLIC_PHOTO = "/public-photo"
    const val POSTS_WITH_USER_NAME = "/posts-with-user-name"
    const val USER_PROFILE_WITH_USERNAME: String = "/user-profile-with-username"

    //Notification
    const val NOTIFICATION = "/notification"
    const val NOTIFICATION_LIST = "/notification-list"

    //Group
    const val GROUP = "/group"
    const val CREATE_GROUP = "/create-group"
    const val ADD_USERS_GROUP = "/add-users-to-group"
    const val ADD_ADMIN_GROUP = "/add-admin-to-group"
    const val REMOVE_USERS_GROUP = "/remove-users-to-group"
    const val MY_GROUP_LIST = "/my-group-list"
    const val MY_GROUP_INFO = "/my-group-info"

    //Platform
    const val PLATFORM = "/platform"
    const val CURRENT_MOBILE_VERSION = "/current-mobile-version"

    //Image
    const val IMAGE = "/image"
    const val DOWNLOAD = "/download"

    //Hashtags
    const val HASHTAGS = "/hashtags"
    const val POPULAR = "/popular"
    const val HASHTAG_POST = "/hashtag-posts"

    //Static
    const val PRIVACY_POLICY = "/privacy-policy"
    const val SUPPORT = "/support"

}