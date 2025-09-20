package com.example.autotranslationapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

object NotificationOption {

    private const val CHANNAL_ID = "translation_screen";
    fun createNotification(context: Context): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        return NotificationCompat.Builder(
            context, CHANNAL_ID
        )
            .setContentTitle("Translation Application")
            .build()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNAL_ID,
                "Screen Recording Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationMannager = context.getSystemService<NotificationManager>()
            notificationMannager?.createNotificationChannel(serviceChannel)
        }
    }

    fun clearNotification(context: Context) {

        val notificationMannager = context.getSystemService<NotificationManager>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationMannager?.deleteNotificationChannel(CHANNAL_ID)
        else
            notificationMannager?.cancelAll()

    }
}