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
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.ui.EdgeToEdge
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * List > Loading
 *
 * Shows a list of cheeses. We use the Paging 3 Library to load the list.
 * Motion provides timely feedback and the status of user actions.
 */
class LoadingActivity : AppCompatActivity() {

    private val viewModel: LoadingViewModel by viewModels()
    private lateinit var list: RecyclerView
    private val cheeseAdapter = CheeseAdapter()

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
            viewModel.cheeses.collectLatest { pagingData ->
                cheeseAdapter.submitData(pagingData)
            }
        }

        // Optional: Add a LoadStateListener to show/hide a progress bar or other indicators
        // based on the load state.
        lifecycleScope.launch {
            cheeseAdapter.loadStateFlow.collectLatest { loadStates ->
                // You can use loadStates.refresh, loadStates.append, loadStates.prepend
                // to show different UI elements for different states.
                // For example, show a progress bar when loadStates.refresh is LoadState.Loading.
                // For simplicity, this example doesn't add explicit loading UI here,
                // relying on the adapter's behavior and potential shimmer/placeholder in ViewHolder.
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.loading, menu)
        return super.onCreateOptionsMenu(menu)
    }

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
