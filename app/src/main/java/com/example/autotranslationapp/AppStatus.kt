package com.example.autotranslationapp

import kotlinx.coroutines.flow.MutableStateFlow

object AppStatus {
    val isWindowEnable= MutableStateFlow(false)
}