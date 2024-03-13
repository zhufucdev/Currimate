package com.zhufucdev.currimate.theme

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val ParPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.White.toArgb()
    textSize = 18f
    typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
}
val BodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.White.toArgb()
    textSize = 24f
}
val LargeTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.White.toArgb()
    textSize = 48f
    typeface = Typeface.DEFAULT_BOLD
}

val TextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.White.toArgb()
    textSize = 32f
}

val TimePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.White.toArgb()
    textSize = 32f
    typeface = Typeface.DEFAULT_BOLD
}

val TitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.White.toArgb()
    textSize = 42f
    typeface = Typeface.DEFAULT_BOLD
}

