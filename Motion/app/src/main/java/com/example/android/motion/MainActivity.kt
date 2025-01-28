/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.motion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import com.example.android.motion.ui.EdgeToEdge
import com.example.android.motion.ui.demolist.DemoListFragment
import com.google.android.material.appbar.AppBarLayout

/**
 * Our main activity. Just sets up our UI which consists of an `AppBarLayout` with ID `R.id.app_bar`
 * holding our [Toolbar], and a `FrameLayout` with ID `R.id.container` holding the various fragments
 * of our app starting with a [DemoListFragment] which is used to switch between the different demo
 * fragments.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file `R.layout.main_activity`. We initialize our
     * [Toolbar] variable `val toolbar` by finding the view with ID `R.id.toolbar` in our UI and
     * set it to act as the `ActionBar` for our Activity window. We then call [EdgeToEdge.setUpRoot]
     * with the view with ID `R.id.main` (the `CoordinatorLayout` root view of our UI) which
     * configures the view for edge-to-edge display, and call [EdgeToEdge.setUpAppBar] with the
     * [AppBarLayout]  with ID `R.id.app_bar` and our [Toolbar] variable `toolbar` to configure the
     * [AppBarLayout] and [Toolbar] for edge-to-edge display. If our parameter [savedInstanceState]
     * is not `null` we are being recreated after a configuration change and the system will take
     * care of restoring whichever fragment is running in our UI and we are done. If it is `null`
     * this is the first time we are being called to we call the `commitNow` extension function of
     * the [FragmentManager] for interacting with fragments associated with this activity to replace
     * the contents of the container with ID `R.id.container` (the `FrameLayout` in our layout file)
     * with a new instance of [DemoListFragment] automatically committing it if completes without
     * exception.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * Otherwise it is `null`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configure edge-to-edge display.
        EdgeToEdge.setUpRoot(findViewById(R.id.main))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)

        // Set up the fragment.
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.container, DemoListFragment())
            }
        }
    }
}
