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

package com.example.android.motion.demo.sharedelement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.motion.R
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge

/**
 * Navigation > Shared element
 *
 * A transition may include a focal element, which is a persistent element significant to the
 * hierarchy that can be tweened. Like animated containers, focal elements enhance continuity
 * by seamlessly transforming their appearance. The starting fragment [CheeseGridFragment] shows
 * all of the [Cheese] objects in the [Cheese.ALL] list of [Cheese] in a 3 wide grid, and when
 * you click on one of them it navigates to the [CheeseDetailFragment] to display the "details"
 * of the [Cheese] using the cheese picture as the shared focal element.
 */
class SharedElementActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementatin of `onCreate`,
     * then we set our content view to our layout file `R.layout.shared_element_activity`. Its top
     * level view group is a `FragmentContainerView` holding a `NavHostFragment` with the attributes
     * app:defaultNavHost="true" (ensures that this `NavHostFragment` intercepts the system Back
     * button) and app:navGraph="@navigation/shared_element" (associates the `NavHostFragment` with
     * the navigation graph navigation/shared_element.xml which specifies all of the destinations
     * of this `NavHostFragment` to which users can navigate). The app:startDestination attribute
     * of the navigation element in navigation/shared_element.xml specifies the fragment with ID
     * id/cheeseGridFragment to be the starting fragment, and that fragment element is for the
     * [CheeseGridFragment].
     *
     * Having set our content view we call our [EdgeToEdge.setUpRoot] method to setup the view with
     * ID `R.id.nav_host` (the root `FragmentContainerView` of our layout file) for edge to edge
     * display.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shared_element_activity)
        EdgeToEdge.setUpRoot(findViewById(R.id.nav_host))
    }
}
