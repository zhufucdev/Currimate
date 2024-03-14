package com.zhufucdev.currimate.render

import android.content.Context
import androidx.annotation.Px
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap

fun Context.fromDrawable(id: Int, tint: Color, @Px width: Int? = null, @Px height: Int? = null) =
    getDrawable(id)!!
        .apply { setTint(tint.toArgb()) }.let {
            it.toBitmap(
                width = width ?: it.intrinsicWidth,
                height = height ?: it.intrinsicHeight
            )
        }

