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
import androidx.transition.TransitionSet

/**
 * Runs multiple [Transition]s sequentially.
 *
 * Setting a duration to this set will distribute the duration to each child transition based on its
 * weight. Interpolator is also segmented and applied to the transitions.
 *
 * (Background) A normal [TransitionSet] simply sets its properties to child transitions as they
 * are. This can be problematic for sequential transition sets. For example, setting a duration of
 * 300ms means that the entire duration will be the multiple of 300ms and the child count.
 */
class SequentialTransitionSet : TransitionSet() {

    init {
        /**
         * Here we set the play order of this set's child transitions to `ORDERING_SEQUENTIAL` (flag
         * used to indicate that the child transitions of this set should play in sequence).
         */
        ordering = ORDERING_SEQUENTIAL
    }

    /**
     * The total duration of this [SequentialTransitionSet]. It is set by our publicly accessible
     * [setDuration] method (aka kotlin `duration` property) and read by our [getDuration] method
     * (aka kotlin `duration` property), and our [distributeDuration] method "distributes" this
     * duration amongst our child transitions depending on the value of their entry in our [weights]
     * list.
     */
    private var _duration: Long = -1

    /**
     * The [TimeInterpolator] for our [TransitionSet]. Private so that we can override [setInterpolator]
     * with a custom setter for our `interpolator` property which does not call our super's version
     * of `setInterpolator` which would set all of the interpolators of our children to this same
     * value. Instead our override of [setInterpolator] calls our [distributeInterpolator] method to
     * assign a [SegmentInterpolator] to each of our children whose `end` is calculated as a fraction
     * of 1.0f based on its entry in our [weights] list divided by the sum of the entries in the
     * [weights] list for all of the children.
     * TODO: Weights different from 1.0f are only used in `SequentialTransitionSetTest`? Verify this.
     */
    private var _interpolator: TimeInterpolator? = null

    /**
     * The relative weight of each of our children [Transition] indexed by their position. The weight
     * of each of the children is used split our total duration amongst the children as well as to
     * split our [TimeInterpolator] field [_interpolator] into separate [SegmentInterpolator] for
     * each child whose end point is based on the child's weight divided by the sum of all of the
     * weights in [weights].
     */
    private val weights = mutableListOf<Float>()

    /**
     * Sets the play order of this set's child transitions. If our [ordering] parameter is not
     * [TransitionSet.ORDERING_SEQUENTIAL] we throw an [IllegalArgumentException] exception,
     * otherwise we return the value returned by our super's implementation of `setOrdering` which
     * is `this` [TransitionSet].
     *
     * @param ordering can only be [TransitionSet.ORDERING_SEQUENTIAL] to play the child transitions
     * in sequence, any other value will throw [IllegalArgumentException].
     * @return This [TransitionSet] object which is returned by our super's implementation
     */
    override fun setOrdering(ordering: Int): TransitionSet {
        if (ordering != ORDERING_SEQUENTIAL) {
            throw IllegalArgumentException(
                "SequentialTransitionSet only supports ORDERING_SEQUENTIAL"
            )
        }
        return super.setOrdering(ordering)
    }

    /**
     * Adds child [Transition] parameter [transition] to this set and assigns [weight] as its weight
     * in our [weights] list. The order in which this child transition is added relative to other
     * child transitions that are added determines the order in which the transitions are started.
     * First we call our super's implementation of `addTransition` to add our [Transition] parameter
     * [transition] to this [TransitionSet]. Then we add the [Float] parameter [weight] for this
     * [Transition] to our [weights] list and call [distributeDuration] to redistribute our duration
     * amongst our children based on their weights, and [distributeInterpolator] to "redistribute"
     * our interpolator in separate [SegmentInterpolator] objects for each child whose end point is
     * based on its weight in the [weights] list divided by the sum of all the weights in the list.
     * Finally we return `this` [TransitionSet] to our caller to allow chaining.
     *
     * @param transition A non-`null` child [Transition] to be added to this set.
     * @param weight the "weight" to give this [Transition] which will be used to distribute our
     * total duration amongst our children, as well as the end points of the interpolators assigned
     * to each child, so that the end points all add up to 1f.
     * @return This [TransitionSet] object.
     */
    fun addTransition(transition: Transition, weight: Float): TransitionSet {
        super.addTransition(transition)
        weights += weight
        distributeDuration()
        distributeInterpolator()
        return this
    }

    override fun addTransition(transition: Transition): TransitionSet {
        return addTransition(transition, 1f)
    }

    override fun setDuration(duration: Long): TransitionSet {
        // Don't call super.
        _duration = duration
        distributeDuration()
        return this
    }

    override fun getDuration(): Long {
        return _duration
    }

    private fun distributeDuration() {
        if (_duration < 0) {
            forEach { transition ->
                transition.duration = -1
            }
            return
        }
        val totalWeight = weights.sum()
        forEachIndexed { i, transition ->
            transition.duration = (_duration * weights[i] / totalWeight).toLong()
        }
    }

    override fun setInterpolator(interpolator: TimeInterpolator?): TransitionSet {
        // Don't call super.
        _interpolator = interpolator
        distributeInterpolator()
        return this
    }

    override fun getInterpolator(): TimeInterpolator? {
        return _interpolator
    }

    private fun distributeInterpolator() {
        val interpolator = _interpolator
        if (interpolator == null) {
            forEach { transition ->
                transition.interpolator = null
            }
            return
        }
        val totalWeight = weights.sum()
        var start = 0f
        forEachIndexed { i, transition ->
            val range = weights[i] / totalWeight
            transition.interpolator = SegmentInterpolator(
                interpolator,
                start,
                start + range
            )
            start += range
        }
    }
}
