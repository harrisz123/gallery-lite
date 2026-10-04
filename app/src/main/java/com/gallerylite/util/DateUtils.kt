package com.gallerylite.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatToDateGroupTitle(timestampMs: Long): String {
        val itemCalendar = Calendar.getInstance().apply { timeInMillis = timestampMs }
        val nowCalendar = Calendar.getInstance()

        return when {
            isSameDay(itemCalendar, nowCalendar) -> "Today"
            isYesterday(itemCalendar, nowCalendar) -> "Yesterday"
            itemCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) -> {
                SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date(timestampMs))
            }
            else -> {
                SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(timestampMs))
            }
        }
    }

    fun isSameDayAndMonthAsToday(timestampMs: Long): Boolean {
        val target = Calendar.getInstance().apply { timeInMillis = timestampMs }
        val today = Calendar.getInstance()
        return target.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                target.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                target.get(Calendar.YEAR) < today.get(Calendar.YEAR)
    }

    fun calculateYearsAgo(timestampMs: Long): Int {
        val target = Calendar.getInstance().apply { timeInMillis = timestampMs }
        val today = Calendar.getInstance()
        return today.get(Calendar.YEAR) - target.get(Calendar.YEAR)
    }

    fun formatYearLabel(timestampMs: Long): String {
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(timestampMs))
    }

    fun formatFullDate(timestampMs: Long): String {
        return SimpleDateFormat("d MMMM yyyy · h:mm a", Locale.getDefault()).format(Date(timestampMs))
    }

    fun formatDayMonth(timestampMs: Long): String {
        return SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date(timestampMs))
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(cal1: Calendar, cal2: Calendar): Boolean {
        val yesterday = (cal2.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(cal1, yesterday)
    }
}
