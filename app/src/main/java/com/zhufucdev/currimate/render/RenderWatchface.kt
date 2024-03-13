package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.theme.TextPaint
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.ZonedDateTime

class RenderWatchface(sharedAssets: WatchFaceCanvasRenderer.CurrimateSharedAssets) :
    Renderable<WatchFaceCanvasRenderer.CurrimateSharedAssets>(sharedAssets) {
    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        drawClock(
            canvas,
            bounds,
            PointF(bounds.exactCenterX(), bounds.exactCenterY()),
            zonedDateTime,
            renderParameters
        )
    }
}
