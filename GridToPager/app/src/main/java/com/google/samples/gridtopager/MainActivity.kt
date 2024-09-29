/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.gridtopager

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.google.samples.gridtopager.fragment.GridFragment

/**
 * Grid to pager app's main activity.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.activity_main] (it contains only a
     * `FrameLayout` with ID [R.id.fragment_container]). If our [Bundle] parameter [savedInstanceState]
     * is not `null` we are being recreated after a configuration change so we set our static field
     * [currentPosition] to the [Int] stored under the key [KEY_CURRENT_POSITION] in [savedInstanceState]
     * and return (the system will take care of restoring the proper fragment to our UI). On the other
     * hand if [savedInstanceState] is `null` this is the first time we have been called so we initialize
     * our [FragmentManager] variable `val fragmentManager` to the [FragmentManager] for interacting
     * with fragments associated with this activity then use it to begin a `FragmentTransaction`
     * which adds an instance of [GridFragment] to the container with ID [R.id.fragment_container]
     * using the simple name of the [GridFragment] class as the tag name, then commit that transaction.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in [onSaveInstanceState],
     * in our case we save the value of our static field [currentPosition] under the key
     * [KEY_CURRENT_POSITION]. If [savedInstanceState] is `null` this is the first time we have
     * been called.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<FrameLayout>(R.id.fragment_container)
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
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0)
            // Return here to prevent adding additional GridFragments when changing orientation.
            return
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, GridFragment(), GridFragment::class.java.simpleName)
            .commit()
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in [onCreate] or [onRestoreInstanceState] (the [Bundle] populated by this
     * method will be passed to both). First we call our super's implementation of `onSaveInstanceState`
     * then we store our static field [currentPosition] under the key [KEY_CURRENT_POSITION] in our
     * [Bundle] parameter [outState].
     *
     * @param outState Bundle in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_POSITION, currentPosition)
    }

    companion object {
        /**
         * Holds the current image position to be shared between the grid and the pager fragments.
         * This position is updated when a grid item is clicked, or when paging the pager.
         *
         * In this demo app, the position is an index into the int array of resource IDs contained
         * in `ImageData.IMAGE_DRAWABLES[]`
         */
        @JvmField
        var currentPosition: Int = 0

        /**
         * Key under which we save our field [currentPosition] when our [onSaveInstanceState]
         * override is called.
         */
        private const val KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition"
    }
}