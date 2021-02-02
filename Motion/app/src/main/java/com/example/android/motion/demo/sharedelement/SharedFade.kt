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
 *
 */
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
    @Suppress("RedundantNullableReturnType")
    override fun getTransitionProperties(): Array<String>? {
        return MIRROR_PROPERTIES
    }

    private fun captureMirrorValues(transitionValues: TransitionValues) {
        val view = transitionValues.view ?: return
        transitionValues.values[PROPNAME_IS_MIRROR] = view is MirrorView
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

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
