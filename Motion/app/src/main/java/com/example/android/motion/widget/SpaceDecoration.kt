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

package com.example.android.motion.widget

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

/**
 * A [RecyclerView.ItemDecoration] allows the application to add a special drawing and layout offset
 * to specific item views from the adapter's data set. This can be useful for drawing dividers
 * between items, highlights, visual grouping boundaries and more. All `ItemDecorations` are drawn
 * in the order they were added, before the item views
 *
 * Put spaces between items. Used in the `onViewCreated` override of `CheeseGridFragment` to add the
 * spacing `R.dimen.spacing_tiny` to the item views in its [RecyclerView], and in the `onCreate`
 * override of `ReorderActivity` to add the spacing `R.dimen.spacing_small` to its [RecyclerView].
 *
 *  @param spacing The width of spaces in pixels.
 */
class SpaceDecoration(
    @param:Px
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    /**
     * Retrieve any offsets for the given item. Each field of [outRect] specifies the number of
     * pixels that the item view should be inset by, similar to padding or margin. The default
     * implementation sets the bounds of outRect to 0 and returns.
     *
     * If this ItemDecoration does not affect the positioning of item views, it should set all four
     * fields of [outRect] (left, top, right, bottom) to zero before returning.
     *
     * If you need to access the Adapter for additional data, you can call the method
     * [RecyclerView.getChildAdapterPosition] to get the adapter position of the View.
     *
     * We just set all four coordinates of our [Rect] parameter [outRect] to our field [spacing].
     *
     * @param outRect [Rect] to receive the output.
     * @param view    The child [View] to decorate
     * @param parent  [RecyclerView] this `ItemDecoration` is decorating
     * @param state   The current state of [RecyclerView].
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(spacing, spacing, spacing, spacing)
    }
}
