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
 * Material design suggests that this be used as the expansion duration for Animated elements that
 * traverse a Medium portion of the screen.
 *
 * Duration used in `FadeThroughActivity` for the `fadeThrough` transition of the layout
 * change caused by changing the visibility of `contact` to invisible and `cheese` to visible.
 */
const val MEDIUM_EXPAND_DURATION: Long = 250L

/**
 * Material design suggests that this be used as the collapse duration for Animated elements that
 * traverse a Medium portion of the screen.
 *
 * Duration used in `FadeThroughActivity` for the `fadeThrough` transition of the layout
 * change caused by changing the visibility of `contact` to visible and `cheese` to invisible.
 */
const val MEDIUM_COLLAPSE_DURATION: Long = 200L

/**
 * Material design suggests that this be used as the expansion duration for Animated elements that
 * traverse a Large portion of the screen.
 *
 * It is used as the duration of the transition between `CheeseArticleFragment` and `CheeseCardFragment`
 * in the `NavFadeThroughActivity` demo, the transition between `CheeseGridFragment` and `CheeseDetailFragment`
 * in the `SharedElementActivity` demo, the duration of the "flashing placeholder" animation in the
 * `LoadingActivity` demo, and as the duration of the `Stagger` delayed Fade in animation used in the
 * `StaggerActivity` demo.
 */
const val LARGE_EXPAND_DURATION: Long = 300L

/**
 * Material design suggests that this be used as the collapse duration for Animated elements that
 * traverse a Large portion of the screen.
 *
 * It is used as the duration of the transition between `CheeseCardFragment` and `CheeseArticleFragment`
 * in the `NavFadeThroughActivity` demo, and the transition between `CheeseDetailFragment` and
 * `CheeseGridFragment` in the `SharedElementActivity` demo.
 */
const val LARGE_COLLAPSE_DURATION: Long = 250L
