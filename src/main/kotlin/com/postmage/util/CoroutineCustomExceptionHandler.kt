package com.postmage.util

import kotlinx.coroutines.CoroutineExceptionHandler

object CoroutineCustomExceptionHandler {
    val handler = CoroutineExceptionHandler { _, exception ->
        //Exception logger
    }
}