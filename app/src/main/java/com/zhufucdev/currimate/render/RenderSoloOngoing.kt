package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.ZonedDateTime

class RenderSoloOngoing(
    sharedAssets: WatchFaceCanvasRenderer.CurrimateSharedAssets,
    private val event: CalendarEvent
) : RenderTimeText(sharedAssets) {
    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        super.render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }
}
