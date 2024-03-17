package com.zhufucdev.currimate.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toRectF
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.R
import com.zhufucdev.currimate.theme.LargeTitlePaint
import com.zhufucdev.currimate.theme.TextPaint
import com.zhufucdev.currimate.watchface.UserStyleHolder
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class RenderSoloOngoing(
    context: Context,
    styleHolder: UserStyleHolder,
    private val event: CalendarEvent
) : RenderTimeText(context, styleHolder) {
    private val titleRenderable = RenderText(event.title, TextPaint, context, styleHolder)
    private val calendarIcon =
        context.fromDrawable(R.drawable.ic_calendar_start_outline, Color.White)

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        val largeTitlePaint =
            if (renderParameters.drawMode == DrawMode.AMBIENT) TextPaint else LargeTitlePaint

        val titleBounds = run {
            val t = Rect()
            largeTitlePaint.getTextBounds(event.title, 0, event.title.length, t)
            if (t.width() > contentBounds.width()) {
                t.left = contentBounds.left.roundToInt()
                t.right = contentBounds.right.roundToInt()
            }
            t.toRectF().apply {
                offsetTo(
                    contentBounds.centerX() + (calendarIcon.width - t.width()) / 2f,
                    0f
                )
            }
        }
        val locationBounds =
            event.location.toBottom(of = RectF(titleBounds).apply { left -= calendarIcon.width * 0.618f })
        val timeString = smartTimeString(event)
        val timeBounds = timeString.toBottom(of = locationBounds, margin = 12f)
        titleBounds.offset(0f, bounds.exactCenterY() - timeBounds.bottom / 2)

        drawClock(
            canvas,
            bounds,
            PointF(bounds.exactCenterX(), bounds.exactCenterY()),
            zonedDateTime,
            renderParameters,
            styleHolder.colors
        )

        drawFocusedEvent(
            event,
            titleRenderable,
            canvas,
            calendarIcon,
            timeString,
            titleBounds,
            zonedDateTime,
            renderParameters,
        )
        super.render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }
}
