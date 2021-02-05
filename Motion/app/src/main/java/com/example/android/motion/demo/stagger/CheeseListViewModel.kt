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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.motion.model.Cheese
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The [ViewModel] used by [StaggerActivity] to hold its dataset.
 */
class CheeseListViewModel : ViewModel() {

    /**
     * Our dataset, private to prevent modification by other classes. Public read only access is
     * provided by our [cheeses] field. It can be emptied by calling our [empty] method, and loaded
     * from the [Cheese.ALL] list of [Cheese] with a 300ms delay to simulate network access by
     * calling our [refresh] method.
     */
    private val _cheeses = MutableLiveData<List<Cheese>>()

    /**
     * Public read only access to our private [_cheeses] field. An observer is added to it in the
     * `onCreate` override of [StaggerActivity] which will, when it changes value, begin a delayed
     * [Stagger] transition of the `RecyclerView` which will display [cheeses] and submit [cheeses]
     * to be diffed and displayed.
     */
    val cheeses: LiveData<List<Cheese>> = _cheeses

    init {
        /**
         * Initialize our `_cheeses` to the `Cheese.ALL` list of `Cheese` after a 300ms delay to
         * simulate network access.
         */
        refresh()
    }

    /**
     * Empties [_cheeses] by setting its value to an empty read-only list. Called from the override
     * of `onOptionsItemSelected` in [StaggerActivity] when the "Refresh" menu item is clicked.
     */
    fun empty() {
        _cheeses.value = emptyList()
    }

    /**
     * Delays for 300ms to simulate network access, then sets the `value` of our [_cheeses] field
     * to the [Cheese.ALL] list of [Cheese].
     */
    fun refresh() {
        viewModelScope.launch {
            // Simulate a loading delay of database, filesystem, etc.
            delay(300L)
            _cheeses.value = Cheese.ALL
        }
    }
}
