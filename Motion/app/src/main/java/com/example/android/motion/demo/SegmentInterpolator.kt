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

import android.animation.TimeInterpolator
import androidx.transition.Transition

/**
 * Takes a [base] interpolator and extracts out a segment from it as a new [TimeInterpolator].
 *
 * This is useful for sequential animations where each of the child animations should be
 * interpolated so that they match with another animation when combined. Used in our class
 * [SequentialTransitionSet].
 *
 * @param base the [TimeInterpolator] which is being "distributed" between each [Transition] in the
 * [SequentialTransitionSet] by the method [SequentialTransitionSet.distributeInterpolator]
 * @param start the start point for this [SegmentInterpolator] interpolator, between 0 and 1.0
 * @param end the end point for this [SegmentInterpolator] interpolator, between 0 and 1.0
 */
class SegmentInterpolator(
    val base: TimeInterpolator,
    val start: Float = 0f,
    val end: Float = 1f
) : TimeInterpolator {

    /**
     * The interpolation value of the animation of the [TimeInterpolator] in [base] we are contructed
     * to wrap which corresponds to our [start] point.
     */
    private val offset = base.getInterpolation(start)

    /**
     * The "length" of this segment, ie. the animation distance traversed when this [TimeInterpolator]
     * is run from [start] to [end], between 0 and 1.0
     */
    private val xRatio = (end - start) / 1f

    /**
     * The animation distance traversed when the [TimeInterpolator] in [base] we are contructed to
     * wrap reaches our [end] point minus the animation distance traversed when this [TimeInterpolator]
     * is run
     */
    private val yRatio = (base.getInterpolation(end) - offset) / 1f

    /**
     * Maps a value representing the elapsed fraction of an animation to a value that represents
     * the interpolated fraction. This interpolated value is then multiplied by the change in
     * value of an animation to derive the animated value at the current elapsed animation time.
     * We calculate the interpolation value to return by adding our [start] value to [input] times
     * [xRatio] (the distance traveled at our current point in the animation) then getting the
     * interpolation value of the [TimeInterpolator] in [base] we are contructed to wrap of this
     * calculated current point. From the interpolation value returned we subtract [offset] (the
     * interpolation value of the animation of the [TimeInterpolator] in [base] we are contructed
     * to wrap which corresponds to our [start] point) and then divide that result by [yRatio] and
     * return the resulting quotient to the caller.
     *
     * @param input A value between 0 and 1.0 indicating our current point in the animation where
     * 0 represents the start and 1.0 represents the end
     * @return The interpolation value. This value can be more than 1.0 for interpolators which
     * overshoot their targets, or less than 0 for interpolators that undershoot their targets.
     */
    override fun getInterpolation(input: Float): Float {
        return (base.getInterpolation(start + (input * xRatio)) - offset) / yRatio
    }
}
