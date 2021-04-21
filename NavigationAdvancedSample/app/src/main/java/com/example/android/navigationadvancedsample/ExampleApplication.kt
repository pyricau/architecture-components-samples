package com.example.android.navigationadvancedsample

import android.app.Application
import com.example.android.navigationadvancedsample.perf.OnTxCommitFragmentViewUpdateRunner
import com.example.android.navigationadvancedsample.perf.ActionTracker
import com.example.android.navigationadvancedsample.perf.TapTracker
import com.example.android.navigationadvancedsample.util.GlobalNavHostDestinationChangedListener
import curtains.Curtains
import curtains.OnRootViewAddedListener
import curtains.keyEventInterceptors
import curtains.phoneWindow
import curtains.touchEventInterceptors
import curtains.windowAttachCount

class ExampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Curtains.onRootViewsChangedListeners += OnRootViewAddedListener { view ->
      view.phoneWindow?.let { window ->
        if (view.windowAttachCount == 0) {
          window.touchEventInterceptors += TapTracker
          window.keyEventInterceptors += TapTracker
        }
      }
    }

    GlobalNavHostDestinationChangedListener.install(this) { fragment, destination, _ ->
      ActionTracker.reportTapAction(
        actionName = destination.label.toString(),
        viewUpdateRunner = OnTxCommitFragmentViewUpdateRunner(fragment)
      )
    }
  }
}