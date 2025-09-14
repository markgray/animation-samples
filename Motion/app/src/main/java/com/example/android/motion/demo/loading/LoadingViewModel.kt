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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource // TODO: replaced by PagingSource
import androidx.paging.toLiveData
import com.example.android.motion.model.Cheese

/**
 * The [ViewModel] used by our [LoadingActivity] demo activity.
 */
class LoadingViewModel : ViewModel() {

    /**
     * The [LiveData] wrapped [PagedList] of [Cheese] objects that the `DataSource.Factory` of
     * our [CheeseDataSource] constructs. The creation of the first [PagedList] is deferred
     * until the [LiveData] is observed. An observer is added to it in our [refresh] method
     * which will when it changes value (when the simulated network download completes) post a
     * task to the main thread to set [_cheeses] to the new value.
     */
    private var source: LiveData<PagedList<Cheese>>? = null

    /**
     * Our [MutableLiveData] subclass which observes our [source] LiveData object and reacts to
     * OnChanged events from it. It is private to prevent modification by other classes. Public
     * read only access is provided by our [cheeses] field.
     */
    private val _cheeses = MediatorLiveData<PagedList<Cheese>>()

    /**
     * Our dataset of [Cheese] objects. It is updated from our [source] field by an observer added
     * to [source] in our [refresh] method, and an observer added to it in the `onCreate` override
     * of [LoadingActivity] does what's needed to display the new data in its `RecyclerView` when
     * it changes value.
     */
    val cheeses: LiveData<PagedList<Cheese>> = _cheeses

    init {
        /**
         * Initialize our dataset.
         */
        refresh()
    }

    /**
     * Iniitializes or refreshes our dataset using our simulated network download [PositionalDataSource]
     * subclass [CheeseDataSource]. If our [source] field is not `null` we calll the `removeSource`
     * method of our [MediatorLiveData] wrapped field [_cheeses] to have it stop listening to our
     * [source] field. We then initialize our variable `val s` to a [LiveData] wrapped [PagedList]
     * of [Cheese] objects using the `DataSource.Factory` of our [PositionalDataSource] subclass
     * [CheeseDataSource] with a `pageSize` of 15. We set our [source] field to `s` then add `s`
     * to our [_cheeses] field to have it start listening to `s`, with a lambda which calls the
     * `postValue` method of [_cheeses] with `s` to post a task to the main thread to set the value
     * of [_cheeses] to `s` when `s` changes value. An observer is added to our [cheeses] field in
     * the `onCreate` override of [LoadingActivity] will then update its UI to reflect the changed
     * dataset.
     */
    fun refresh() {
        source?.let { _cheeses.removeSource(it) }
        val s = CheeseDataSource.toLiveData(pageSize = 15)
        source = s
        _cheeses.addSource(s) { _cheeses.postValue(it) }
    }
}
