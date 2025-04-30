package com.postmage.di

import com.postmage.mongo_client.MongoInitialize
import com.postmage.repo.*
import com.postmage.repo.PublicUserDataRepository
import com.postmage.router.BaseRouterImpl
import com.postmage.service.group.GroupInterface
import com.postmage.util.AppMessages
import com.postmage.service.login.LoginInterface
import org.koin.core.module.Module
import org.koin.dsl.module
import com.postmage.service.group.GroupService
import com.postmage.service.image.ImageInterface
import com.postmage.service.image.ImageService
import com.postmage.service.logger.LoggerInterface
import com.postmage.service.logger.LoggerService
import com.postmage.service.login.LoginService
import com.postmage.service.notification.NotificationService
import com.postmage.service.platform.PlatformService
import com.postmage.service.profile.ProfileInterface
import com.postmage.service.profile.ProfileService
import com.postmage.service.public_user.PublicUserDataInterface
import com.postmage.service.public_user.PublicUserDataService
import com.postmage.service.hashtags.HashtagsService
import com.postmage.service.user_posts.UserPostsInterface
import com.postmage.service.user_posts.UserPostsService
import com.postmage.util.exception.ExceptionLogger
import com.postmage.util.notification.AppNotification
import com.postmage.vm.*
import com.postmage.vm.LoggerVM

fun injectModule(): Module {
    return module {

        single { AppMessages() }
        single { MongoInitialize() }
        single { AppNotification() }

        //Router
        single { BaseRouterImpl() }

        //Exception Logger
        single { ExceptionLogger(mongoDB = get()) }

        //Profile
        single { ProfileService(mongoDB = get()) }
        single { ProfileRepository(profileService = get(), appMessages = get()) as ProfileInterface }

        //Login
        single { LoginService(mongoDB = get()) }
        single { LoginRepository(longinService = get(), appMessages = get()) as LoginInterface }

        //Notification
        single {
            NotificationService(
                mongoDB = get(),
                profileRepository = get()
            )
        }
        single { NotificationRepository(notificationService = get(), appMessages = get()) }

        single { HashtagsService(mongoDB = get()) }
        single { HashtagRepository(hashtagsService = get()) }

        //AddPosts
        single {
            UserPostsService(
                mongoDB = get(),
                notificationRepository = get(),
                //hashtagRepository = get()
            )
        }

        single { UserPostRepository(userPostsService = get()) as UserPostsInterface }

        //Image
        single { ImageService(mongoDB = get()) }
        single { ImageRepository(imageService = get(), appMessages = get()) as ImageInterface }

        //Group
        single { GroupService(mongoDB = get(), appMessages = get(), notificationRepository = get()) }
        single { GroupRepository(groupService = get(), appMessages = get()) as GroupInterface }

        //Logger
        single { LoggerService(mongoDB = get()) }
        single { LoggerRepository(loggerService = get()) as LoggerInterface }


        //PublicUserData
        single {
            PublicUserDataService(
                mongoDB = get()
            )
        }
        single {
            PublicUserDataRepository(
                publicUserDataService = get(),
            ) as PublicUserDataInterface
        }

        //Platform
        single { PlatformService(mongoDB = get()) }
        single { PlatformRepository(platformService = get()) }

        //By VM
        single<LoginVM> { LoginVM(LoginRepository(longinService = get(), appMessages = get())) }
        single<ProfileVM> { ProfileVM(ProfileRepository(profileService = get(), appMessages = get()), get()) }
        single<UserPostsVM> { UserPostsVM(UserPostRepository(userPostsService = get())) }
        single<ImageVM> { ImageVM(ImageRepository(imageService = get(), appMessages = get())) }
        single<GroupVM> { GroupVM(GroupRepository(groupService = get(), appMessages = get()), get()) }
        single<HashtagVM> { HashtagVM(HashtagRepository(hashtagsService = get())) }
        single<LoggerVM> { LoggerVM(LoggerRepository(loggerService = get())) }
        single<PlatformVM> { PlatformVM(PlatformRepository(platformService = get())) }
        single<NotificationVM> {
            NotificationVM(
                NotificationRepository(
                    notificationService = get(),
                    appMessages = get()
                ), get()
            )
        }
        single<PublicUserDataVM> {
            PublicUserDataVM(
                PublicUserDataRepository(
                    publicUserDataService = get()
                )
            )
        }
    }
}