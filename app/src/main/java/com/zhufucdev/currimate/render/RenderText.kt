package com.zhufucdev.currimate.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import com.zhufucdev.currimate.watchface.UserStyleHolder
import java.time.ZonedDateTime
import kotlin.math.roundToInt

private const val VELOCITY = 50f // pixel per second
private const val MARGIN = 100

class RenderText(
    val text: String,
    var paint: Paint,
    context: Context,
    styleHolder: UserStyleHolder,
    private val framerate: Int = 60
) :
    Renderable(context, styleHolder) {
    private var frame = 0
    private val textBounds get() = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
    private val frames get() = ((textBounds.width() + MARGIN) / VELOCITY * framerate).roundToInt()

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        contentBounds: RectF,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters
    ) {
        if (bounds.width() >= textBounds.width()) {
            canvas.drawText(
                text,
                bounds.exactCenterX() - textBounds.width() / 2f,
                bounds.exactCenterY() + textBounds.height() / 2f,
                paint
            )
        } else if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val leftMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    bounds.left.toFloat(),
                    0f,
                    bounds.exactCenterX(),
                    0f,
                    intArrayOf(
                        Color.Black.toArgb(),
                        Color.Transparent.toArgb(),
                    ),
                    floatArrayOf(0f, 40f / bounds.width()),
                    Shader.TileMode.MIRROR
                )
                xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
            }

            val frames = frames
            frame = (frame + 1) % frames
            val offset = -frame * 1f / frames * (textBounds.width() + MARGIN)
            val y = contentBounds.centerY() + textBounds.height() / 2f
            canvas.drawText(text, offset + bounds.left, y, paint)
            canvas.drawText(text, offset + MARGIN + textBounds.width(), y, paint)
            canvas.drawRect(bounds, leftMaskPaint)
        } else {
            var remaining = bounds.width() * text.length / textBounds.width()
            var truncatedText = text.substring(0 until remaining) + "..."
            var width = paint.measureText(truncatedText)
            while (width >= bounds.width()) {
                remaining -= 1
                truncatedText = text.substring(0 until remaining) + "..."
                width = paint.measureText(truncatedText)
            }
            canvas.drawText(
                truncatedText,
                bounds.exactCenterX() - width / 2f,
                contentBounds.centerY() + textBounds.height() / 2f,
                paint
            )
        }
    }
}
