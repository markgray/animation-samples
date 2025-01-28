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

package com.example.android.motion.demo.dissolve

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * [ViewModel] used to drive the UI displayed by [DissolveActivity].
 */
class DissolveViewModel : ViewModel() {

    /**
     * Index into the [Cheese.IMAGES] array of resource IDs which points to the next drawable to
     * be displayed.
     */
    private val position = MutableLiveData(0)

    /**
     * Resource ID of the drawable to be displayed. An `Observer` is added to it in the `onCreate`
     * override of [DissolveActivity] whose lambda causes the `TransitionManager` to transition to
     * display the drawable with the new resource ID in the [ImageView] with ID `R.id.image` whenever
     * this property changes value. It is incremented by our [nextImage] method which is called from
     * the `OnClickListener` of both the `MaterialCardView` with ID `R.id.card` and the `MaterialButton`
     * with ID `R.id.next`.
     */
    val image: LiveData<Int> = position.map { p -> Cheese.IMAGES[p % Cheese.IMAGES.size] }

    /**
     * Increments our [position] property so that it points to the next drawable resource ID in the
     * [Cheese.IMAGES] array to be displayed in the UI of [DissolveActivity]. It is called by the
     * `OnClickListener` of both the `MaterialCardView` with ID `R.id.card` and the `MaterialButton`
     * with ID `R.id.next`.
     */
    fun nextImage() {
        position.value?.let { position.value = it + 1 }
    }
}
