package com.zhufucdev.currimate.watchface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.drawable.Icon
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import com.zhufucdev.currimate.R

const val ID_COLOR_SETTINGS = "color_style"

enum class UserStyleColors(
    val id: String,
    @StringRes val displayNameResourceId: Int,
    val hourHandColor: Int,
    val minuteHandColor: Int,
    val secondHandColor: Int
) {
    SALMON(
        "salmon", R.string.title_salmon,
        Color(204, 110, 69).toArgb(),
        Color(181, 181, 181).toArgb(),
        Color(107, 53, 30).toArgb()
    ),
    RASPBERRY(
        "raspberry", R.string.title_raspberry,
        Color(244, 69, 96).toArgb(),
        Color(68, 209, 223).toArgb(),
        Color(115, 5, 24).toArgb()
    ),
    MAGENTA(
        "magenta", R.string.title_magenta,
        Color(255, 0, 255).toArgb(),
        Color(102, 255, 255).toArgb(),
        Color(128, 0, 64).toArgb(),
    ),
    STRAWBERRY(
        "strawberry", R.string.title_strawberry,
        Color(255, 0, 128).toArgb(),
        Color(204, 204, 204).toArgb(),
        Color(255, 111, 207).toArgb()
    ),
    TANGERINE(
        "tangerine", R.string.title_tangerine,
        Color(255, 128, 0).toArgb(),
        Color(204, 204, 204).toArgb(),
        Color(0, 0, 128).toArgb()
    ),
    AQUA(
        "aqua", R.string.title_aqua,
        Color(0, 128, 255).toArgb(),
        Color(204, 204, 204).toArgb(),
        Color(128, 0, 0).toArgb()
    ),
    BLUEBERRY(
        "blueberry", R.string.title_blueberry,
        Color(0, 0, 255).toArgb(),
        Color(255, 102, 102).toArgb(),
        Color(64, 0, 128).toArgb()
    ),
    LIME(
        "lime", R.string.title_lime,
        Color(0, 255, 128).toArgb(),
        Color(204, 255, 102).toArgb(),
        Color(128, 128, 0).toArgb()
    ),
    SEA_FOAM(
        "sea_foam", R.string.title_sea_foam,
        Color(0, 255, 0).toArgb(),
        Color(0, 128, 128).toArgb(),
        Color(128, 0, 64).toArgb()
    );

    companion object {
        fun generateOptionList(context: Context) = entries.map {
            UserStyleSetting.ListUserStyleSetting.ListOption(
                UserStyleSetting.Option.Id(it.id),
                displayNameResourceId = it.displayNameResourceId,
                resources = context.resources,
                screenReaderNameResourceId = it.displayNameResourceId,
                icon = Icon.createWithBitmap(Bitmap.createBitmap(Picture().apply {
                    val canvas = beginRecording(64, 64)
                    canvas.drawRect(
                        0f,
                        0f,
                        width.toFloat(),
                        height.toFloat(),
                        Paint().apply { color = it.hourHandColor })
                    endRecording()
                }))
            )
        }

        fun fromOptionId(id: String) = entries.firstOrNull { it.id == id }
    }
}

fun createUserScheme(context: Context) = UserStyleSchema(
    listOf<UserStyleSetting>(
        UserStyleSetting.ListUserStyleSetting(
            id = UserStyleSetting.Id(ID_COLOR_SETTINGS),
            resources = context.resources,
            displayNameResourceId = R.string.title_color,
            descriptionResourceId = R.string.par_des_color,
            icon = null,
            options = UserStyleColors.generateOptionList(context),
            affectsWatchFaceLayers = listOf(WatchFaceLayer.BASE)
        )
    )
)
