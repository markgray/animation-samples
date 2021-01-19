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

package com.example.android.motion.demo.oscillation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.model.Cheese

/**
 * [ViewModel] used by [OscillationActivity] to hold its dataset.
 */
class OscillationViewModel : ViewModel() {

    /**
     * 15 shuffled [Cheese] objects from the [Cheese.ALL] list whose `name` property is shorter than
     * 10 characters. It is used to provide data for the adapter of the [RecyclerView] in the UI of
     * [OscillationActivity]. An observer is added to it in the `onCreate` override of
     * [OscillationActivity] whose lambda submits this list to the [CheeseAdapter] adapter of that
     * [RecyclerView] to be diffed, and displayed.
     */
    val cheeses: LiveData<List<Cheese>> = MutableLiveData(Cheese.ALL.filter {
        it.name.length < 10
    }.shuffled().take(15))
}
