package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.theme.TimePaint
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI

abstract class RenderTimeText(sharedAssets: WatchFaceCanvasRenderer.CurrimateSharedAssets) :
    Renderable<WatchFaceCanvasRenderer.CurrimateSharedAssets>(sharedAssets) {

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        val radius = minOf(bounds.width(), bounds.height()) * 0.8f / 2f
        val timeBounds = RectF(
            bounds.width() / 2f - radius,
            bounds.height() / 2f - radius,
            bounds.width() / 2f + radius,
            bounds.height() / 2f + radius
        )
        val timeString = zonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
        val timeTextWidth = TimePaint.measureText(timeString)
        val angularOffset = (timeTextWidth / (2 * PI * radius) * 180).toFloat()
        val timeTextPath = Path().apply {
            addArc(timeBounds, -90f - angularOffset, angularOffset * 2)
        }
        canvas.drawTextOnPath(
            timeString,
            timeTextPath,
            0f,
            0f,
            TimePaint
        )

    }
}
