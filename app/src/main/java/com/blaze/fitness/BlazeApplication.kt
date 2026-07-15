package com.blaze.fitness

import android.app.Application
import com.blaze.fitness.di.AppContainer

class BlazeApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
