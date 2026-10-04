package com.gallerylite.data.repository

import com.gallerylite.data.model.OnThisDayMemory
import com.gallerylite.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class OnThisDayRepository(
    private val mediaRepository: MediaRepository
) {
    suspend fun getMemoriesForToday(): List<OnThisDayMemory> = withContext(Dispatchers.IO) {
        val allMedia = mediaRepository.queryAllMedia()

        // Filter media where month and day match today in past years
        val matchingMedia = allMedia.filter { item ->
            DateUtils.isSameDayAndMonthAsToday(item.dateTakenMs)
        }

        matchingMedia
            .groupBy { item -> DateUtils.calculateYearsAgo(item.dateTakenMs) }
            .filter { (yearsAgo, _) -> yearsAgo > 0 }
            .map { (yearsAgo, items) ->
                val sampleTimestamp = items.first().dateTakenMs
                val year = Calendar.getInstance().apply { timeInMillis = sampleTimestamp }.get(Calendar.YEAR)
                val dayMonth = DateUtils.formatDayMonth(sampleTimestamp)
                val yearsAgoLabel = if (yearsAgo == 1) "1 Year Ago" else "$yearsAgo Years Ago"

                OnThisDayMemory(
                    yearsAgo = yearsAgo,
                    year = year,
                    dateDisplay = "$dayMonth $year · $yearsAgoLabel",
                    items = items.sortedByDescending { it.dateTakenMs }
                )
            }
            .sortedBy { it.yearsAgo } // Nearest past years first (1 year ago, 2 years ago...)
    }
}
