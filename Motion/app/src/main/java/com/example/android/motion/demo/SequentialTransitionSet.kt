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
     * assign a [SegmentInterpolator] wrapped version of our [_interpolator] to each of our children
     * whose range is calculated as a fraction of 1.0f based on its entry in our [weights] list
     * divided by the sum of the entries in the [weights] list for all of the children, and whose
     * `start` begins where the previous child ended and whose `end` is its `start` plus its range.
     * Note: Weights different from 1f are only used in `SequentialTransitionSetTest`
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

    /**
     * Adds child [Transition] parameter [transition] to this set. The order in which this child
     * transition is added relative to other child transitions that are added determines the order
     * in which the transitions are started. We return the [TransitionSet] returned by our two
     * argument version of `addTransition` when we have it add [transition] to our set with a weight
     * of 1f.
     *
     * @param transition A non-`null` child [Transition] to be added to this set.
     * @return This [TransitionSet] object.
     */
    override fun addTransition(transition: Transition): TransitionSet {
        return addTransition(transition, 1f)
    }

    /**
     * Sets the total duration of our [TransitionSet] animation to its [Long] parameter [duration].
     * We set our [_duration] field to our parameter [duration] then call our [distributeDuration]
     * method to have it distribute this duration amongst our [Transition] children according to
     * their weight in our [weights] list. Finally we return `this` [TransitionSet] to the caller
     * to allow chaining.
     *
     * @param duration The length of the animation, in milliseconds.
     * @return This [TransitionSet] object.
     */
    override fun setDuration(duration: Long): TransitionSet {
        // Don't call super.
        _duration = duration
        distributeDuration()
        return this
    }

    /**
     * Returns the duration set on this transition. If no duration has been set, the returned value
     * will be negative. We just return our [Long] field [_duration] to the caller.
     *
     * @return The duration set on this transition, in milliseconds, if one has been
     * set, otherwise returns a negative number.
     */
    override fun getDuration(): Long {
        return _duration
    }

    /**
     * Distributes our duration (contents of our [_duration] field) between our [Transition] children
     * according to their weight in our [weights] list. If [_duration] is less than 0 we just set the
     * duration of all of our children to -1 and return. Otherwise we initialize our [Float] variable
     * `val totalWeight` to the sum of all of the weights of our children recorded in our [weights]
     * list. Then we loop using our [TransitionSet.forEachIndexed] extension function setting the
     * duration of each [Transition] in our [TransitionSet] to [_duration] times its entry in our
     * [weights] list divided by `totalWeight`.
     */
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

    /**
     * Sets the interpolator of this [TransitionSet]. We set our [_interpolator] field to our
     * [TimeInterpolator] parameter [interpolator] then call our [distributeInterpolator] method
     * to have it distribute this interpolator amongst our [Transition] children according to their
     * weight in our [weights] list. Finally we return `this` [TransitionSet] to the caller to
     * allow chaining.
     *
     * @param interpolator The time interpolator used by the transition
     * @return This [TransitionSet] object.
     */
    override fun setInterpolator(interpolator: TimeInterpolator?): TransitionSet {
        // Don't call super.
        _interpolator = interpolator
        distributeInterpolator()
        return this
    }

    /**
     * Returns the [TimeInterpolator] set on this transition. If no interpolator has been set, the
     * returned value will be `null`. We just return the contents of our [_interpolator] field to
     * the caller.
     *
     * @return The interpolator set on this transition, if one has been set, otherwise
     * returns `null`.
     */
    override fun getInterpolator(): TimeInterpolator? {
        return _interpolator
    }

    /**
     * Distributes the [TimeInterpolator] contained in our field [_interpolator] amongst our
     * [Transition] children according to their weight in our [weights] list by constructing a
     * [SegmentInterpolator] wrapped version of our [_interpolator] whose range is calculated as
     * a fraction of 1.0f based on its entry in our [weights] list divided by the sum of the entries
     * in the [weights] list for all of the children, and whose `start` begins where the previous
     * child ended and whose `end` is its `start` plus its range.
     *
     * We initialize our [TimeInterpolator] variable `val interpolator` to our [_interpolator] field
     * and if this is `null` we just loop through all or our [Transition] children setting their
     * `interpolator` to `null` and then return. Otherwise we initialize our [Float] variable
     * `val totalWeight` to the sum of all of the weights of our children recorded in our [weights]
     * list and initialize our variable `var start` to 0f. Then we use our [TransitionSet.forEachIndexed]
     * extension function to loop through all of our [Transition] children setting our variable
     * `val range` to the child's entry in our [weights] list divided by `totalWeight`, then setting
     * the `interpolator` of the child to a new instance of [SegmentInterpolator] constructed to
     * wrap our `interpolator` with a start value of `start` and an `end` value of `start` plus
     * `range`. We add `range` to `start` and loop around to handle the next child.
     */
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
