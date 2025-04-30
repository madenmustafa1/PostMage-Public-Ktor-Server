package com.postmage.model.error

data class GeneralErrorModel(
    val message: String?,
    val reason: String?,
    val code: Int?,
    val line: String?
)
