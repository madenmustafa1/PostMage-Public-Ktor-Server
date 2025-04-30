package com.postmage.enums


enum class AppUserRole {
    ANYONE,
    USER,
    ADMIN,
    DEV_READ,
    DEV_AND_WRITE,
}

fun userRouteRole(): ArrayList<AppUserRole> {
    return arrayListOf(
        AppUserRole.USER,
        AppUserRole.ADMIN,
    )
}