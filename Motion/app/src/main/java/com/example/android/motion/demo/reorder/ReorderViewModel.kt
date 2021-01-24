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

package com.example.android.motion.demo.reorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.model.Cheese

/**
 * The [ViewModel] used by our [ReorderActivity] demo app. It exposes a publicly accessible [cheeses]
 * field which mirrors the private [MutableLiveData] wrapped [MutableList] of [Cheese] objects field
 * [_cheeses], and a [move] method which allows the movement of [Cheese] objects in [_cheeses].
 */
class ReorderViewModel : ViewModel() {

    /**
     * The dataset of [Cheese] objects displayed in the [RecyclerView] of the [ReorderActivity] demo
     * app using [CheeseGridAdapter] as the [ListAdapter]. This is private because we don't want to
     * allow other classes to directly set it, public read only access is provided by our [List] of
     * [Cheese] field [cheeses]. Our method [move] can be used to move [Cheese] objects to new
     * positions in our [MutableList] and is called when the the user drags a [Cheese] itemView over
     * another [Cheese] by the `onMove` override of the [ItemTouchHelper.Callback] of the
     * [ItemTouchHelper] attached to the [RecyclerView].
     */
    private val _cheeses = MutableLiveData(Cheese.ALL.toMutableList())

    /**
     * Public read only access to our [MutableLiveData] wrapped [MutableList] of [Cheese] field
     * [_cheeses]. An observer is added to it in the `onCreate` override of [ReorderActivity] whose
     * lambda submits the [List] to the [CheeseGridAdapter] supplying data to the [RecyclerView] in
     * the UI whenever the [_cheeses] backing field changes value.
     */
    val cheeses = _cheeses.map { it.toList() }

    fun move(from: Int, to: Int) {
        _cheeses.value?.let { list ->
            val cheese = list.removeAt(from)
            list.add(to, cheese)
            _cheeses.value = list
        }
    }
}
