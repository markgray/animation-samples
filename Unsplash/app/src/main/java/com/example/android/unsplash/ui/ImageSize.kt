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

/**
 * Constants used to override the com.bumptech.glide.request.target.Target's width and height with
 * given values. Only [NORMAL] is actually used.
 */
object ImageSize {
    /**
     * The width in pixels and the height in pixels for `Glide` to use to load the jpeg.
     */
    @JvmField
    val NORMAL: IntArray = intArrayOf(480, 400)

    /**
     * The width in pixels and the height in pixels for `Glide` to use to load a large jpeg.
     */
    @Suppress("unused")
    val LARGE: IntArray = intArrayOf(960, 800)
}