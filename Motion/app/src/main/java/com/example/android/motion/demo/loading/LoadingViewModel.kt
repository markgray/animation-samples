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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.motion.model.Cheese
import kotlinx.coroutines.flow.Flow

/**
 * The [ViewModel] used by our `LoadingActivity` demo activity.
 */
class LoadingViewModel : ViewModel() {

    val cheeses: Flow<PagingData<Cheese>> = Pager(
        config = PagingConfig(
            pageSize = 15,
            enablePlaceholders = false // Placeholders are not typically used with PagingSource
        ),
        pagingSourceFactory = { CheeseDataSource() }
    ).flow.cachedIn(viewModelScope)

    // The refresh method is no longer strictly necessary with Paging 3's reactive approach,
    // as the PagingSource will be reloaded automatically on invalidation or adapter refresh.
    // However, if you need an explicit refresh mechanism, you would typically invalidate the
    // PagingSource. For this example, we'll remove it as the list now supports swipe-to-refresh
    // in the Activity, which triggers adapter.refresh().
}
