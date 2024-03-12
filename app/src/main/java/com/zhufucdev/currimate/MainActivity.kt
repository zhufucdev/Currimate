package com.zhufucdev.currimate

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import com.zhufucdev.currimate.theme.WearAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            installSplashScreen()

            WearAppTheme {
                WearApp()
            }

            setTheme(android.R.style.Theme_DeviceDefault)
        }
    }
}

@Composable
fun WearApp() {
    var calendarPermissionGranted by remember { mutableStateOf<Boolean?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        calendarPermissionGranted = it
    }

    LaunchedEffect(true) {
        launcher.launch(Manifest.permission.READ_CALENDAR)
    }

    Scaffold(
        timeText = { ResponsiveTimeText() },
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.title2,
                )
                Text(
                    text = stringResource(
                        id =
                        if (calendarPermissionGranted == true) R.string.span_permission_granted
                        else R.string.span_permission_denied
                    )
                )
                if (calendarPermissionGranted == false) {
                    OutlinedButton(
                        onClick = { launcher.launch(Manifest.permission.READ_CALENDAR) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.par_grant),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}
