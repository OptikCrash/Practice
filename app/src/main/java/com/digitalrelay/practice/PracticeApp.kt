package com.digitalrelay.practice

import android.app.Application
import android.content.Context

class PracticeApp : Application() {
    private object InstanceHolder {
        lateinit var INSTANCE : PracticeApp
    }

    init {
        InstanceHolder.INSTANCE = this@PracticeApp
    }
    companion object {
        private val instance: PracticeApp by lazy { InstanceHolder.INSTANCE }
        val applicationContext: Context
            get() { return instance.applicationContext }
    }

}