package com.postmage.di


import com.postmage.router.BaseRouterImpl
import com.postmage.util.AppMessages
import com.postmage.util.exception.ExceptionLogger
import com.postmage.util.notification.AppNotification
import com.postmage.vm.*
import com.postmage.vm.LoggerVM
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class KoinApplication: KoinComponent {

    val baseRouter by inject<BaseRouterImpl>()
    val appMessages by inject<AppMessages>()
    val appNotification by inject<AppNotification>()

    val exceptionLogger by inject<ExceptionLogger>()

    //Inject by VM
    val loginVM by inject<LoginVM>()
    val profileVM by inject<ProfileVM>()
    val usersPostsVM by inject<UserPostsVM>()
    val imageVM by inject<ImageVM>()
    val groupVM by inject<GroupVM>()
    val hashtagVM by inject<HashtagVM>()
    val httpLogVM by inject<LoggerVM>()
    val platformVM by inject<PlatformVM>()
    val publicUserDataVM by inject<PublicUserDataVM>()
    val notificationVM by inject<NotificationVM>()
}
