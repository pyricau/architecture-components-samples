package com.example.android.navigationadvancedsample.perf

import android.view.View
import androidx.fragment.app.Fragment

interface ViewUpdateRunner {
  fun runOnViewsUpdated(block: (View) -> Unit)
}

class ImmediateFragmentViewUpdateRunner(private val fragment: Fragment) : ViewUpdateRunner {
  override fun runOnViewsUpdated(block: (View) -> Unit) {
    block(fragment.view!!)
  }
}

class OnTxCommitFragmentViewUpdateRunner(private val fragment: Fragment) : ViewUpdateRunner {
  override fun runOnViewsUpdated(block: (View) -> Unit) {
    val transaction = fragment.parentFragmentManager.beginTransaction()
    transaction.runOnCommit {
      block(fragment.view!!)
    }.commit()
  }
}