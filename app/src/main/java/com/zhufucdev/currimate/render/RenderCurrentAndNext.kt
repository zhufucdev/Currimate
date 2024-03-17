package com.zhufucdev.currimate.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RenderNode
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.CalendarEvent
import com.zhufucdev.currimate.R
import com.zhufucdev.currimate.endInstant
import com.zhufucdev.currimate.theme.LargeTitlePaint
import com.zhufucdev.currimate.theme.ParPaint
import com.zhufucdev.currimate.theme.TextPaint
import com.zhufucdev.currimate.theme.TitlePaint
import com.zhufucdev.currimate.watchface.UserStyleHolder
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

class RenderCurrentAndNext(
    context: Context,
    styleHolder: UserStyleHolder,
    private val current: CalendarEvent,
    private val next: CalendarEvent
) : RenderTimeText(context, styleHolder) {
    private val nextTitleRenderable = RenderText(next.title, TextPaint, context, styleHolder)
    private val currTitleRenderable = RenderText(current.title, TextPaint, context, styleHolder)

    private val timerStandIcon =
        context.fromDrawable(
            R.drawable.ic_timer_sand,
            Color.White.copy(alpha = 0.5f),
            120,
            120
        )
    private val calendarIcon =
        context.fromDrawable(R.drawable.ic_calendar_start_outline, Color.White)

    private fun timeRemainingString(event: CalendarEvent): String {
        val t = Duration.between(Instant.now(), event.endInstant).toMinutes().toInt()
        return context.resources
            .getQuantityString(R.plurals.par_minutes_remaining, t, t)
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        val titlePaint =
            if (renderParameters.drawMode == DrawMode.AMBIENT) TextPaint else TitlePaint
        val largeTitlePaint =
            if (renderParameters.drawMode == DrawMode.AMBIENT) TextPaint else LargeTitlePaint

        val currTitleSize = Rect()
        titlePaint.getTextBounds(current.title, 0, current.title.length, currTitleSize)
        if (currTitleSize.width() >= contentBounds.width()) {
            currTitleSize.left = contentBounds.left.toInt()
            currTitleSize.right = contentBounds.right.toInt()
        }

        val currRemainingStr = timeRemainingString(current)
        val currRemainingBounds = run {
            val t = Rect()
            ParPaint.getTextBounds(
                currRemainingStr,
                0,
                currRemainingStr.length,
                t
            )
            t.toRectF().apply {
                offsetTo(
                    (contentBounds.centerX() - currTitleSize.width() / 2f),
                    (contentBounds.top + currTitleSize.height() + 12),
                )
            }
        }
        val nextTitleBounds = run {
            val t = Rect()
            largeTitlePaint.getTextBounds(next.title, 0, next.title.length, t)
            if (t.width() >= contentBounds.width()) {
                t.left = contentBounds.left.toInt()
                t.right = contentBounds.right.toInt()
            }
            t.toRectF().apply {
                offsetTo(
                    contentBounds.centerX() + (calendarIcon.width - t.width()) / 2f,
                    maxOf(currRemainingBounds.bottom + 12f, bounds.exactCenterY() - t.height())
                )
            }
        }
        val nextLocationBounds =
            next.location.toBottom(of = RectF(nextTitleBounds).apply { left -= calendarIcon.width * 0.618f })
        val nextTimeString = smartTimeString(next)
        val nextTimeBounds = nextTimeString.toBottom(of = nextLocationBounds, margin = 12f)

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val timerStandIconBounds = RectF(
                contentBounds.centerX() - timerStandIcon.width / 2f,
                contentBounds.top -
                    (timerStandIcon.height - currTitleSize.height() - currRemainingBounds.height()) / 2f,
                contentBounds.centerX() + timerStandIcon.width / 2f,
                contentBounds.top + currTitleSize.bottom +
                    (timerStandIcon.height - currTitleSize.height() - currRemainingBounds.height()) / 2f
            )

            val iconMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                isDither = true
                shader = LinearGradient(
                    timerStandIconBounds.centerX(),
                    timerStandIconBounds.top,
                    timerStandIconBounds.centerX(),
                    timerStandIconBounds.bottom,
                    intArrayOf(
                        Color.Black.toArgb(),
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                        Color.Black.toArgb()
                    ),
                    floatArrayOf(0f, 0.4f, 0.6f, 1f),
                    Shader.TileMode.MIRROR
                )
            }
            canvas.drawBitmap(
                timerStandIcon,
                timerStandIconBounds.left,
                timerStandIconBounds.top,
                null
            )
            canvas.drawRect(timerStandIconBounds, iconMaskPaint)
        }

        val clockCenter = PointF(bounds.exactCenterX(), nextTimeBounds.centerY() + 40)
        drawClock(
            canvas,
            bounds,
            clockCenter,
            zonedDateTime,
            renderParameters,
            styleHolder.colors
        )

        run {
            currTitleRenderable.paint = titlePaint
            val node = RenderNode("current event title")
            val position = Rect(currTitleSize).apply {
                offsetTo(
                    (contentBounds.centerX() - currTitleSize.width() / 2).toInt(),
                    contentBounds.top.toInt()
                )
                bottom += 10
            }
            node.setPosition(position)
            val c = node.beginRecording()
            val mapped = Rect(currTitleSize)
            mapped.offsetTo(0, 0)
            currTitleRenderable.render(
                c,
                position.apply { offsetTo(0, 0) },
                mapped.toRectF(),
                zonedDateTime,
                renderParameters
            )
            node.endRecording()
            canvas.drawRenderNode(node)
        }

        canvas.drawText(
            currRemainingStr,
            currRemainingBounds.left,
            currRemainingBounds.bottom,
            ParPaint
        )

        drawFocusedEvent(
            next,
            nextTitleRenderable,
            canvas,
            calendarIcon,
            nextTimeString,
            nextTitleBounds,
            zonedDateTime,
            renderParameters
        )

        super.render(canvas, bounds, contentBounds, zonedDateTime, renderParameters)
    }

    override fun onDestroy() {
        calendarIcon.recycle()
        timerStandIcon.recycle()
    }
}
