/*
 * Copyright 2020 Google LLC
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

package com.example.android.drawableanimations

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import com.example.android.drawableanimations.ui.home.HomeFragment

/**
 * This is our `MainActivity`. It uses the alternate [AppCompatActivity] constructor to provide
 * the layout file `R.layout.main_activity` that will be inflated when we call our super's
 * implementation of `onCreate`. That layout file holds only a `FragmentContainerView` with the
 * ID `R.id.main` which we will replace with one of our three fragments: `HomeFragment`,
 * `AnimatedFragment`, or `SeekableFragment` depending on the state of the app. We start out by
 * adding an instance of `HomeFragment` to it in our `onCreate` override if we are not being
 * recreated after a configuration change (after a configuration change the system takes care to
 * restore the fragment that was previously occupying it).
 */
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`
     * (since we passed our layout file `R.layout.main_activity` to our super's constructor it will
     * inflate that file and set it to be our content view at this point). If our [Bundle] parameter
     * [savedInstanceState] this is the first time we have been called, so we use the `commitNow`
     * method of the `FragmentManager` for interacting with fragments associated with this activity
     * to have it replace (add) an instance of [HomeFragment] to the container with ID `R.id.main`.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in [onSaveInstanceState],
     * otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootView = findViewById<FragmentContainerView>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.main, HomeFragment())
            }
        }
    }
}
