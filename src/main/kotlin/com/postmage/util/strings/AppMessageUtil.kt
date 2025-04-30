package com.postmage.util.strings

import com.google.gson.Gson
import com.postmage.BUILD_TYPE
import com.postmage.model.app.build_type.AppBuildType
import com.postmage.model.app.app_message.AppMessages
import com.postmage.model.app.app_message.LanguageType
import java.io.File

object AppMessageUtil {

    private val appMessagesMap = hashMapOf<LanguageType, AppMessages>()
    fun init() {
        LanguageType.values().forEach {
            appMessagesMap[it] = convertAppMessages(it.value)
        }
    }

    private fun convertAppMessages(language: String?): AppMessages {
        val jsonFile = File(getPath(language))
        return Gson().fromJson(jsonFile.readText(), AppMessages::class.java)
    }
    //val path = "src/main/resources/strings/"
    private fun getPath(language: String?): String {
        val path = when(BUILD_TYPE) {
            AppBuildType.DEV, AppBuildType.TEST -> "src/main/resources/static/strings/"
            AppBuildType.PROD -> "/app/com/postmage/util/strings/"
        }

        LanguageType.values().forEach {
            if (it.value == language?.trim()
                    ?.lowercase()
            ) return path + it.value + ".json"
        }

        return path + LanguageType.EN.value + ".json"
    }

    fun getAppMessages(language: LanguageType): AppMessages {
        return appMessagesMap[language] ?: (appMessagesMap[LanguageType.EN]!!)
    }

}

