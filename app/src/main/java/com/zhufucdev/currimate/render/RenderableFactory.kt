package com.zhufucdev.currimate.render

import android.content.Context
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.beginInstant
import com.zhufucdev.currimate.contains
import com.zhufucdev.currimate.watchface.UserStyleHolder
import java.time.Duration
import java.time.ZonedDateTime

class RenderableFactory(private val styleHolder: UserStyleHolder, private val context: Context) {
    private val renderWatchface = RenderWatchface(context, styleHolder)
    private var mCurrNext: Triple<CalendarEvent, CalendarEvent, RenderCurrentAndNext>? = null

    private fun renderCurrentAndNext(current: CalendarEvent, next: CalendarEvent) =
        mCurrNext?.takeIf { it.first == current && it.second == next }?.third
            ?: RenderCurrentAndNext(context, styleHolder, current, next)
                .also {
                    mCurrNext?.third?.onDestroy()
                    mCurrNext = Triple(current, next, it)
                }

    private var mSolo: Pair<CalendarEvent, RenderSoloOngoing>? = null

    private fun renderSoloOngoing(event: CalendarEvent) =
        mSolo?.takeIf { it.first == event }?.second
            ?: RenderSoloOngoing(context, styleHolder, event)
                .also {
                    mSolo?.second?.onDestroy()
                    mSolo = event to it
                }

    fun getRenderer(zonedDateTime: ZonedDateTime, events: List<CalendarEvent>): Renderable =
        if (events.isEmpty()) {
            renderWatchface
        } else {
            val currentEvent = events.first()
            if (zonedDateTime in currentEvent) {
                if (events.size < 2
                    || Duration.between(
                        zonedDateTime.toInstant(),
                        events[1].beginInstant
                    ) > Duration.ofHours(1)
                ) {
                    renderSoloOngoing(currentEvent)
                } else {
                    renderCurrentAndNext(currentEvent, events[1])
                }
            } else if (Duration.between(zonedDateTime.toInstant(), currentEvent.beginInstant)
                    .let { !it.isNegative && it.toHours() <= 1 }
            ) {
                renderSoloOngoing(currentEvent)
            } else {
                renderWatchface
            }
        }

    fun onDestroy() {
        mCurrNext?.third?.onDestroy()
        mSolo?.second?.onDestroy()
    }
}
