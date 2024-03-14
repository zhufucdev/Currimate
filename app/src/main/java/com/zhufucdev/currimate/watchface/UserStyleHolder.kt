package com.zhufucdev.currimate.watchface

import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.coroutines.launch

data class UserStyleHolder(var colors: UserStyleColors = UserStyleColors.SALMON)

fun WatchFaceCanvasRenderer.createUserStyleHolder(currentUserStyleRepository: CurrentUserStyleRepository): UserStyleHolder {
    val holder = UserStyleHolder()
    scope.launch {
        currentUserStyleRepository.userStyle.collect {
            val option =
                it[UserStyleSetting.Id(ID_COLOR_SETTINGS)] as UserStyleSetting.ListUserStyleSetting.ListOption
            holder.colors = UserStyleColors.fromOptionId(option.id.value.decodeToString()) ?: error(
                "Color ${option.displayName} not implemented"
            )
        }
    }
    return holder
}
