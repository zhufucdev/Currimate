package com.zhufucdev.currimate

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.CalendarContract
import android.util.Log
import androidx.wear.provider.WearableCalendarContract
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class CalendarEvents(private val contentResolver: ContentResolver) {
    /**
     * Query calendar events between a time range (UTC epoch)
     * @return [CalendarEvent]s that are guaranteed to be sorted ascendingly
     */
    operator fun get(rangeMills: LongRange): List<CalendarEvent> {
        val builder = WearableCalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, rangeMills.first)
        ContentUris.appendId(builder, rangeMills.last)

        return buildList {
            try {
                contentResolver.query(
                    builder.build(),
                    projection,
                    null,
                    null
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        add(
                            CalendarEvent(
                                id = cursor.getLong(EVENT_ID_INDEX),
                                beginMills = cursor.getLong(BEGIN_INDEX),
                                endMills = cursor.getLong(END_INDEX),
                                title = cursor.getString(TITLE_INDEX),
                                location = cursor.getString(EVENT_LOCATION_INDEX)
                            )
                        )
                    }
                }
            } catch (e: SecurityException) {
                Log.w("calendar", "Permission denied while querying event instances")
            }
        }.sorted()
    }
}

private val projection = arrayOf(
    CalendarContract.Instances.EVENT_ID,
    CalendarContract.Instances.TITLE,
    CalendarContract.Instances.BEGIN,
    CalendarContract.Instances.END,
    CalendarContract.Instances.EVENT_LOCATION
)

private const val EVENT_ID_INDEX = 0
private const val TITLE_INDEX = 1
private const val BEGIN_INDEX = 2
private const val END_INDEX = 3
private const val EVENT_LOCATION_INDEX = 4

data class CalendarEvent(
    val id: Long,
    val title: String,
    val beginMills: Long,
    val endMills: Long,
    val location: String
) : Comparable<CalendarEvent> {
    override fun compareTo(other: CalendarEvent): Int = beginMills.compareTo(other.beginMills)
}

operator fun CalendarEvent.contains(time: ZonedDateTime) =
    time.toInstant().toEpochMilli() in beginMills until endMills

val CalendarEvent.beginInstant: Instant get() = Instant.ofEpochMilli(beginMills)
val CalendarEvent.endInstant: Instant get() = Instant.ofEpochMilli(endMills)
