package com.postmage.util.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun launchInIO(block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block()
    }
}