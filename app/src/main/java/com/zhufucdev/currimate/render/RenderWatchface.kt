package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.ZonedDateTime

class RenderWatchface(sharedAssets: WatchFaceCanvasRenderer.CurrimateSharedAssets) :
    Renderable<WatchFaceCanvasRenderer.CurrimateSharedAssets>(sharedAssets) {
    private val linePaint = Paint().apply {
        color = Color.Red.toArgb()
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        canvas.drawText(
            "Watch face placeholder",
            contentBounds.left,
            contentBounds.top,
            sharedAssets.textPaint
        )
        canvas.drawLine(
            contentBounds.left,
            contentBounds.top,
            contentBounds.right,
            contentBounds.bottom,
            linePaint
        )
        canvas.drawLine(
            contentBounds.right,
            contentBounds.top,
            contentBounds.left,
            contentBounds.bottom,
            linePaint
        )
    }
}
