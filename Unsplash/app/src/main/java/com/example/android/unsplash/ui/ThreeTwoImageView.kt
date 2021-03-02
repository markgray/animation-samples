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
package com.example.android.unsplash.ui

import android.content.Context
import android.util.AttributeSet
import com.example.android.unsplash.DetailActivity
import com.example.android.unsplash.data.model.Photo

/**
 * A custom [ForegroundImageView] which calculates its height to be 2/3 of its width, it is used to
 * display the jpeg of the [Photo] object which is displayed by [DetailActivity] in its layout file
 * layout/detail_view.xml
 *
 * @param context The [Context] the view is running in, through which it can access the
 * current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 */
class ThreeTwoImageView(
    context: Context?,
    attrs: AttributeSet?
) : ForegroundImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val desiredHeight = width * 2 / 3
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY)
        )
    }
}