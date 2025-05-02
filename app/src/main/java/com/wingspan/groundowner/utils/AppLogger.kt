package com.wingspan.groundowner.utils

import android.util.Log
import com.wingspan.groundowner.BuildConfig
object AppLogger {
    private val IS_DEBUG = BuildConfig.DEBUG

    fun d(tag: String, message: String) {
        if (IS_DEBUG) Log.d(tag, message)
    }

    fun e(tag: String, message: String) {
        if (IS_DEBUG) Log.e(tag, message)
    }
}