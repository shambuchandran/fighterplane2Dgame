package org.example.twodgamecmp.game.util

import android.app.Application
import org.example.twodgamecmp.game.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin{
            androidContext(this@MyApplication)
        }
    }
}