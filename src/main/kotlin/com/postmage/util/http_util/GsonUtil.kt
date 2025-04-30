package com.postmage.util.http_util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object GsonUtil {
    val gson = Gson()

    fun <T> gsonToJson(model: T): String {
        return gson.toJson(model)
    }

    inline fun <reified T> jsonToGson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    inline fun <reified T> jsonToGsonArray(json: String): T? {
        try {
            val type: Type = object : TypeToken<T>() {}.type
            return gson.fromJson<T>(json, type)
        } catch (e: Exception) {
            println(e.localizedMessage)
            return null
        }
    }
}