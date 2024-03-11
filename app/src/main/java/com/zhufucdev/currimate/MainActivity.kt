package com.zhufucdev.currimate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.zhufucdev.currimate.theme.WearAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            installSplashScreen()

            WearApp()

            setTheme(android.R.style.Theme_DeviceDefault)
        }
    }
}

@Composable
fun WearApp() {
    WearAppTheme {
        Scaffold(
            timeText = { ResponsiveTimeText() },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.title2,
                )
            }
        }
    }
}
