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
import com.example.android.motion.model.Cheese

/**
 * [ViewModel] used by [CheeseDetailFragment] to hold its data.
 */
class CheeseDetailViewModel : ViewModel() {

    /**
     * The [Cheese] whose [Cheese.id] property is the same as the safe args that [CheeseDetailFragment]
     * is passed when it is navigated to from [CheeseGridFragment]. Private to prevent its modification
     * by the fragment, public read-only access is provided by our [cheese] property, and it is set
     * by setting our [cheeseId] property which causes it to search the [Cheese.ALL] list of [Cheese]
     * to find the first element whose [Cheese.id] property is the same as the value that [cheeseId]
     * is being set to and setting [_cheese] to that [Cheese].
     */
    private val _cheese = MutableLiveData<Cheese?>()

    /**
     * Public read only access to our [_cheese] property. An observer is added to it in the
     * `onViewCreated` override of [CheeseDetailFragment] which causes the [Cheese] to be displayed
     * in the UI.
     */
    val cheese: LiveData<Cheese?> = _cheese

    /**
     * Returns the [Cheese.id] property of our [_cheese] property when read, and when set will search
     * the [Cheese.ALL] list of [Cheese] to find the first element whose [Cheese.id] property is the
     * same as the value it is being set to and then set [_cheese] to that [Cheese].
     */
    var cheeseId: Long?
        get() = _cheese.value?.id
        set(value) {
            _cheese.value = Cheese.ALL.find { it.id == value }
        }

}
