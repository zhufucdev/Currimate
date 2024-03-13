package com.zhufucdev.currimate.render

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.style.CurrentUserStyleRepository
import java.time.ZonedDateTime

abstract class Renderable<T : Renderer.SharedAssets>(
    val sharedAssets: T,
    val currentUserStyleRepository: CurrentUserStyleRepository
) {
    abstract fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    )

    open fun onDestroy() {}
}
