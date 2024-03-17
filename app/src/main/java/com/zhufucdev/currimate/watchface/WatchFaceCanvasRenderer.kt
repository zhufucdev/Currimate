package com.zhufucdev.currimate.watchface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.Log
import android.view.SurfaceHolder
import androidx.annotation.Px
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.CalendarEvents
import com.zhufucdev.currimate.beginInstant
import com.zhufucdev.currimate.contains
import com.zhufucdev.currimate.render.RenderCurrentAndNext
import com.zhufucdev.currimate.render.RenderSoloOngoing
import com.zhufucdev.currimate.render.RenderWatchface
import com.zhufucdev.currimate.render.RenderableFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
    class CurrimateSharedAssets(context: Context) : SharedAssets {
        private val delegatedEvents = CalendarEvents(context.contentResolver)
        var events: List<CalendarEvent>
            private set
        private val eventsFetcher: Timer

        private fun fetchEvents(): List<CalendarEvent> {
            val startMills = Calendar.getInstance().timeInMillis
            val endMills = Calendar.getInstance().apply { add(Calendar.DATE, 1) }.timeInMillis
            return delegatedEvents[startMills..endMills]
        }

        init {
            events = fetchEvents()

            val interval = Duration.ofMinutes(1).toMillis()
            eventsFetcher = timer(daemon = true, initialDelay = interval, period = interval) {
                events = fetchEvents()
            }
        }

        override fun onDestroy() {
            eventsFetcher.cancel()
        }
    }

    override suspend fun createSharedAssets(): CurrimateSharedAssets =
        CurrimateSharedAssets(context)

    val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val styleHolder: UserStyleHolder = createUserStyleHolder(currentUserStyleRepository)
    private val renderableFactory = RenderableFactory(styleHolder, context)

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: CurrimateSharedAssets
    ) {
        // noop
    }

    private val testEvents =
        listOf(
            CalendarEvent(
                1,
                "计算机组成与体系结构明德楼实验",
                System.currentTimeMillis(),
                System.currentTimeMillis() + 10 * 60 * 1000L,
                "明德N301"
            ),
            CalendarEvent(
                1,
                "计算机组成与体系结构",
                System.currentTimeMillis() + 10 * 60 * 1000L,
                System.currentTimeMillis() + 10 * 60 * 1000L,
                "明德N301"
            )
        )

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

        renderableFactory.getRenderer(zonedDateTime, testEvents)
            .render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }

    override fun onDestroy() {
        scope.cancel("WatchCanvasRenderer scope clear() request")
        renderableFactory.onDestroy()
        super.onDestroy()
    }
}
