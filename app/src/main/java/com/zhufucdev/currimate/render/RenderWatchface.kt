package com.zhufucdev.currimate.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.style.CurrentUserStyleRepository
import com.zhufucdev.currimate.watchface.UserStyleHolder
import com.zhufucdev.currimate.watchface.WatchFaceCanvasRenderer
import java.time.ZonedDateTime

class RenderWatchface(
    context: Context,
    styleHolder: UserStyleHolder
) : Renderable(context, styleHolder) {
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
            renderParameters,
            styleHolder.colors
        )
    }
}
