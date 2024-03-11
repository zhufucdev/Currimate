package com.zhufucdev.currimate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.text.MeasuredText
import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI

private const val FRAME_PERIOD_EXPECTED = 16L

class WatchFaceCanvasRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    watchState: WatchState,
    currentUserStyleRepository: CurrentUserStyleRepository,
    canvasType: Int
) : Renderer.CanvasRenderer2<WatchFaceCanvasRenderer.CurrimateSharedAssets>(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    canvasType,
    FRAME_PERIOD_EXPECTED,
    false
) {
    class CurrimateSharedAssets : SharedAssets {
        override fun onDestroy() {
        }
    }

    override suspend fun createSharedAssets(): CurrimateSharedAssets = CurrimateSharedAssets()

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: CurrimateSharedAssets
    ) {

    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: CurrimateSharedAssets
    ) {
        canvas.drawRect(bounds, Paint())
        val fillWhite =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.White.toArgb()
                textSize = 26f
            }
        val strokeRed =
            Paint().apply {
                color = Color.Red.toArgb()
                strokeWidth = 2f
            }
        val radius = minOf(bounds.width(), bounds.height()) * 0.8f / 2
        val drawingBounds = RectF(
            bounds.width() / 2f - radius,
            bounds.height() / 2f - radius,
            bounds.width() / 2f + radius,
            bounds.height() / 2f + radius
        )
        val timeString = zonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
        val measuredTimeText = MeasuredText.Builder(timeString.toCharArray())
            .appendStyleRun(fillWhite, timeString.length, false)
            .build()
        val angleOffset = (measuredTimeText.getWidth(0, timeString.length) / (2 * PI * radius) * 180).toFloat()
        val timeTextPath = Path().apply {
            addArc(drawingBounds, -90f - angleOffset, angleOffset * 2)
        }
        canvas.drawTextOnPath(
            timeString,
            timeTextPath,
            0f,
            0f,
            fillWhite
        )
    }
}
