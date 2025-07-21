package io.github.samolego.kelnar

import android.content.Context

object AppContext {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun get(): Context {
        return context
    }
}
