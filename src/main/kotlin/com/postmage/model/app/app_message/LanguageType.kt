package com.postmage.model.app.app_message


//Buraya eklediğinde dockerfile'a eklemeyi unutma yoksa uygulama çalışmaz
enum class LanguageType(val value: String) {
    EN("en"),
    TR("tr");

    companion object {
        fun getLanguageType(value: String?): LanguageType {
            LanguageType.values().forEach {
                if (it.value == value?.replace(" ", "")?.trim()) return it
            }
            return EN
        }
    }
}