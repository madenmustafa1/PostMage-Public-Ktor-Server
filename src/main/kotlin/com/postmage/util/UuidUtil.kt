package com.postmage.util

import java.util.UUID

object UuidUtil {
    fun createUuid(): UUID = UUID.randomUUID()
}