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

package com.example.android.motion.demo.loading

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.demo.SequentialTransitionSet
import com.example.android.motion.demo.plusAssign
import com.example.android.motion.demo.transitionSequential
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge

/**
 * Shows a list of cheeses. We use the Paging Library to load the list. Motion provides timely
 * feedback and the status of user actions. An animated placeholder UI can indicate that content
 * is loading. [PlaceholderAdapter] is used to animate the alpha of its [R.drawable.image_placeholder]
 * drawable and the alpha of its [R.drawable.text_placeholder] text view background while it pretends
 * to be delayed loading due to network delay.
 */
class LoadingActivity : AppCompatActivity() {

    /**
     * The [LoadingViewModel] view model which holds our dataset in its `cheeses` [LiveData] wrapped
     * [PagedList] of [Cheese] objects property.
     */
    private val viewModel: LoadingViewModel by viewModels()

    /**
     * The [RecyclerView] in our layout file with ID [R.id.list] which is used to display our list
     * of cheeses.
     */
    private lateinit var list: RecyclerView

    /**
     * The [SequentialTransitionSet] which applies a [Fade.OUT] and [Fade.IN] transition to our
     * [RecyclerView] field [list] when its adapter is first loading it. The lambda argument to
     * [transitionSequential] sets the duration of [fade] to [LARGE_EXPAND_DURATION] (300ms), its
     * interpolator to [FAST_OUT_SLOW_IN], adds a [Fade.OUT] fade followed by a [Fade.IN] fade and
     * adds an [TransitionListenerAdapter] whose `onTransitionEnd` override sets the item Animator
     * of [list] to the value saved in our [RecyclerView.ItemAnimator] field [savedItemAnimator] if
     * it is not `null`.
     */
    private val fade = transitionSequential {
        duration = LARGE_EXPAND_DURATION
        interpolator = FAST_OUT_SLOW_IN
        this += Fade(Fade.OUT)
        this += Fade(Fade.IN)
        addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                if (savedItemAnimator != null) {
                    list.itemAnimator = savedItemAnimator
                }
            }
        })
    }

    /**
     * The [PlaceholderAdapter] used to fill our [RecyclerView] with "flashing" animated empty views
     * while our dataset is being loaded into the `cheeses` property of our [LoadingViewModel] field
     * [viewModel]. An Oberver of `cheeses` replaces it with the "real" [CheeseAdapter] field
     * [cheeseAdapter] when it changes value.
     */
    private val placeholderAdapter = PlaceholderAdapter()

    /**
     * The [CheeseAdapter] used to display our dataset the `cheeses` property of our [LoadingViewModel]
     * field [viewModel] in our [RecyclerView].
     */
    private val cheeseAdapter = CheeseAdapter()

    /**
     * Temporary storage for saving the animations that take place on items as changes are made to
     * the adapter of the [RecyclerView] field [list]. It is saved to in the Observer of the `cheeses`
     * property of our [LoadingViewModel] field [viewModel] created in our [onCreate] override, and
     * restored to the `ItemAnimator` of [list] in the `onTransitionEnd` override of the
     * [TransitionListenerAdapter] field [fade]
     */
    private var savedItemAnimator: RecyclerView.ItemAnimator? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        list = findViewById(R.id.list)
        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.coordinator))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(list)

        // Show the initial placeholders.
        // See the ViewHolder implementation for how to create the loading animation.
        list.adapter = placeholderAdapter
        viewModel.cheeses.observe(this) { cheeses ->
            if (list.adapter != cheeseAdapter) {
                list.adapter = cheeseAdapter
                savedItemAnimator = list.itemAnimator
                list.itemAnimator = null
                TransitionManager.beginDelayedTransition(list, fade)
            }
            cheeseAdapter.submitList(cheeses)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.loading, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                TransitionManager.beginDelayedTransition(list, fade)
                list.adapter = placeholderAdapter
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
