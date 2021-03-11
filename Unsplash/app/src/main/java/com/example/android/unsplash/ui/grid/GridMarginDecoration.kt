/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.unsplash.ui.grid

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.android.unsplash.MainActivity
import com.example.android.unsplash.R

/**
 * The `setupRecyclerView` method of [MainActivity] adds an instance of this to its [RecyclerView]
 * constructed to use a [space] of [R.dimen.grid_item_spacing] (2dp) as an [RecyclerView.ItemDecoration]
 *
 * @param space
 */
class GridMarginDecoration(
    private val space: Int
    ) : ItemDecoration() {
    /**
     * Retrieve any offsets for the given item. Each field of [outRect] specifies the number of
     * pixels that the item view should be inset by, similar to padding or margin. The default
     * implementation sets the bounds of outRect to 0 and returns. We set all four fields of our
     * [Rect] parameter [outRect] (`left`, `top`, `right`, and `bottom`) to the value of [space]
     * with which we were constructed with.
     *
     * @param outRect [Rect] to receive the output.
     * @param view    The child view to decorate
     * @param parent  [RecyclerView] this `ItemDecoration` is decorating
     * @param state   The current state of [RecyclerView].
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.top = space
        outRect.right = space
        outRect.bottom = space
    }
}