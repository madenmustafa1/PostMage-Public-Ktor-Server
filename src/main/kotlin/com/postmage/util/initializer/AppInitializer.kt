package com.postmage.util.initializer

import com.postmage.plugins.koin
import com.postmage.util.file.Directory
import com.postmage.util.strings.AppMessageUtil

fun appInitializer() {
    Directory.initializeDirectory()
    AppMessageUtil.init()
    koin.appNotification.init()
}