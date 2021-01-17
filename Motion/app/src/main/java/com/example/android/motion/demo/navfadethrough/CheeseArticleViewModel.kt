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

package com.example.android.motion.demo.navfadethrough

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.motion.model.Cheese

/**
 * The [ViewModel] used by our [CheeseArticleFragment] fragment.
 */
class CheeseArticleViewModel : ViewModel() {

    /**
     * The [Cheese] object that [CheeseArticleFragment] should display. Private to prevent its
     * modification by other classes, public read-only access is provided by our [LiveData] wrapped
     * [Cheese] field [cheese]. It is set in a rather clever way by the setter for our [cheeseId]
     * field to the [Cheese] in the [Cheese.ALL] list whose `id` property is the same as the
     * argument passed to the setter of [cheeseId]. The setter of [cheeseId] is called from the
     * `onCreate` override of [CheeseArticleFragment] with the safe args [Long] value passed it
     * when it is navigated to from [CheeseCardFragment].
     */
    private val _cheese = MutableLiveData<Cheese?>()

    /**
     * Public read-only access to our [_cheese] field. An observer is added to it in the
     * `onViewCreated` override of [CheeseArticleFragment] which updates its UI to display
     * the [Cheese] when it changes to a non-`null` value.
     */
    val cheese: LiveData<Cheese?> = _cheese

    /**
     * The `id` property of the [Cheese] we are supposed to store in our [_cheese] field. Setting it
     * to a [Long] causes a search of the [Cheese.ALL] list for a [Cheese] with that value for its
     * `id` property, and the `value` of our [_cheese] field is then set to that [Cheese]. Our setter
     * is called from the `onCreate` override of [CheeseArticleFragment] with the safe args [Long]
     * value passed it when it is navigated to from [CheeseCardFragment].
     */
    var cheeseId: Long?
        get() = _cheese.value?.id
        set(value) {
            _cheese.value = Cheese.ALL.find { it.id == value }
        }

}
