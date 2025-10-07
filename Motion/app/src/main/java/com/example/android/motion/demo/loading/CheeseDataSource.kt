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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.motion.model.Cheese
import kotlinx.coroutines.delay

/**
 * A [PagingSource] that loads pages of cheeses from a static list [Cheese.ALL].
 *
 * This class simulates a slow network by delaying for 3 seconds in the [load] method.
 * It uses integer page numbers as keys for pagination.
 */
class CheeseDataSource : PagingSource<Int, Cheese>() {

    /**
     * Loads a page of data for the [PagingSource].
     *
     * This function is called by the Paging library to fetch a page of data. It simulates a
     * network delay of 3 seconds. It calculates the sublist of cheeses to be fetched based on
     * the page key and page size provided in [LoadParams] of [Int] parameter [params].
     *
     * We start by initializing our [Int] variable `page` to the [LoadParams.key] of our [LoadParams]
     * of [Int] parameter [params] defaulting to `0` if it is `null`. We initialize our [Int] variable
     * `pageSize` to the [LoadParams.loadSize] of our [LoadParams] of [Int] parameter [params]. We
     * delay for `3,000` miliseconds to simulate a slow network.
     *
     * We initialize our [Int] variable `startPosition` to `page` times `pageSize`. We initialize
     * our [Int] variable `endPosition` to the minimum of `startPosition` plus `pageSize` and the
     * size of the list of all cheeses in [Cheese.ALL]. If `startPosition` is less than the size of
     * the list of all cheeses in [Cheese.ALL], we initialize our [List] variable `cheeses` to a
     * sublist of the list of all cheeses in [Cheese.ALL] from `startPosition` to `endPosition`,
     * otherwise we initialize it to an empty list.
     *
     * Wrapped in a try-catch block, we return a [LoadResult.Page] containing the list of cheeses as
     * its `data` and the keys for the previous and next pages as its `prevKey` and `nextKey`
     * arguments. If an error occurs, it returns a [LoadResult.Error] with the exception as its
     * `throwable` argument.
     *
     * @param params Parameters for the load request, including the key for the page to be loaded
     * and the requested load size. The key is an integer representing the page number.
     * @return A [LoadResult.Page] containing the list of cheeses for the requested page, along with
     * keys for the previous and next pages. If an error occurs, it returns a [LoadResult.Error].
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cheese> {
        val page = params.key ?: 0 // Start from page 0 if no key is provided
        val pageSize = params.loadSize

        // Simulate a slow network.
        delay(timeMillis = 3_000L)

        val startPosition: Int = page * pageSize
        val endPosition: Int =
            (startPosition + pageSize).coerceAtMost(maximumValue = Cheese.ALL.size)

        val cheeses: List<Cheese> = if (startPosition < Cheese.ALL.size) {
            Cheese.ALL.subList(startPosition, endPosition)
        } else {
            emptyList()
        }

        return try {
            LoadResult.Page(
                data = cheeses,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (cheeses.isEmpty() || endPosition >= Cheese.ALL.size) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }

    /**
     * Provides a key for initial load or refresh, based on the current scroll position.
     *
     * The Paging library calls this method when the data needs to be refreshed, for example,
     * after a configuration change or when the user manually triggers a refresh. The key returned
     * by this method will be used as the `key` in the initial `load` call.
     *
     * This implementation determines the key based on the [PagingState.anchorPosition], which is
     * the most recently accessed index in the list. It finds the page closest to this position and
     * calculates the key that would load that same page.
     *
     * @param state The current state of the paging data, including the list of pages loaded,
     * the last accessed position (`anchorPosition`), and the config.
     * @return An `Int` representing the page key to be used for the refresh, or `null` if the
     * anchor position is not available, which would cause the load to start from the initial key.
     */
    override fun getRefreshKey(state: PagingState<Int, Cheese>): Int? {
        return state.anchorPosition?.let { anchorPosition: Int ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(other = 1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(other = 1)
        }
    }
}
