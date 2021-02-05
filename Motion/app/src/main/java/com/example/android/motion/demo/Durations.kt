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

package com.example.android.motion.demo

/**
 * Animation durations.
 * @see [https://material.io/design/motion/speed.html#duration] for the detail.
 */

/**
 * Duration used in `FadeThroughActivity` for the `fadeThrough` transition of the layout
 * change caused by changing the visibility of `contact` to invisible and `cheese` to visible.
 */
const val MEDIUM_EXPAND_DURATION = 250L

/**
 * Duration used in `FadeThroughActivity` for the `fadeThrough` transition of the layout
 * change caused by changing the visibility of `contact` to visible and `cheese` to invisible.
 */
const val MEDIUM_COLLAPSE_DURATION = 200L

const val LARGE_EXPAND_DURATION = 300L
const val LARGE_COLLAPSE_DURATION = 250L
