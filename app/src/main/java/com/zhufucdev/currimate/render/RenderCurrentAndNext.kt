package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toRectF
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.R
import com.zhufucdev.currimate.beginInstant
import com.zhufucdev.currimate.endInstant
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

class RenderCurrentAndNext(
    sharedAssets: WatchFaceCanvasRenderer.CurrimateSharedAssets,
    private val current: CalendarEvent,
    private val next: CalendarEvent
) : RenderTimeText(sharedAssets) {
    private val timerStandIcon =
        sharedAssets.fromDrawable(
            R.drawable.ic_timer_sand,
            Color.White.copy(alpha = 0.5f),
            120,
            120
        )
    private val calendarIcon =
        sharedAssets.fromDrawable(R.drawable.ic_calendar_start_outline, Color.White)

    private val parPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.White.toArgb()
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
    }

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.White.toArgb()
        textSize = 24f
    }

    private fun timeRemainingString(event: CalendarEvent): String {
        val t = Duration.between(Instant.now(), event.endInstant).toMinutes().toInt()
        return sharedAssets.context.resources
            .getQuantityString(R.plurals.par_minutes_remaining, t, t)
    }

    private fun smartTimeString(event: CalendarEvent): String {
        val t =
            Duration.between(Instant.now(), event.beginInstant).toMinutes().toInt()
        return if (t > 0) {
            sharedAssets.context.resources.getQuantityString(R.plurals.par_in_minutes, t, t)
        } else if (t == 0) {
            sharedAssets.context.getString(R.string.par_at_present)
        } else if (t > -10) {
            sharedAssets.context.resources
                .getQuantityString(R.plurals.par_minutes_ago, -t, -t)
        } else {
            val k = Duration.between(Instant.now(), event.endInstant).toMinutes().toInt()
            sharedAssets.context.resources
                .getQuantityString(R.plurals.par_minutes_remaining, k, k)
        }
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        val titlePaint =
            if (renderParameters.drawMode == DrawMode.AMBIENT) sharedAssets.textPaint else sharedAssets.titlePaint

        val currTitleSize = Rect()
        titlePaint.getTextBounds(current.title, 0, current.title.length, currTitleSize)

        val currRemainingStr = timeRemainingString(current)
        val currRemainingBounds = run {
            val t = Rect()
            parPaint.getTextBounds(
                currRemainingStr,
                0,
                currRemainingStr.length,
                t
            )
            t.toRectF().apply {
                offsetTo(
                    (contentBounds.centerX() - currTitleSize.width() / 2f),
                    (contentBounds.top + currTitleSize.height() + 12),
                )
            }
        }

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val timerStandIconBounds = RectF(
                contentBounds.centerX() - timerStandIcon.width / 2f,
                contentBounds.top -
                    (timerStandIcon.height - currTitleSize.height() - currRemainingBounds.height()) / 2f,
                contentBounds.centerX() + timerStandIcon.width / 2f,
                contentBounds.top + currTitleSize.bottom +
                    (timerStandIcon.height - currTitleSize.height() - currRemainingBounds.height()) / 2f
            )

            val iconMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                isDither = true
                shader = LinearGradient(
                    timerStandIconBounds.centerX(),
                    timerStandIconBounds.top,
                    timerStandIconBounds.centerX(),
                    timerStandIconBounds.bottom,
                    intArrayOf(
                        Color.Black.toArgb(),
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                        Color.Black.toArgb()
                    ),
                    floatArrayOf(0f, 0.4f, 0.6f, 1f),
                    Shader.TileMode.MIRROR
                )
            }
            canvas.drawBitmap(
                timerStandIcon,
                timerStandIconBounds.left,
                timerStandIconBounds.top,
                null
            )
            canvas.drawRect(timerStandIconBounds, iconMaskPaint)
        }

        canvas.drawText(
            current.title,
            contentBounds.centerX() - currTitleSize.width() / 2f,
            contentBounds.top + currTitleSize.height(),
            titlePaint
        )

        canvas.drawText(
            currRemainingStr,
            currRemainingBounds.left,
            currRemainingBounds.bottom,
            parPaint
        )

        val nextTitleBounds = run {
            val t = Rect()
            titlePaint.getTextBounds(next.title, 0, next.title.length, t)
            t.toRectF().apply {
                offsetTo(
                    contentBounds.centerX() + (calendarIcon.width - t.width()) / 2f,
                    maxOf(currRemainingBounds.bottom + 12f, bounds.exactCenterY() - t.height())
                )
            }
        }
        canvas.drawText(next.title, nextTitleBounds.left, nextTitleBounds.bottom, titlePaint)
        canvas.drawBitmap(
            calendarIcon,
            nextTitleBounds.left - calendarIcon.width,
            nextTitleBounds.top + (calendarIcon.height - nextTitleBounds.height()) / 2f,
            titlePaint
        )

        val nextLocationBounds = next.location.toBottom(of = nextTitleBounds.apply { left -= calendarIcon.width * 0.618f })
        canvas.drawText(
            next.location,
            nextLocationBounds.left,
            nextLocationBounds.bottom,
            bodyPaint
        )

        val nextTimeString = smartTimeString(next)
        val nextTimeBounds = nextTimeString.toBottom(of = nextLocationBounds, margin = 12f)
        canvas.drawText(nextTimeString, nextTimeBounds.left, nextTimeBounds.bottom, bodyPaint)

        super.render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }

    private fun String.toBottom(of: RectF, margin: Float = 20f, paint: Paint = bodyPaint): RectF {
        val t = Rect()
        bodyPaint.getTextBounds(this, 0, next.location.length, t)
        return t.toRectF().apply {
            offsetTo(
                of.left,
                of.bottom + margin
            )
        }
    }
}
