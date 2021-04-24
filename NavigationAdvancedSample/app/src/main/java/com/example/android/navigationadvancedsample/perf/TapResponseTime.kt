package com.example.android.navigationadvancedsample.perf

import android.view.FrameMetrics
import android.view.FrameMetrics.ANIMATION_DURATION
import android.view.FrameMetrics.COMMAND_ISSUE_DURATION
import android.view.FrameMetrics.DRAW_DURATION
import android.view.FrameMetrics.INPUT_HANDLING_DURATION
import android.view.FrameMetrics.INTENDED_VSYNC_TIMESTAMP
import android.view.FrameMetrics.LAYOUT_MEASURE_DURATION
import android.view.FrameMetrics.SWAP_BUFFERS_DURATION
import android.view.FrameMetrics.SYNC_DURATION
import android.view.FrameMetrics.TOTAL_DURATION
import android.view.FrameMetrics.VSYNC_TIMESTAMP
import androidx.annotation.RequiresApi

data class TapResponseTime(
  val actionName: String,
  val totalMillis: Long,
  /**
   * Duration from touch being sent by the display to received by the app.
   */
  val touchDispatchMillis: Long,
  /**
   * Duration from touch received by the app to triggering the action (e.g. running a posted click)
   */
  val actionDispatchMillis: Long,
  /**
   * Duration from triggering the action (e.g. running a posted click) to starting the frame
   * rendering.
   */
  val frameDispatchMillis: Long,

  /**
   * @see [FrameMetrics.TOTAL_DURATION]
   */
  val frameTotalMillis: Long,

  /**
   * @see [FrameMetrics.INPUT_HANDLING_DURATION]
   */
  val frameInputHandlingMillis: Long,

  /**
   * @see [FrameMetrics.ANIMATION_DURATION]
   */
  val frameAnimationMillis: Long,

  /**
   * @see [FrameMetrics.LAYOUT_MEASURE_DURATION]
   */
  val frameLayoutMeasureMillis: Long,

  /**
   * @see [FrameMetrics.DRAW_DURATION]
   */
  val frameDrawMillis: Long,

  /**
   * @see [FrameMetrics.SYNC_DURATION]
   */
  val frameSyncMillis: Long,

  /**
   * @see [FrameMetrics.COMMAND_ISSUE_DURATION]
   */
  val frameCommandIssueMillis: Long,

  /**
   * @see [FrameMetrics.SWAP_BUFFERS_DURATION]
   */
  val frameSwapBuffersMillis: Long,
) {

  data class Builder(
    val tapUptimeMillis: Long,
    val dispatchedUptimeMillis: Long,
    val triggeringActionUptimeMillis: Long? = null,
    val actionName: String? = null,
  ) {
    @RequiresApi(26)
    fun build(frameMetrics: FrameMetrics): TapResponseTime {
      val vsyncUptimeMillis = frameMetrics.getMetric(VSYNC_TIMESTAMP) / 1_000_000
      val intendedVsyncUptimeMillis = frameMetrics.getMetric(INTENDED_VSYNC_TIMESTAMP) / 1_000_000
      val totalMillis = frameMetrics.getMetric(TOTAL_DURATION) / 1_000_000
      // Note: TOTAL_DURATION is the duration from the intended vsync time, not the actual vsync
      // time. The difference between INTENDED_VSYNC_TIMESTAMP and VSYNC_TIMESTAMP is
      // UNKNOWN_DELAY_DURATION.
      val bufferSwapUptimeMillis = intendedVsyncUptimeMillis + totalMillis
      return TapResponseTime(
        actionName = actionName!!,
        totalMillis = bufferSwapUptimeMillis - tapUptimeMillis,
        touchDispatchMillis = dispatchedUptimeMillis - tapUptimeMillis,
        actionDispatchMillis = triggeringActionUptimeMillis!! - dispatchedUptimeMillis,
        frameDispatchMillis = vsyncUptimeMillis - triggeringActionUptimeMillis,
        frameTotalMillis = totalMillis,
        frameInputHandlingMillis = frameMetrics.getMetric(INPUT_HANDLING_DURATION) / 1_000_000,
        frameAnimationMillis = frameMetrics.getMetric(ANIMATION_DURATION) / 1_000_000,
        frameLayoutMeasureMillis = frameMetrics.getMetric(LAYOUT_MEASURE_DURATION) / 1_000_000,
        frameDrawMillis = frameMetrics.getMetric(DRAW_DURATION) / 1_000_000,
        frameSyncMillis = frameMetrics.getMetric(SYNC_DURATION) / 1_000_000,
        frameCommandIssueMillis = frameMetrics.getMetric(COMMAND_ISSUE_DURATION) / 1_000_000,
        frameSwapBuffersMillis = frameMetrics.getMetric(SWAP_BUFFERS_DURATION) / 1_000_000,
      )
    }
  }
}