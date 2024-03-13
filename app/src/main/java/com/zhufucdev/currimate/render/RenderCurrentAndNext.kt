package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toRectF
import androidx.core.graphics.withRotation
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
import java.time.temporal.ChronoField
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

private const val HOUR_HAND_WIDTH = 24f
private const val HOUR_HAND_ROUNDNESS = 16f
private const val MINUTE_HAND_WIDTH = 12f
private const val MINUTE_HAND_ROUNDNESS = 8f

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
    private val largeTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.White.toArgb()
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
    }
    private val hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color(204, 110, 69, 255).toArgb()
    }
    private val minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LightGray.toArgb()
    }
    private val secondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color(107, 53, 30, 255).toArgb()
        strokeWidth = 4f
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
        val largeTitlePaint =
            if (renderParameters.drawMode == DrawMode.AMBIENT) sharedAssets.textPaint else largeTitlePaint

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
        val nextTitleBounds = run {
            val t = Rect()
            largeTitlePaint.getTextBounds(next.title, 0, next.title.length, t)
            t.toRectF().apply {
                offsetTo(
                    contentBounds.centerX() + (calendarIcon.width - t.width()) / 2f,
                    maxOf(currRemainingBounds.bottom + 12f, bounds.exactCenterY() - t.height())
                )
            }
        }
        val nextLocationBounds =
            next.location.toBottom(of = RectF(nextTitleBounds).apply { left -= calendarIcon.width * 0.618f })
        val nextTimeString = smartTimeString(next)
        val nextTimeBounds = nextTimeString.toBottom(of = nextLocationBounds, margin = 12f)

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

        val clockCenter = PointF(bounds.exactCenterX(), nextTimeBounds.centerY())
        val hourHandLength = 0.5f * (HOUR_HAND_WIDTH / 2 + bounds.bottom - clockCenter.y)
        val hourHandRotation =
            ((zonedDateTime.hour % 12) / 12f + zonedDateTime.minute / 3600f) * 360
        val hourHandOutline = RectF(
            clockCenter.x - HOUR_HAND_WIDTH / 2,
            clockCenter.y - HOUR_HAND_WIDTH / 2 - hourHandLength,
            clockCenter.x + HOUR_HAND_WIDTH / 2,
            clockCenter.y + HOUR_HAND_WIDTH / 2,
        )
        canvas.withRotation(hourHandRotation, clockCenter.x, clockCenter.y) {
            canvas.drawRoundRect(
                hourHandOutline,
                HOUR_HAND_ROUNDNESS,
                HOUR_HAND_ROUNDNESS,
                if (renderParameters.drawMode == DrawMode.AMBIENT) {
                    Paint(hourHandPaint).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = HOUR_HAND_WIDTH * 0.1f
                    }
                } else {
                    hourHandPaint
                }
            )
        }

        val minuteHandRotation = (zonedDateTime.minute / 60f + zonedDateTime.second / 3600f) * 360
        val minuteHandLength = hourHandLength * 1.8f
        canvas.withRotation(minuteHandRotation, clockCenter.x, clockCenter.y) {
            canvas.drawRoundRect(
                clockCenter.x - MINUTE_HAND_WIDTH / 2,
                clockCenter.y - MINUTE_HAND_WIDTH / 2 - minuteHandLength,
                clockCenter.x + MINUTE_HAND_WIDTH / 2,
                clockCenter.y + MINUTE_HAND_WIDTH / 2,
                MINUTE_HAND_ROUNDNESS,
                MINUTE_HAND_ROUNDNESS,
                minuteHandPaint
            )
        }

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val secondHandRotation =
                (zonedDateTime.second / 60f + zonedDateTime[ChronoField.MILLI_OF_SECOND] / 1000f / 60f) * 360
            val secondHandLength = run {
                val h = bounds.centerY() - clockCenter.y
                val t = cos(secondHandRotation / 180 * PI).toFloat()
                val r = bounds.width() / 2f
                (h * t - sqrt(h * h * t * t - h * h + r * r)) * 0.92f
            }
            canvas.withRotation(secondHandRotation, clockCenter.x, clockCenter.y) {
                canvas.drawLine(
                    clockCenter.x,
                    clockCenter.y,
                    clockCenter.x,
                    clockCenter.y + secondHandLength,
                    secondHandPaint
                )
            }
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

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            canvas.drawText(
                next.title,
                nextTitleBounds.left,
                nextTitleBounds.bottom,
                largeTitlePaint
            )
            canvas.drawBitmap(
                calendarIcon,
                nextTitleBounds.left - calendarIcon.width,
                nextTitleBounds.top + 8f,
                largeTitlePaint
            )
        } else {
            canvas.drawText(
                next.title,
                nextTitleBounds.left - calendarIcon.width / 2f,
                nextTitleBounds.bottom,
                largeTitlePaint
            )
        }
        canvas.drawText(
            next.location,
            nextLocationBounds.left,
            nextLocationBounds.bottom,
            bodyPaint
        )
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
