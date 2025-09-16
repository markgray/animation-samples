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
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * List > Loading
 *
 * Shows a list of cheeses. We use the Paging 3 Library to load the list.
 * Motion provides timely feedback and the status of user actions.
 */
class LoadingActivity : AppCompatActivity() {

    /**
     * The [LoadingViewModel] for this activity, which is responsible for fetching the list of
     * cheeses. It is instantiated using the `by viewModels()` Kotlin property delegate from the
     * `activity-ktx` library.
     */
    private val viewModel: LoadingViewModel by viewModels()

    /**
     * The [RecyclerView] in the activity's layout that displays the list of cheeses.
     */
    private lateinit var list: RecyclerView

    /**
     * The [CheeseAdapter] for the list of cheeses. It is initialized with an empty list of cheeses
     * and is used to display the list of cheeses in the [RecyclerView].
     */
    private val cheeseAdapter = CheeseAdapter()

    /**
     * Called when the activity is first created. This is where we do all of our normal static set
     * up: create views, bind data to lists, etc.
     *
     * This method sets up the UI, including the toolbar and edge-to-edge display. It initializes the
     * [RecyclerView] with a [CheeseAdapter] and observes the [LoadingViewModel.cheeses] flow.
     * When new paged data is available, it's submitted to the adapter. It also sets up a coroutine
     * to collect the loading state from the adapter, which can be used to display loading indicators.
     *
     * We start by calling our super's implementation of `onCreate`. We set the content view to
     * the layout file `R.layout.loading_activity`. We initialize our [Toolbar] variable `toolbar`
     * to the view with ID `R.id.toolbar` in the content view.
     *
     * We initialize our [RecyclerView] property [list] to the view with ID `R.id.list` in the
     * content view. We set the toolbar as the support action bar for our activity. We call
     * [EdgeToEdge.setUpRoot] to set up the root view of the activity for edge-to-edge display to
     * the view with ID `R.id.coordinator`. We call [EdgeToEdge.setUpAppBar] to set up the app bar
     * for edge-to-edge display to the view with ID `R.id.app_bar` and the [Toolbar] variable
     * `toolbar`. We call [EdgeToEdge.setUpScrollingContent] to set up the scrolling content for
     * edge-to-edge display.
     *
     * We set the adapter of the [RecyclerView] variable [list] to the [CheeseAdapter] property
     * [cheeseAdapter]. We launch a coroutine in the lifecycle scope of our activity using the
     * [CoroutineScope.launch] method of [lifecycleScope]. In the [CoroutineScope] `block` lambda
     * argument, we call the [Flow.collectLatest] method of the [Flow] of [PagingData] of [Cheese]
     * property of the [LoadingViewModel.cheeses] property of our [LoadingViewModel] property
     * [viewModel]. When new [PagingData] of [Cheese] are emitted, they are submitted to the
     * [CheeseAdapter] property [cheeseAdapter] using the [PagingDataAdapter.submitData] method.
     *
     * We launch another coroutine in the lifecycle scope of our activity using the
     * [CoroutineScope.launch] method of [lifecycleScope]. In the [CoroutineScope] `block` lambda
     * argument we call the [Flow.collectLatest] method of the [Flow] of [CombinedLoadStates] property
     * of the [CheeseAdapter.loadStateFlow] property of our [CheeseAdapter] property [cheeseAdapter].
     * In the `action` lambda argument, we do nothing, but we could show a progress bar or other
     * indicators based on the [CombinedLoadStates] argument `loadStates]` passed the lambda.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in [onSaveInstanceState]. We do
     * not override [onSaveInstanceState] so we do not use it.
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

        list.adapter = cheeseAdapter

        lifecycleScope.launch {
            viewModel.cheeses.collectLatest { pagingData: PagingData<Cheese> ->
                cheeseAdapter.submitData(pagingData)
            }
        }

        // Optional: Add a LoadStateListener to show/hide a progress bar or other indicators
        // based on the load state. TODO: Add a LoadStateListener
        lifecycleScope.launch {
            cheeseAdapter.loadStateFlow.collectLatest { loadStates: CombinedLoadStates ->
                // You can use loadStates.refresh, loadStates.append, loadStates.prepend
                // to show different UI elements for different states.
                // For example, show a progress bar when loadStates.refresh is LoadState.Loading.
                // For simplicity, this example doesn't add explicit loading UI here,
                // relying on the adapter's behavior and potential shimmer/placeholder in ViewHolder.
            }
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in to [menu]. This is only called once, the first time the options menu is displayed.
     *
     * We use a [menuInflater] to inflate our menu resource file `R.menu.loading` into the [Menu]
     * parameter [menu]. This adds a "Refresh" button to the app bar. Finally we call our super's
     * implementation of `onCreateOptionsMenu` and return the value it returns.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return `true` for the menu to be displayed; if you return `false` it will
     * not be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.loading, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in our options menu is selected.
     *
     * We check if the [MenuItem.getItemId] of the [MenuItem] parameter [item] is [R.id.action_refresh].
     * If it is, we call the `refresh` method of our [CheeseAdapter] field [cheeseAdapter].
     * The Paging 3 library handles this by invalidating the `PagingSource` and reloading
     * the data.
     *
     * As an optional visual cue, we create a [Fade] transition named `fade` with a duration of
     * [LARGE_EXPAND_DURATION] and an interpolator of [FAST_OUT_SLOW_IN]. We then use the
     * [TransitionManager.beginDelayedTransition] method to start this transition on our
     * [RecyclerView] field [list]. This causes the list to fade out as it becomes empty and
     * then fade back in as it is repopulated with new data.
     *
     * Finally, we return `true` to consume the event here.
     *
     * If the `itemId` is not [R.id.action_refresh], we call our super's implementation of
     * `onOptionsItemSelected` and return the value it returns.
     *
     * @param item The menu item that was selected.
     * @return `true` to consume the event here, or the result of calling our super's implementation
     * of `onOptionsItemSelected`.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                // Paging 3 handles refresh by calling adapter.refresh()
                // The PagingSource will be re-invalidated and data reloaded.
                cheeseAdapter.refresh()
                // Optional: You might want a visual cue that refresh is happening.
                // A SwipeRefreshLayout is a common pattern for this.
                // If you want to keep the fade transition for the list becoming empty and then repopulating:
                val fade = Fade().apply {
                    duration = LARGE_EXPAND_DURATION
                    interpolator = FAST_OUT_SLOW_IN
                }
                TransitionManager.beginDelayedTransition(list, fade)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
