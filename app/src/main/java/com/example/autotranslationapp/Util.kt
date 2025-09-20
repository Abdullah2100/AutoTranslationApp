package com.example.autotranslationapp

import android.os.Build

object Util {
    fun isRequireNotificationPermission(): Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}