/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.activityscenetransitionbasic

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * [android.widget.FrameLayout] which forces itself to be laid out as square.
 *
 * @param context The Context the view is running in, through which it can access the current theme,
 * resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 * @param defStyleAttr An attribute in the current theme that contains a reference to a style
 * resource that supplies default values for the view. Can be 0 to not look for defaults.
 */
class SquareFrameLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context!!, attrs, defStyleAttr) {
    /**
     * Measure the view and its content to determine the measured width and the measured height.
     * This method is invoked by [measure] and should be overridden by subclasses to provide accurate
     * and efficient measurement of their contents.
     *
     * **CONTRACT:** When overriding this method, you *must* call [setMeasuredDimension] to store
     * the measured width and height of this view. Failure to do so will trigger an
     * [IllegalStateException], thrown by [measure]. Calling the superclass' [onMeasure] is a valid
     * use.
     *
     * First we initialize our variable `val widthSize` by extracting the size from our measure
     * specification parameter [widthMeasureSpec] (width in pixels), and our variable `val heightSize`
     * by extracting the size from our measure specification parameter [heightMeasureSpec] (height
     * in pixels). If these are both 0 that means there are no constraints on size so we call our
     * super's implementation of `onMeasure` to let it measure. We then initialize our variable
     * `val minSize` to smallest of the measured dimensions for both dimensions. We then call
     * [setMeasuredDimension] to store `minSize` as both the measured width and measured height,
     * and return to the caller
     *
     * If on the other hand only one of the size constraints is non-zero we initialize our variable
     * `val size` to the size that does have a constraint, and if both size constraints are non-zero
     * we initialize `size` to the smallest of the two constraints. We then initialize our variable
     * `val newMeasureSpec` to a `MeasureSpec` whose exact size is `size`, and then call our super's
     * implementation of `onMeasure` with both horizontal and vertical space requirements set to
     * `newMeasureSpec`.
     *
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent. The requirements
     * are encoded with [android.view.View.MeasureSpec]
     * @param heightMeasureSpec vertical space requirements as imposed by the parent. The requirements
     * are encoded with [android.view.View.MeasureSpec].
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSize == 0 && heightSize == 0) {
            // If there are no constraints on size, let FrameLayout measure
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            // Now use the smallest of the measured dimensions for both dimensions
            val minSize = measuredWidth.coerceAtMost(measuredHeight)
            setMeasuredDimension(minSize, minSize)
            return
        }
        val size: Int = if (widthSize == 0 || heightSize == 0) {
            // If one of the dimensions has no restriction on size, set both dimensions to be the
            // one that does
            widthSize.coerceAtLeast(heightSize)
        } else {
            // Both dimensions have restrictions on size, set both dimensions to be the
            // smallest of the two
            widthSize.coerceAtMost(heightSize)
        }
        val newMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(newMeasureSpec, newMeasureSpec)
    }
}