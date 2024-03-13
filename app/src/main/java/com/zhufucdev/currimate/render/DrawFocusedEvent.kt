package com.zhufucdev.currimate.render

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.theme.BodyPaint
import com.zhufucdev.currimate.theme.LargeTitlePaint
import com.zhufucdev.currimate.theme.TextPaint

fun drawFocusedEvent(
    event: CalendarEvent,
    canvas: Canvas,
    calendarIcon: Bitmap,
    timeString: String,
    titleBounds: RectF,
    renderParameters: RenderParameters,
    locationBounds: RectF = event.location.toBottom(of = RectF(titleBounds).apply { left -= calendarIcon.width * 0.618f }),
    timeBounds: RectF = timeString.toBottom(of = locationBounds, margin = 12f)
) {
    val largeTitlePaint =
        if (renderParameters.drawMode == DrawMode.AMBIENT) TextPaint else LargeTitlePaint

    if (renderParameters.drawMode != DrawMode.AMBIENT) {
        canvas.drawText(
            event.title,
            titleBounds.left,
            titleBounds.bottom,
            largeTitlePaint
        )
        canvas.drawBitmap(
            calendarIcon,
            titleBounds.left - calendarIcon.width,
            titleBounds.top + 8f,
            largeTitlePaint
        )
    } else {
        canvas.drawText(
            event.title,
            titleBounds.left - calendarIcon.width / 2f,
            titleBounds.bottom,
            largeTitlePaint
        )
    }
    canvas.drawText(
        event.location,
        locationBounds.left,
        locationBounds.bottom,
        BodyPaint
    )
    canvas.drawText(timeString, timeBounds.left, timeBounds.bottom, BodyPaint)
}
