package com.example.autotranslationapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.startForegroundService
import com.example.autotranslationapp.ui.theme.AutoTranslationAppTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AutoTranslationAppTheme {
                AppScreen()
            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun AppScreen() {
    val context = LocalContext.current

    val windowFlowStatus = AppStatus.isWindowEnable.collectAsState()

    val isRequireNotificationPermission =
        remember { mutableStateOf(Util.isRequireNotificationPermission()) }


    fun closeServices(context: Context, intent: Intent) {
        context.stopService(intent)
    }

    fun startServices(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }


    fun serviceUpdateState(context: Context) {

        val intent = Intent(context, ScreenCaptureService::class.java)

        when (windowFlowStatus.value) {
            true -> {
                closeServices(context, intent)
            }

            false -> {
                startServices(context, intent)
            }
        }
    }


    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    )
    {

        if (!Settings.canDrawOverlays(context)) {
            Toast.makeText(context, "Overlay permission is required.", Toast.LENGTH_LONG).show()
        } else {

            Toast.makeText(context, "Overlay permission granted.", Toast.LENGTH_SHORT).show()
        }

    }


    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${context.packageName}".toUri()
                )
                overlayPermissionLauncher.launch(intent)
            } else {
                serviceUpdateState(context)
            }

        } else {
            Toast.makeText(
                context,
                "Notification permission is recommended for the service.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        innerPadding.calculateTopPadding() // Consume padding - good practice
        innerPadding.calculateBottomPadding()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Overlay permission is granted or not needed (older Android versions)
            Button(
                onClick = {
                    if (isRequireNotificationPermission.value) {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = if (windowFlowStatus.value) Color.Green
                    else Color.Gray
                )
            ) {
                Text("Start Translation Service")
            }
        }
    }
}




