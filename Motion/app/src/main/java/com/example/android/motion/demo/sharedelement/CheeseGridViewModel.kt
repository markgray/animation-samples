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

package com.example.android.motion.demo.sharedelement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.model.Cheese

/**
 * The [ViewModel] used by [CheeseGridFragment] to hold the data that it displays in its UI.
 */
class CheeseGridViewModel : ViewModel() {

    /**
     * The dataset used by [CheeseGridAdapter] to feed data into the [RecyclerView] of the grid
     * displayed by [CheeseGridFragment]. An observer is added to it in the `onViewCreated`
     * override of [CheeseGridFragment] whose lambda submits this [List] of [Cheese] objects to
     * the [CheeseGridAdapter] to be diffed and displayed whenever this changes value.
     */
    val cheeses: LiveData<List<Cheese>> = MutableLiveData(Cheese.ALL)

}
