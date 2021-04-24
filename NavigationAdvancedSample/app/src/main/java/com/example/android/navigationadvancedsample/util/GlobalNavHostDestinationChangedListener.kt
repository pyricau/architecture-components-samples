package com.example.android.navigationadvancedsample.util

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment

class GlobalNavHostDestinationChangedListener private constructor(
  private val listener: (fragment: NavHostFragment, destination: NavDestination, arguments: Bundle?) -> Unit
) : ActivityLifecycleCallbacks {
  override fun onActivityCreated(
    activity: Activity,
    savedInstanceState: Bundle?
  ) {
    if (activity is FragmentActivity) {
      registerFragmentCreation(activity)
    }
  }

  private fun registerFragmentCreation(activity: FragmentActivity) {
    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
      object : FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(
          fm: FragmentManager,
          fragment: Fragment,
          savedInstanceState: Bundle?
        ) {
          if (fragment is NavHostFragment) {
            registerDestinationChange(fragment)
          }
        }
      }, true
    )
  }

  private fun registerDestinationChange(fragment: NavHostFragment) {
    fragment.navController.addOnDestinationChangedListener { _, destination, arguments ->
      listener(fragment, destination, arguments)
    }
  }

  override fun onActivityStarted(activity: Activity) = Unit

  override fun onActivityResumed(activity: Activity) = Unit

  override fun onActivityPaused(activity: Activity) = Unit

  override fun onActivityStopped(activity: Activity) = Unit

  override fun onActivitySaveInstanceState(
    activity: Activity,
    outState: Bundle
  ) = Unit

  override fun onActivityDestroyed(activity: Activity) = Unit

  companion object {

    fun install(
      application: Application,
      listener: (fragment: NavHostFragment, destination: NavDestination, arguments: Bundle?) -> Unit
    ) {
      application.registerActivityLifecycleCallbacks(
        GlobalNavHostDestinationChangedListener(listener)
      )
    }
  }
}