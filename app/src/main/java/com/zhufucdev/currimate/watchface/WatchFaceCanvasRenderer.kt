package com.zhufucdev.currimate.watchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.view.SurfaceHolder
import androidx.annotation.Px
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.CalendarEvents
import com.zhufucdev.currimate.beginInstant
import com.zhufucdev.currimate.contains
import com.zhufucdev.currimate.render.RenderCurrentAndNext
import com.zhufucdev.currimate.render.RenderSoloOngoing
import com.zhufucdev.currimate.render.RenderWatchface
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.math.sqrt

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
    class CurrimateSharedAssets(val context: Context) : SharedAssets {
        fun fromDrawable(id: Int, tint: Color, @Px width: Int? = null, @Px height: Int? = null) =
            context.getDrawable(id)!!
                .apply { setTint(tint.toArgb()) }.let {
                    it.toBitmap(
                        width = width ?: it.intrinsicWidth,
                        height = height ?: it.intrinsicHeight
                    )
                }

        private val delegatedEvents = CalendarEvents(context.contentResolver)
        val events: List<CalendarEvent>

        val renderWatchface = RenderWatchface(this)

        private var mCurrNext: Triple<CalendarEvent, CalendarEvent, RenderCurrentAndNext>? = null
        fun renderCurrentAndNext(current: CalendarEvent, next: CalendarEvent) =
            mCurrNext?.takeIf { it.first == current && it.second == next }?.third
                ?: RenderCurrentAndNext(this, current, next)
                    .also { mCurrNext = Triple(current, next, it) }

        private var mSolo: Pair<CalendarEvent, RenderSoloOngoing>? = null
        fun renderSoloOngoing(event: CalendarEvent) =
            mSolo?.takeIf { it.first == event }?.second
                ?: RenderSoloOngoing(this, event)
                    .also { mSolo = event to it }

        init {
//            val startMills = Calendar.getInstance().timeInMillis
//            val endMills = Calendar.getInstance().apply { add(Calendar.DATE, 1) }.timeInMillis
//            events = delegatedEvents[startMills..endMills]
            events = listOf(
                CalendarEvent(
                    0,
                    "算法设计与分析",
                    System.currentTimeMillis() - 30 * 60 * 1000,
                    System.currentTimeMillis() + 30 * 60 * 1000,
                    "文德N614"
                ),
                CalendarEvent(
                    1,
                    "计算机网络Ⅰ",
                    System.currentTimeMillis() + 60 * 60 * 1000,
                    System.currentTimeMillis() + 120 * 60 * 1000,
                    "文德N615"
                )
            )
        }

        override fun onDestroy() {
        }
    }

    override suspend fun createSharedAssets(): CurrimateSharedAssets =
        CurrimateSharedAssets(context)

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


        val contentBounds = run {
            val width = bounds.width() / 4f * sqrt(2f)
            val height = bounds.height() / 4f * sqrt(2f)
            RectF(
                bounds.centerX() - width,
                bounds.centerY() - height,
                bounds.centerX() + width,
                bounds.centerY() + height,
            )
        }

        if (sharedAssets.events.isEmpty()) {
            sharedAssets.renderWatchface
        } else {
            val currentEvent = sharedAssets.events.first()
            if (zonedDateTime in currentEvent) {
                if (sharedAssets.events.size < 2
                    || Duration.between(
                        zonedDateTime.toInstant(),
                        sharedAssets.events[1].beginInstant
                    ) > Duration.ofHours(1)
                ) {
                    sharedAssets.renderSoloOngoing(currentEvent)
                } else {
                    sharedAssets.renderCurrentAndNext(currentEvent, sharedAssets.events[1])
                }
            } else {
                sharedAssets.renderWatchface
            }
        }.render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }
}
