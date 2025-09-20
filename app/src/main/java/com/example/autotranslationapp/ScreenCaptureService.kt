package com.example.autotranslationapp

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ScreenCaptureService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onBind(intent: Intent?): IBinder? = null

    var dispatcher = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onCreate()
        createNotification()
        generateWindowFlow()
        dispatcher.launch {
            AppStatus.isWindowEnable.emit(true)
        }
        return START_STICKY
    }

    private fun createNotification() {
        val notification =
            NotificationOption.createNotification(applicationContext) // Build your notification here
        startForeground(1, notification)
    }

    private fun generateWindowFlow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

        val closeButton = floatingView.findViewById<ImageButton>(R.id.close)

        closeButton.setOnClickListener {
            closeWindow()
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )


        // Starting position of the floating window
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.x = 20
        params.y = 100

        windowManager.addView(floatingView, params)
    }


    private fun closeWindow() {

        //finish services
        val serviceIntent = Intent(this, ScreenCaptureService::class.java)
        stopService(serviceIntent)
        //clear notification
        NotificationOption.clearNotification(applicationContext)

        //clear window
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }

        //change service status
        dispatcher.launch {
            AppStatus.isWindowEnable.emit(false)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        closeWindow()
    }

}