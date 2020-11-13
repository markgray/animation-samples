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
package com.example.android.customtransition

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.transition.TransitionValues
import android.view.View
import android.view.ViewGroup

/**
 * The custom [Transition] used by `CustomTransitionFragment` to transition between scenes.
 */
class ChangeColor : Transition() {
    // BEGIN_INCLUDE (capture_values)
    /**
     * Convenience method: Add the background Drawable property value to the TransitionsValues.value
     * Map for a target under the key [PROPNAME_BACKGROUND].
     *
     * @param values The holder for any values that the [Transition] wishes to store.
     */
    private fun captureValues(values: TransitionValues) {
        // Capture the property values of views for later use
        values.values[PROPNAME_BACKGROUND] = values.view.background
    }

    /**
     * Captures the values in the start scene for the properties that this transition monitors.
     * These values are then passed as the `startValues` structure in a later call to [createAnimator].
     * The main concern for an implementation is what the properties are that the transition cares
     * about and what the values are for all of those properties. The start and end values will be
     * compared later during the [createAnimator] method to determine what, if any, animations,
     * should be run.
     *
     * Subclasses must implement this method. The method should only be called by the
     * transition system; it is not intended to be called from external classes.
     *
     * We just call our convenience method [captureValues] to add the background Drawable property
     * value of the `view` field of [transitionValues] to the `TransitionsValues.value` Map for the
     * target under the key [PROPNAME_BACKGROUND].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     * Values are stored in the `values` field of this [TransitionValues] object and are keyed from
     * a String value. For example, to store a view's rotation value, a transition might call
     * `transitionValues.values.put("appname:transitionname:rotation", view.getRotation())`.
     * The target view will already be stored in the [transitionValues] structure when this method
     * is called.
     */
    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    /**
     * Captures the values in the end scene for the properties that this transition monitors. These
     * values are then passed as the endValues structure in a later call to [createAnimator]. The
     * main concern for an implementation is what the properties are that the transition cares about
     * and what the values are for all of those properties. The start and end values will be compared
     * later during the [createAnimator] method to determine what, if any, animations, should be run.
     *
     * We just call our convenience method [captureValues] to add the background Drawable property
     * value of the `view` field of [transitionValues] to the `TransitionsValues.value` Map for the
     * target under the key [PROPNAME_BACKGROUND].
     *
     * @param transitionValues The holder for any values that the Transition wishes to store. Values
     * are stored in the `values` field of this [TransitionValues] object and are keyed from a String
     * value. For example, to store a view's rotation value, a transition might call:
     *
     *     transitionValues.values.put("appname:transitionname:rotation", view.getRotation()).
     *
     * The target view will already be stored in the transitionValues structure when this method is
     * called.
     */
    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    // END_INCLUDE (capture_values)
    // BEGIN_INCLUDE (create_animator)
    // Create an animation for each target that is in both the starting and ending Scene. For each
    // pair of targets, if their background property value is a color (rather than a graphic),
    // create a ValueAnimator based on an ArgbEvaluator that interpolates between the starting and
    // ending color. Also create an update listener that sets the View background color for each
    // animation frame
    /**
     * This method creates an animation that will be run for this transition given the information
     * in the [startValues] and [endValues] structures captured earlier for the start and end scenes.
     * Subclasses of Transition should override this method. The method should only be called by the
     * transition system; it is not intended to be called from external classes.
     *
     * This method is called by the transition's parent (all the way up to the topmost Transition in
     * the hierarchy) with the [sceneRoot] and start/end values that the transition may need to set
     * up initial target values and construct an appropriate animation. For example, if an overall
     * Transition is a `TransitionSet` consisting of several child transitions in sequence, then some
     * of the child transitions may want to set initial values on target views prior to the overall
     * Transition commencing, to put them in an appropriate state for the delay between that start
     * and the child Transition start time. For example, a transition that fades an item in may wish
     * to set the starting alpha value to 0, to avoid it blinking in prior to the transition actually
     * starting the animation. This is necessary because the scene change that triggers the Transition
     * will automatically set the end-scene on all target views, so a Transition that wants to animate
     * from a different value should set that value prior to returning from this method.
     *
     * Additionally, a Transition can perform logic to determine whether the transition needs to run
     * on the given target and start/end values. For example, a transition that resizes objects on
     * the screen may wish to avoid running for views which are not present in either the start or
     * end scenes.
     *
     * If there is an animator created and returned from this method, the transition mechanism will
     * apply any applicable duration, startDelay, and interpolator to that animation and start it.
     * A return value of `null` indicates that no animation should run. The default implementation
     * returns `null`.
     *
     * The method is called for every applicable target object, which is stored in the `view` field
     * of [TransitionValues].
     *
     * If either [startValues] or [endValues] is `null` we return `null` to the caller since this
     * transition can only be applied to views that are on both starting and ending scenes.
     *
     * Otherwise we initialize our [View] variable `val view` to the `view` field of [endValues] to
     * have a convenient reference to the target (both the starting and ending layout have the same
     * target). We initialize our [Drawable] variable `val startBackground` to the value stored in
     * the `value` map of [startValues] under the key [PROPNAME_BACKGROUND], and our [Drawable]
     * variable `val endBackground` to the value stored in the `value` map of [endValues] under
     * the key [PROPNAME_BACKGROUND]. If either `startBackground` or `endBackground` is not a
     * [ColorDrawable] we just ignore the target by falling through and returning `null` to the
     * caller. Otherwise we check whether the `color` of `startBackground` is equal to the `color`
     * of `endBackground` and if they we also just ignore the target by falling through and returning
     * `null` to the caller. When they are different we want to create a new Animator object to apply
     * to the targets as the transitions framework changes from the starting to the ending layout.
     * To do this we initialize our [ValueAnimator] variable `val animator` to an instance which
     * animates between Object values using an [ArgbEvaluator] as the `TypeEvaluator` that will
     * animate between the `color` of `startBackground` and the `color` of `endBackground`. Then
     * we add an [ValueAnimator.AnimatorUpdateListener] to `animator` whose lambda will change the
     * background color of the target every time the [ValueAnimator] produces a new frame in the
     * animation. Finally we return `animator` to the caller (I set the duration of `animator` to
     * 2000ms so that the animation is more noticeable).
     *
     * @param sceneRoot The root of the transition hierarchy.
     * @param startValues The values for a specific target in the start scene.
     * @param endValues The values for the target in the end scene.
     * @return A Animator to be started at the appropriate time in the
     * overall transition for this scene change. A null value means no animation
     * should be run.
     */
    override fun createAnimator(sceneRoot: ViewGroup,
                                startValues: TransitionValues?,
                                endValues: TransitionValues?
    ): Animator? {
        // This transition can only be applied to views that are on both starting and ending scenes.
        if (null == startValues || null == endValues) {
            return null
        }
        // Store a convenient reference to the target. Both the starting and ending layout have the
        // same target.
        val view: View = endValues.view
        // Store the object containing the background property for both the starting and ending
        // layouts.
        val startBackground = startValues.values[PROPNAME_BACKGROUND] as Drawable?
        val endBackground = endValues.values[PROPNAME_BACKGROUND] as Drawable?
        // This transition changes background colors for a target. It doesn't animate any other
        // background changes. If the property isn't a ColorDrawable, ignore the target.
        if (startBackground is ColorDrawable && endBackground is ColorDrawable) {
            // If the background color for the target in the starting and ending layouts is
            // different, create an animation.
            if (startBackground.color != endBackground.color) {
                // Create a new Animator object to apply to the targets as the transitions framework
                // changes from the starting to the ending layout. Use the class ValueAnimator,
                // which provides a timing pulse to change property values provided to it. The
                // animation runs on the UI thread. The Evaluator controls what type of
                // interpolation is done. In this case, an ArgbEvaluator interpolates between two
                // #argb values, which are specified as the 2nd and 3rd input arguments.
                val animator = ValueAnimator.ofObject(ArgbEvaluator(),
                        startBackground.color, endBackground.color)
                // Add an update listener to the Animator object.
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue
                    // Each time the ValueAnimator produces a new frame in the animation, change
                    // the background color of the target. Ensure that the value isn't null.
                    if (null != value) {
                        view.setBackgroundColor((value as Int))
                    }
                }
                // Return the Animator object to the transitions framework. As the framework changes
                // between the starting and ending layouts, it applies the animation you've created.
                return animator.setDuration(2000L)
            }
        }
        // For non-ColorDrawable backgrounds, we just return null, and no animation will take place.
        return null
    } // END_INCLUDE (create_animator)

    companion object {
        /**
         * Key to store a color value in TransitionValues object
         */
        private const val PROPNAME_BACKGROUND = "customtransition:change_color:background"
    }
}