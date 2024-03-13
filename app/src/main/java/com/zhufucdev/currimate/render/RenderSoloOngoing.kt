package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toRectF
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.style.CurrentUserStyleRepository
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.R
import com.zhufucdev.currimate.theme.LargeTitlePaint
import com.zhufucdev.currimate.theme.TextPaint
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.ZonedDateTime

class RenderSoloOngoing(
    sharedAssets: WatchFaceCanvasRenderer.CurrimateSharedAssets,
    currentUserStyleRepository: CurrentUserStyleRepository,
    private val event: CalendarEvent
) : RenderTimeText(sharedAssets, currentUserStyleRepository) {
    private val calendarIcon =
        sharedAssets.fromDrawable(R.drawable.ic_calendar_start_outline, Color.White)

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
            currentUserStyleRepository
        )

        drawFocusedEvent(
            event,
            canvas,
            calendarIcon,
            timeString,
            titleBounds,
            renderParameters,
        )
        super.render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }
}
