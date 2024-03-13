package com.zhufucdev.currimate.render

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.toRectF
import com.zhufucdev.currimate.theme.BodyPaint

fun String.toBottom(of: RectF, margin: Float = 20f, paint: Paint = BodyPaint): RectF {
    val t = Rect()
    BodyPaint.getTextBounds(this, 0, length, t)
    return t.toRectF().apply {
        offsetTo(
            of.left,
            of.bottom + margin
        )
    }
}
