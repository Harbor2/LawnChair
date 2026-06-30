package com.example

import android.app.Application

object NegativeContext {
    lateinit var context: Application
        private set

    fun init(app: Application) {
        if (!::context.isInitialized) {
            context = app
        }
    }
}
