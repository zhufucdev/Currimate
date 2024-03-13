package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.withRotation
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.theme.HOUR_HAND_ROUNDNESS
import com.zhufucdev.currimate.theme.HOUR_HAND_WIDTH
import com.zhufucdev.currimate.theme.HourHandPaint
import com.zhufucdev.currimate.theme.MINUTE_HAND_ROUNDNESS
import com.zhufucdev.currimate.theme.MINUTE_HAND_WIDTH
import com.zhufucdev.currimate.theme.MinuteHandPaint
import com.zhufucdev.currimate.theme.SecondHandPaint
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

fun drawClock(canvas: Canvas, bounds: Rect, clockCenter: PointF, zonedDateTime: ZonedDateTime, renderParameters: RenderParameters) {
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
                Paint(HourHandPaint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = HOUR_HAND_WIDTH * 0.1f
                }
            } else {
                HourHandPaint
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
            MinuteHandPaint
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
                SecondHandPaint
            )
        }
    }
}
