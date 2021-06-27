/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.samples.donuttracker

import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.android.samples.donuttracker.databinding.ActivityMainBinding

/**
 * Main activity class. Not much happens here, just some basic UI setup.
 * The main logic occurs in the fragments which populate this activity.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        Notifier.init(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                Debug.startMethodTracingSampling(
                        // This stores in the external storage, e.g.
                        // /sdcard/Android/data/com.android.samples.navdonutcreator/files/1624811076892.trace
                        "${System.currentTimeMillis()}.trace",
                        // 50Mb max file size
                        50 * 1024 * 1024,
                        // Every 1 ms
                        1000
                )
            }
            MotionEvent.ACTION_UP -> {
                val handler = Handler(Looper.getMainLooper())
                // Fires right before View click listener
                handler.post {
                    // Run when fragment navigation transaction executes
                    supportFragmentManager.beginTransaction().runOnCommit {
                        // Stop right AFTER the next frame is done.
                        Choreographer.getInstance().postFrameCallback {
                            handler.postAtFrontOfQueue {
                                Debug.stopMethodTracing()
                            }
                        }
                    }.commit()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
