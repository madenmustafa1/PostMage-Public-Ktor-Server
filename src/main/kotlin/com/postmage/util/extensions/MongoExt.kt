package com.postmage.util.extensions

import com.mongodb.client.MongoIterable
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.profile.user.UserProfileInfoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun MongoIterable<UserProfileInfoModel>.findUser(
    showPassword: Boolean,
    userProfileModel: (userProfileModel: UserProfileInfoModel) -> Unit
) {
    this.forEach { data ->
        if (data.isDeleted == false) {
            if (!showPassword) data.password = null
            userProfileModel(data)
        }
    }
}

fun MongoIterable<GetUserPostModel>.findPost(userProfileModel: (userProfileModel: GetUserPostModel) -> Unit) {
    this.forEach { data ->
        if (data.isDeleted == false) {
            userProfileModel(data)
        }
    }
}

suspend fun <T> MongoIterable<T>.asyncForeach(data: suspend (data: T) -> Unit) {
    this.forEach {
        CoroutineScope(Dispatchers.IO).launch {
            data(it)
        }
    }
}
