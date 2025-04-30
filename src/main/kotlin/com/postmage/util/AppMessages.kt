package com.postmage.util

class AppMessages {
    //GENERAL ERROR
    val SERVER_ERROR by lazy { "Something went wrong." }
    val MODEL_IS_NOT_VALID by lazy { "Model is not valid." }
    val ACCESS_DENIED by lazy { "Access denied" }
    val UNAUTHORIZED by lazy { "Unauthorized." }

    //User
    val NOT_VALID_EMAIL by lazy { "E-mail not valid." }
    val PASSWORD_NOT_BE_SHORT by lazy { "Password cannot be shorter than 5 characters" }
    val NAME_SURNAME_NOT_BE_EMPTY by lazy { "Name Surname cannot be empty" }
    val GENDER_INVALIDATE by lazy { "Gender invalidate. INT: 0 MALE, 1 FEMALE" }
    val EMAIL_NOT_UNIQUE by lazy { "E-mail already exits" }
    val EMAIL_OR_PASSWORD_INCORRECT by lazy { "E-mail or password incorrect." }
    val USER_NOT_FOUND by lazy { "User not found." }



    //Post
    val USER_ID_NOT_BE_NULL by lazy { "userId cannot be null." }
    val POST_ID_NOT_BE_NULL by lazy { "postId cannot be null." }
    val GROUP_ID_NOT_BE_NULL by lazy { "groupId cannot be null." }
    val WRONG_USER_ID by lazy { "Wrong userId." }
    val GROUP_NAME_NOT_BE_NULL by lazy { "groupName cannot be null." }
    val POST_NOT_FOUND by lazy { "Post not found." }
    val PHOTO_SAVE_ERROR by lazy { "An error occurred while saving the photo." }

    //Photo
    val PHOTO_CANNOT_BE_EMPTY by lazy { "Photo cannot be empty" }
    val PHOTO_NAME_CANNOT_BE_EMPTY by lazy { "Photo name cannot be empty" }
    val PHOTO_NAME_NOT_UNIQUE by lazy { "Photo name already exits" }
    val PHOTO_NOT_FOUND by lazy { "Photo not found" }
}