package com.example.android.navigationadvancedsample.perf

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.Choreographer
import android.view.FrameMetrics
import curtains.phoneWindow
import kotlin.LazyThreadSafetyMode.NONE

object ActionTracker {

  private val frameMetricsHandler by lazy(NONE) {
    val thread = HandlerThread("frame_metrics")
    thread.start()
    Handler(thread.looper)
  }

  private var actionInFlight: Boolean = false

  fun reportTapAction(
    actionName: String,
    viewUpdateRunner: ViewUpdateRunner
  ) {
    if (Build.VERSION.SDK_INT < 26) {
      return
    }
    val currentTap = TapTracker.currentTap
    if (!actionInFlight && currentTap != null) {
      actionInFlight = true
      val actionRecorded = currentTap.copy(actionName = actionName)
      viewUpdateRunner.runOnViewsUpdated { view ->
        Choreographer.getInstance().postFrameCallback { frameTimeNanos ->
          actionInFlight = false
          val callback: (FrameMetrics) -> Unit = { frameMetrics ->
            val tapResponseTime = actionRecorded.build(frameMetrics)
            PerfAnalytics.log(tapResponseTime)
          }
          val frameMetricsListener = CurrentFrameMetricsListener(frameTimeNanos, callback)
          val window = view.phoneWindow!!
          window.addOnFrameMetricsAvailableListener(
            frameMetricsListener,
            frameMetricsHandler
          )
        }
      }
    }
  }
}