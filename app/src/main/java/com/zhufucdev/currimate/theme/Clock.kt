package com.zhufucdev.currimate.theme

import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

const val HOUR_HAND_WIDTH = 24f
const val HOUR_HAND_ROUNDNESS = 16f
const val MINUTE_HAND_WIDTH = 12f
const val MINUTE_HAND_ROUNDNESS = 8f

val HourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color(204, 110, 69, 255).toArgb()
}
val MinuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color(181, 181, 181, 255).toArgb()
}
val SecondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color(107, 53, 30, 255).toArgb()
    strokeWidth = 4f
}
