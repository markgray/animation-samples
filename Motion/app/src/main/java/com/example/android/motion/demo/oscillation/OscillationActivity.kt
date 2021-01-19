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

package com.example.android.motion.demo.oscillation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.R
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge

/**
 * List > Oscillation
 *
 * Oscillation occurs when an animation uses more than one overshoot. Each additional back and
 * forth movement is smaller than the previous one, until the motion stops. Oscillation may be
 * used to create a cartoon style. The item views in the [RecyclerView] in the UI "wobble" back
 * and forth as they are scrolled onto the screen.
 */
class OscillationActivity : AppCompatActivity() {

    /**
     * Our [ViewModel], it holds a list of 15 shuffled [Cheese] objects from the [Cheese.ALL] list
     * whose `name` property is shorter than 10 characters in its [LiveData] wrapped [List] of
     * [Cheese] objects [OscillationViewModel.cheeses].
     */
    private val viewModel: OscillationViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.oscillation_activity] which consists
     * of a `CoordinatorLayout` (`CoordinatorLayout` is a super-powered `FrameLayout` which will
     * coordinate the animations and transitions of the views within it) holding a `MaterialToolbar`
     * as its `AppBarLayout` and a [RecyclerView] with an `app:layout_behavior` attribute which
     * uses `AppBarLayout.ScrollingViewBehavior` to automatically scroll its AppBarLayout sibling.
     *
     * Next we initialize our [Toolbar] variable `val toolbar` to the View with ID [R.id.toolbar],
     * and our [RecyclerView] variable `val list` to the View with ID [R.id.list]. We set `toolbar`
     * to act as the ActionBar for our Activity window.
     *
     * We use our [EdgeToEdge.setUpRoot] method to set up the view with ID [R.id.root] (it is the
     * outermost `CoordinatorLayout` holding the rest of our UI) for edge to edge display, use our
     * [EdgeToEdge.setUpAppBar] method to configure the app bar with ID [R.id.app_bar] and the
     * toolbar `toolbar` for edge-to-edge display, and use our method [EdgeToEdge.setUpScrollingContent]
     * to set up our scrolling ViewGroup `list` for edge-to-edge display.
     *
     * Now we initialize our [CheeseAdapter] variable `val adapter` to a new instance and set it to
     * be the adapter for `list`. We add the [CheeseAdapter.onScrollListener] field of `adapter` as
     * a listener that will be notified of any changes in scroll state or position to `list` (tilts
     * the visible items in `list` while the list is scrolled), and set the
     * [RecyclerView.EdgeEffectFactory] of `list` to the [CheeseAdapter.edgeEffectFactory] field
     * of `adapter` (adds bounce effect when the list is over-scrolled).
     *
     * Finally we add an observer to the [OscillationViewModel.cheeses] field of our [viewModel]
     * field whose lambda will submit the [LiveData] wrapped [List] of [Cheese] objects to
     * `adapter` to be diffed, and displayed when it changes value.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.oscillation_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val list: RecyclerView = findViewById(R.id.list)
        setSupportActionBar(toolbar)

        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(list)

        val adapter = CheeseAdapter()
        list.adapter = adapter
        // The adapter knows how to animate its items while the list is scrolled.
        list.addOnScrollListener(adapter.onScrollListener)
        list.edgeEffectFactory = adapter.edgeEffectFactory

        viewModel.cheeses.observe(this) { cheeses ->
            adapter.submitList(cheeses)
        }
    }
}
