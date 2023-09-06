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

package com.example.android.motion.demo.sharedelement

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionSet
import androidx.transition.TransitionValues

/**
 * Property name for a [MirrorView]. It is used as the contents of [MIRROR_PROPERTIES] which is
 * returned by the `getTransitionProperties` override of [SharedFade] to report the property names
 * that [SharedFade] cares about for the purposes of canceling overlapping animations. It is used
 * as the key of the [TransitionValues.values] map and is set to `true` in the `captureMirrorValues`
 * method if the [TransitionValues.view] is a [MirrorView].
 */
private const val PROPNAME_IS_MIRROR: String = "com.example.android.motion.demo:is_mirror"

/**
 * The set of property names that this transition cares about for the purposes of canceling
 * overlapping animations. It is returned by the `getTransitionProperties` override of [SharedFade]
 */
@Suppress("PrivatePropertyName")
private val MIRROR_PROPERTIES: Array<String> = arrayOf(PROPNAME_IS_MIRROR)

/**
 * Transitions between a view and its copy by [MirrorView].
 *
 * This can be typically used in a shared element transition where the shared element is necessary
 * only during the animation. The shared element needs to exist and laid out on both sides of the
 * transition in order to animate between them, but it can be wasteful to create the exact same view
 * on the side where it is not functional. This transition matches the substance and its mirror and
 * animates between them. Depending on which of the start or the end state is the substance of
 * [MirrorView], the animation either fades into it or fades out of it.
 *
 * This can be combined with other [Transition]s. For example, ChangeTransform can translate the
 * position of the substance view or the mirror view along with this transition.
 */
class SharedFade : Transition() {

    /**
     * Returns the set of property names used stored in the [TransitionValues] object passed into
     * [captureStartValues] that this transition cares about for the purposes of canceling overlapping
     * animations. When any transition is started on a given scene root, all transitions currently
     * running on that same scene root are checked to see whether the properties on which they based
     * their animations agree with the end values of the same properties in the new transition. If
     * the end values are not equal, then the old animation is canceled since the new transition will
     * start a new animation to these new values. If the values are equal, the old animation is allowed
     * to continue and no new animation is started for that transition.
     *
     * We just return the [Array] of [String] constant [MIRROR_PROPERTIES] since its [PROPNAME_IS_MIRROR]
     * contents is the only property name we care about for the purposes of canceling overlapping
     * animations.
     *
     * @return An array of property names as described in the class documentation for
     * [TransitionValues]. The default implementation returns `null`.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
    override fun getTransitionProperties(): Array<String>? {
        return MIRROR_PROPERTIES
    }

    /**
     * Convenience function called by our [captureStartValues] and [captureEndValues] overrides to
     * check whether the [View] in the [TransitionValues.view] field of the parameter [transitionValues]
     * is a [MirrorView], and if it is sets the value of the entry in the [TransitionValues.values]
     * field of [transitionValues] whose key is [PROPNAME_IS_MIRROR] to `true`.
     *
     * @param transitionValues the [TransitionValues] passed to [captureStartValues] or [captureEndValues]
     * which holds cached values for the [SharedFade] transition.
     */
    private fun captureMirrorValues(transitionValues: TransitionValues) {
        val view = transitionValues.view ?: return
        transitionValues.values[PROPNAME_IS_MIRROR] = view is MirrorView
    }

    /**
     * Captures the values in the start scene for the properties that this transition monitors.
     * These values are then passed as the `startValues` structure in a later call to [createAnimator].
     * The main concern for an implementation is what the properties are that the transition cares
     * about and what the values are for all of those properties. The start and end values will be
     * compared later during the [createAnimator] method to determine what, if any, animations,
     * should be run.
     *
     * We just call our [captureMirrorValues] method to have it store a `true` under the key
     * [PROPNAME_IS_MIRROR] in the [TransitionValues.values] field of our [TransitionValues]
     * parameter [transitionValues] iff the [View] in the [TransitionValues.view] field of
     * [transitionValues] is a [MirrorView].
     *
     * @param transitionValues The holder for any values that the Transition wishes to store. Values
     * are stored in the [TransitionValues.values] field of this [TransitionValues] object and are
     * keyed from a [String] value. For example, to store a view's rotation value, a transition might
     * call:
     *
     *     transitionValues.values.put(
     *              "appname:transitionname:rotation",
     *               view.getRotation()
     *     )
     *
     * The target view will already be stored in the [transitionValues] structure when this method
     * is called.
     */
    override fun captureStartValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    /**
     * Captures the values in the end scene for the properties that this transition monitors. These
     * values are then passed as the `endValues` structure in a later call to [createAnimator]. The
     * main concern for an implementation is what the properties are that the transition cares about
     * and what the values are for all of those properties. The start and end values will be compared
     * later during the [createAnimator] method to determine what, if any, animations, should be run.
     *
     * We just call our [captureMirrorValues] method to have it store a `true` under the key
     * [PROPNAME_IS_MIRROR] in the [TransitionValues.values] field of our [TransitionValues]
     * parameter [transitionValues] iff the [View] in the [TransitionValues.view] field of
     * [transitionValues] is a [MirrorView].
     *
     * @param transitionValues The holder for any values that the Transition wishes to store. Values
     * are stored in the [TransitionValues.values] field of this [TransitionValues] object and are
     * keyed from a [String] value. For example, to store a view's rotation value, a transition might
     * call:
     *
     *     transitionValues.values.put(
     *              "appname:transitionname:rotation",
     *               view.getRotation()
     *     )
     *
     * The target view will already be stored in the [transitionValues] structure when this method
     * is called.
     */
    override fun captureEndValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    /**
     * This method creates an animation that will be run for this transition given the information
     * in the [startValues] and [endValues] structures captured earlier for the start and end scenes.
     * Subclasses of [Transition] should override this method. The method should only be called by
     * the transition system; it is not intended to be called from external classes.
     *
     * This method is called by the transition's parent (all the way up to the topmost [Transition]
     * in the hierarchy) with the [sceneRoot] and start/end values that the transition may need to
     * set up initial target values and construct an appropriate animation. For example, if an
     * overall Transition is a [TransitionSet] consisting of several child transitions in sequence,
     * then some of the child transitions may want to set initial values on target views prior to
     * the overall [Transition] commencing, to put them in an appropriate state for the delay between
     * that start and the child Transition start time. For example, a transition that fades an item
     * in may wish to set the starting alpha value to 0, to avoid it blinking in prior to the
     * transition actually starting the animation. This is necessary because the scene change that
     * triggers the [Transition] will automatically set the end-scene on all target views, so a
     * [Transition] that wants to animate from a different value should set that value prior to
     * returning from this method.
     *
     * Additionally, a [Transition] can perform logic to determine whether the transition needs to
     * run on the given target and start/end values. For example, a transition that resizes objects
     * on the screen may wish to avoid running for views which are not present in either the start
     * or end scenes.
     *
     * If there is an animator created and returned from this method, the transition mechanism will
     * apply any applicable `duration`, `startDelay`, and `interpolator` to that animation and start
     * it. A return value of `null` indicates that no animation should run. The default implementation
     * returns `null`.
     *
     * The method is called for every applicable target object, which is stored in the
     * [TransitionValues.view] field.
     *
     * If either our [TransitionValues] parameter [startValues] or our [TransitionValues] parameter
     * [startValues] are `null` we return having done nothing. We initialize our `val startView` to
     * the [TransitionValues.view] field of [startValues] and return having done nothing if this is
     * `null`.  We initialize our `val endView` to the [TransitionValues.view] field of [endValues]
     * and return having done nothing if this is `null`. We then branch depending on whether either
     * of these views are a [MirrorView]:
     *  - `startView` is a [MirrorView] - The view is appearing. We animate the substance view by
     *  returning an [ObjectAnimator] of the [View.ALPHA] property of `endView` starting from 0f,
     *  and moving from 0f to 1f.
     *  - `endView` is a [MirrorView] - The view is disappearing. We mirror the substance view, and
     *  animate the [MirrorView] by setting the [MirrorView.substance] field of `endView` to
     *  `startView` and returning an [ObjectAnimator] of the [View.ALPHA] property of `endView`
     *  starting from 1f  and moving from 0f to 0f.
     *  - Neither view is a [MirrorView] - We ignore by returning `null`.
     *
     * @param sceneRoot   The root of the transition hierarchy.
     * @param startValues The values for a specific target in the start scene.
     * @param endValues   The values for the target in the end scene.
     * @return A [Animator] to be started at the appropriate time in the overall transition for this
     * scene change. A `null` value means no animation should be run.
     */
    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startView = startValues.view ?: return null
        val endView = endValues.view ?: return null
        if (startView is MirrorView) {
            // The view is appearing. We animate the substance view.
            // The MirrorView was used merely for matching the layout position by other Transitions.
            return ObjectAnimator.ofFloat(endView, View.ALPHA, 0f, 0f, 1f)
        } else if (endView is MirrorView) { // Disappearing
            // The view is disappearing. We mirror the substance view, and animate the MirrorView.
            endView.substance = startView
            return ObjectAnimator.ofFloat(endView, View.ALPHA, 1f, 0f, 0f)
        }
        return null
    }
}
