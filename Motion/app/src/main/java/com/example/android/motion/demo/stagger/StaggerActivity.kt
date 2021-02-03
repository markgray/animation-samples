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

package com.example.android.motion.demo.stagger

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.android.motion.R
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge

/**
 * List > Stagger
 *
 * Shows a list of items. The items are loaded asynchronously, and they appear with stagger.
 *
 * Stagger refers to applying temporal offsets to a group of elements in sequence, like a list.
 * Stagger creates a cascade effect that focuses attention briefly on each item. It can reveal
 * significant content or highlight affordances within a group.
 *
 * See
 * [Stagger](https://material.io/design/motion/customization.html#sequencing)
 * for the detail.
 */
class StaggerActivity : AppCompatActivity() {

    /**
     * The [ViewModel] holding our dataset which consists of a [MutableLiveData] wrapped [List] of
     * [Cheese] objects in a private field: `_cheeses`, with public read only access to `_cheeses`
     * supplied by its [CheeseListViewModel.cheeses] reference to `_cheeses`. It also has a method
     * [CheeseListViewModel.refresh] which simulates a network load by delaying 300ms before setting
     * the value of `_cheeses` to the [Cheese.ALL] list, and a method [CheeseListViewModel.empty]
     * which sets the value of `_cheeses` to [emptyList]
     */
    private val viewModel: CheeseListViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.stagger_activity] which consists of
     * a `CoordinatorLayout` root view holding an `AppBarLayout` which holds a [Toolbar] child, and
     * a [RecyclerView] with a vertical scrollbar and a `LinearLayoutManager` as its layoutManager.
     *
     * We initialize our [Toolbar] variable `val toolbar` by finding the view with ID [R.id.toolbar]
     * and our [RecyclerView] variable `val list` by finding the view with ID [R.id.list]. We then
     * set `toolbar` to act as the `ActionBar` for this Activity window. We call our method
     * [EdgeToEdge.setUpRoot] to configure the view with ID [R.id.root] (our root view) for edge to
     * edge display, call our method [EdgeToEdge.setUpAppBar] configure our app bar (whose ID is
     * [R.id.app_bar]) and our `toolbar` [Toolbar] for edge-to-edge display, and call our method
     * [EdgeToEdge.setUpScrollingContent] to configure the scrolling content in `list` for edge to
     * edge display.
     *
     * Next we initialize our [CheeseListAdapter] variable `val adapter` with a new instance and set
     * the [RecyclerView.Adapter] of `list` to this `adapter`. We set the [RecyclerView.ItemAnimator]
     * (defines the animations that take place on items as changes are made to the adapter) of `list`
     * to an anonymous [DefaultItemAnimator] whose `animateAdd` override disables item additions in
     * the [RecyclerView] by first calling [DefaultItemAnimator.dispatchAddFinished] to indicate that
     * the add animation is done, then calling [DefaultItemAnimator.dispatchAddStarting] to indicate
     * an add animation is being started and returning `false` to indicate that we do not request a
     * call to [DefaultItemAnimator.runPendingAnimations] (we disable it in [RecyclerView] because we
     * animate item additions on our side).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stagger_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val list: RecyclerView = findViewById(R.id.list)
        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(list)

        val adapter = CheeseListAdapter()
        list.adapter = adapter

        // We animate item additions on our side, so disable it in RecyclerView.
        list.itemAnimator = object : DefaultItemAnimator() {
            /**
             * Called when an item is added to the [RecyclerView].
             *
             * @param holder The item that is being added.
             * @return `true` if a later call to `runPendingAnimations` is requested,
             * `false` otherwise.
             */
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                dispatchAddFinished(holder)
                dispatchAddStarting(holder)
                return false
            }
        }

        // This is the transition for the stagger effect.
        val stagger = Stagger()

        viewModel.cheeses.observe(this) { cheeses ->
            // Delay the stagger effect until the list is updated.
            TransitionManager.beginDelayedTransition(list, stagger)
            adapter.submitList(cheeses)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stagger, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                // In real-life apps, refresh feature would just overwrite the existing list with
                // the new list. In this demo, we clear the list and repopulate to demonstrate the
                // stagger effect again.
                viewModel.empty()
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
