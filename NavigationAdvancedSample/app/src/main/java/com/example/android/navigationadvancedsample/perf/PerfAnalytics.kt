package com.example.android.navigationadvancedsample.perf

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

object PerfAnalytics {

  private val analyticsHandler by lazy {
    val thread = HandlerThread("analytics")
    thread.start()
    Handler(thread.looper)
  }

  fun log(tapResponseTime: TapResponseTime) {
    analyticsHandler.post {
      Log.d("PerfAnalytics", "$tapResponseTime")
    }
  }
}

