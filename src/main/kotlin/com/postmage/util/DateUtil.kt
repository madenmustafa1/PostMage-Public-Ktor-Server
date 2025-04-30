package com.postmage.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    fun getTimeNow() = Calendar.getInstance(Locale.getDefault()).timeInMillis
    fun getDateNow(): String {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(getTimeNow())
        } catch (_: Exception) {
        }

        return ""
    }

    private val calendar = Calendar.getInstance()
    fun getCurrentWeekOfYear(): Int {
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.minimalDaysInFirstWeek = 4
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun getCurrentYear() = calendar.get(Calendar.YEAR)

    fun getTotalWeeksOfYear(year: Int = calendar.get(Calendar.YEAR)): Int {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.weeksInWeekYear
        return calendar.getActualMaximum(Calendar.WEEK_OF_YEAR)
    }
}