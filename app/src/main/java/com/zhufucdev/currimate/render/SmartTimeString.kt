package com.zhufucdev.currimate.render

import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.R
import com.zhufucdev.currimate.beginInstant
import com.zhufucdev.currimate.endInstant
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.Duration
import java.time.Instant

fun Renderable.smartTimeString(event: CalendarEvent): String {
    val t =
        Duration.between(Instant.now(), event.beginInstant).toMinutes().toInt()
    return if (t > 0) {
        context.resources.getQuantityString(R.plurals.par_in_minutes, t, t)
    } else if (t == 0) {
        context.getString(R.string.par_at_present)
    } else if (t > -10) {
        context.resources
            .getQuantityString(R.plurals.par_minutes_ago, -t, -t)
    } else {
        val k = Duration.between(Instant.now(), event.endInstant).toMinutes().toInt()
        context.resources
            .getQuantityString(R.plurals.par_minutes_remaining, k, k)
    }
}
