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

class CheeseDataSource : PagingSource<Int, Cheese>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cheese> {
        val page = params.key ?: 0 // Start from page 0 if no key is provided
        val pageSize = params.loadSize

        // Simulate a slow network.
        delay(3000L)

        val startPosition = page * pageSize
        val endPosition = (startPosition + pageSize).coerceAtMost(Cheese.ALL.size)

        val cheeses = if (startPosition < Cheese.ALL.size) {
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
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Cheese>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
