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

package com.example.android.motion.demo.navfadethrough

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.motion.R
import com.example.android.motion.ui.EdgeToEdge

/**
 * "Navigation > Fade through"
 *
 * To simplify overlapping motion, consider substituting a focal element
 * with a fade through transition.
 */
class NavFadeThroughActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.nav_fade_through_activity] which
     * consists of a `FragmentContainerView` whose "android:name" attribute specifies the fragment
     * androidx.navigation.fragment.NavHostFragment and whose "app:navGraph" attribute points to our
     * navigation file navigation/nav_fade_through.xml as the `navigation` graph for our activity.
     * The "app:startDestination" fragment of the graph is our fragment [CheeseCardFragment]. Finally
     * we call our [EdgeToEdge.setUpRoot] method to set up the root view of our UI (the view has the
     * ID [R.id.nav_host]) for edge to edge display.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_fade_through_activity)
        EdgeToEdge.setUpRoot(findViewById(R.id.nav_host))
    }
}
