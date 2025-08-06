package com.menu

import android.app.Application
import com.menu.data.AppContainer
import com.menu.data.DefaultAppContainer

class LunchMenuApplication: Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}