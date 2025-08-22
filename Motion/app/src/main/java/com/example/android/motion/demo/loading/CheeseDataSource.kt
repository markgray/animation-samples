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

import android.os.SystemClock
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource // TODO: replaced by PagingSource
import androidx.paging.PagingSource
import com.example.android.motion.model.Cheese

/**
 * Position-based data loader for our fixed-size, countable data set of [Cheese] objects, supporting
 * fixed-size loads at arbitrary page positions. It simulates internet delay by sleeping for 3000ms
 * every time it is asked to do a load in both our [loadInitial] and our [loadRange] overrides.
 */
class CheeseDataSource : PositionalDataSource<Cheese>() {

    /**
     * Factory for DataSources. Data-loading systems of an application or library can implement this
     * interface to allow `LiveData<PagedList>`'s to be created.
     */
    companion object Factory : DataSource.Factory<Int, Cheese>() {
        /**
         * Create a DataSource. The [DataSource] should invalidate itself if the snapshot is no
         * longer valid. If a [DataSource] becomes invalid, the only way to query more data is to
         * create a new [DataSource] from the Factory.
         *
         * @return the new DataSource.
         */
        override fun create(): DataSource<Int, Cheese> = CheeseDataSource()
    }

    /**
     * Load initial list data. This method is called to load the initial page(s) from the DataSource.
     * Result list must be a multiple of pageSize to enable efficient tiling. We simulate a slow
     * network by sleeping for 3,000ms. Then we call the `onResult` method of our `LoadInitialCallback`
     * of [Cheese] parameter [callback] passing as its data the list of [Cheese] objects returned by
     * the `subList` method when it selects the portion of the [Cheese.ALL] list between the position
     * `requestedStartPosition` of `LoadInitialParams` parameter [params] and the position created
     * by adding its `requestedLoadSize` property to `requestedStartPosition`. The `position` of
     * the item at the front of the list is the `requestedStartPosition` property of [params] and
     * the `totalCount` total number of items that may be returned from this [DataSource] is the
     * size of the [Cheese.ALL] list.
     *
     * @param params Parameters for initial load, including requested start position, load size, and
     * page size.
     * @param callback Callback that receives initial load data, including
     * position and total data set size.
     */
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Cheese>) {
        // Simulate a slow network.
        SystemClock.sleep(3000L)
        callback.onResult(
            Cheese.ALL.subList(
                params.requestedStartPosition,
                params.requestedStartPosition + params.requestedLoadSize
            ),
            params.requestedStartPosition,
            Cheese.ALL.size
        )
    }

    /**
     * Called to load a range of data from the DataSource. This method is called to load additional
     * pages from the [DataSource] after the `LoadInitialCallback` passed to [loadInitial] has
     * initialized a `PagedList`. Unlike [loadInitial], this method must return the number of items
     * requested, at the position requested. We simulate a slow network by sleeping for 3,000ms.
     * Then we call the `onResult` method of our `LoadInitialCallback` of [Cheese] parameter
     * [callback] passing as its data the list of [Cheese] objects returned by the `subList` method
     * when it selects the portion of the [Cheese.ALL] list between the position
     * `requestedStartPosition` of `LoadInitialParams` parameter [params] and the position created
     * by adding its `loadSize` property to `requestedStartPosition`.
     *
     * @param params Parameters for load, including start position and load size.
     * @param callback Callback that receives loaded data.
     */
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Cheese>) {
        // Simulate a slow network.
        SystemClock.sleep(3000L)
        callback.onResult(
            Cheese.ALL.subList(
                params.startPosition,
                params.startPosition + params.loadSize
            )
        )
    }
}
