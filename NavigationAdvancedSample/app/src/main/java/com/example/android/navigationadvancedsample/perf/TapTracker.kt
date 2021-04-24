package com.example.android.navigationadvancedsample.perf

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.Trace
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.MotionEvent
import curtains.DispatchState
import curtains.KeyEventInterceptor
import curtains.TouchEventInterceptor

object TapTracker : TouchEventInterceptor, KeyEventInterceptor {

  var currentTap: TapResponseTime.Builder? = null
    private set

  private val handler = Handler(Looper.getMainLooper())

  override fun intercept(
    motionEvent: MotionEvent,
    dispatch: (MotionEvent) -> DispatchState
  ): DispatchState {
    val isActionUp = motionEvent.action == MotionEvent.ACTION_UP
    if (isActionUp) {
      val currentTap = TapResponseTime.Builder(
        tapUptimeMillis = motionEvent.eventTime,
        dispatchedUptimeMillis = SystemClock.uptimeMillis()
      )
      handler.post {
        TapTracker.currentTap = currentTap.copy(
          triggeringActionUptimeMillis = SystemClock.uptimeMillis()
        )
      }
    }
    val dispatchState = dispatch(motionEvent)
    // Android posts onClick callbacks when it receives the up event. So here we leverage
    // afterTouchEvent at which point the onClick has been posted, and by posting then we ensure
    // we're clearing the event right after the onclick is handled.
    if (isActionUp) {
      handler.post {
        currentTap = null
      }
    }
    return dispatchState
  }

  override fun intercept(
    keyEvent: KeyEvent,
    dispatch: (KeyEvent) -> DispatchState
  ): DispatchState {

    val isBackPressed = keyEvent.keyCode == KEYCODE_BACK &&
      keyEvent.action == KeyEvent.ACTION_UP &&
      !keyEvent.isCanceled

    if (isBackPressed) {
      val now = SystemClock.uptimeMillis()
      currentTap = TapResponseTime.Builder(
        tapUptimeMillis = keyEvent.eventTime,
        dispatchedUptimeMillis = now,
        triggeringActionUptimeMillis = now
      )
    }

    val dispatchState = dispatch(keyEvent)
    currentTap = null

    return dispatchState
  }
}