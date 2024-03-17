package com.zhufucdev.currimate.render

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RenderNode
import androidx.core.graphics.toRect
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.theme.BodyPaint
import com.zhufucdev.currimate.theme.LargeTitlePaint
import com.zhufucdev.currimate.theme.TextPaint
import java.time.ZonedDateTime

fun drawFocusedEvent(
    event: CalendarEvent,
    renderable: RenderText,
    canvas: Canvas,
    calendarIcon: Bitmap,
    timeString: String,
    titleBounds: RectF,
    zonedDateTime: ZonedDateTime,
    renderParameters: RenderParameters,
    locationBounds: RectF = event.location.toBottom(of = RectF(titleBounds).apply { left -= calendarIcon.width * 0.618f }),
    timeBounds: RectF = timeString.toBottom(of = locationBounds, margin = 12f)
) {
    val largeTitlePaint =
        if (renderParameters.drawMode == DrawMode.AMBIENT) TextPaint else LargeTitlePaint
    renderable.paint = largeTitlePaint

    val node = RenderNode("event title")
    val titleBoundsMapped = RectF(0f, 0f, titleBounds.width(), titleBounds.height())

    if (renderParameters.drawMode != DrawMode.AMBIENT) {
        val position = titleBounds.toRect().apply { bottom += 10 } // TODO better fix (text baseline)
        node.setPosition(position)
        val extendedBounds = Rect(position).apply { offsetTo(0, 0) }
        val titleCanvas = node.beginRecording()
        renderable.render(
            canvas = titleCanvas,
            bounds = extendedBounds,
            contentBounds = titleBoundsMapped,
            zonedDateTime = zonedDateTime,
            renderParameters = renderParameters
        )
        node.endRecording()
        canvas.drawBitmap(
            calendarIcon,
            titleBounds.left - calendarIcon.width - 4f,
            titleBounds.top + (titleBounds.height() - calendarIcon.height) / 2 + 2f,
            largeTitlePaint
        )
    } else {
        val position = Rect(titleBounds.toRect()).apply {
            left -= calendarIcon.width / 2
            bottom += 10
        }
        val extendedBounds = Rect(position).apply { offsetTo(0, 0) }
        node.setPosition(position)
        val titleCanvas = node.beginRecording()
        renderable.render(
            titleCanvas,
            extendedBounds,
            titleBoundsMapped,
            zonedDateTime,
            renderParameters
        )
        node.endRecording()
    }
    canvas.drawRenderNode(node)

    canvas.drawText(
        event.location,
        locationBounds.left,
        locationBounds.bottom,
        BodyPaint
    )
    canvas.drawText(timeString, timeBounds.left, timeBounds.bottom, BodyPaint)
}
