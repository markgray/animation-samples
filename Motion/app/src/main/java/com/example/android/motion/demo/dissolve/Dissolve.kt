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

package com.example.android.motion.demo.dissolve

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.ViewGroup
import android.view.ViewOverlay
import androidx.core.animation.doOnEnd
import androidx.core.view.drawToBitmap
import androidx.transition.Transition
import androidx.transition.TransitionValues

/**
 * Dissolve animation pattern implemented as a [Transition].
 *
 * A dissolve creates a smooth transition between elements that completely overlap one another,
 * such as photos inside a card or other container. A foreground element fades in (appears) or out
 * (disappears) to show or hide an element behind it.
 *
 * See
 * [Expressing continuity](https://material.io/design/motion/understanding-motion.html#expressing-continuity)
 * for the detail.
 */
class Dissolve : Transition() {

    companion object {
        /**
         * Key under which we store the [Bitmap] in the [TransitionValues] map passed to our method
         * [captureValues]. Our [createAnimator] override is passed the [TransitionValues] for both
         * the start and the end of the [Transition].
         */
        private const val PROPNAME_BITMAP = "com.example.android.motion.demo.dissolve:bitmap"

        /**
         * This transition depends on [ViewOverlay] to show the animation. On older devices that
         * don't support it, this transition doesn't do anything.
         */
        @SuppressLint("ObsoleteSdkInt")
        private val SUPPORTS_VIEW_OVERLAY = Build.VERSION.SDK_INT >= 18
    }

    /**
     * Captures the values in the start scene for the properties that this transition monitors.
     * These values are then passed as the startValues structure in a later call to [createAnimator].
     * The main concern for an implementation is what the properties are that the transition cares
     * about and what the values are for all of those properties. The start and end values will be
     * compared later during the [createAnimator] method to determine what, if any, animations,
     * should be run. We just call our [captureValues] method with our [TransitionValues] parameter
     * [transitionValues].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     * Values are stored in the `values` field of this [TransitionValues] object and are keyed from
     * a String value. The target view will already be stored in the [transitionValues] structure
     * when this method is called.
     */
    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    /**
     * Captures the values in the end scene for the properties that this transition monitors. These
     * values are then passed as the `endValues` structure in a later call to [createAnimator]. The
     * main concern for an implementation is what the properties are that the transition cares about
     * and what the values are for all of those properties. The start and end values will be compared
     * later during the [createAnimator] method to determine what, if any, animations, should be run.
     * We just call our [captureValues] method with our [TransitionValues] parameter [transitionValues].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     * Values are stored in the `values` field of this [TransitionValues] object and are keyed from
     * a String value. The target view will already be stored in the [transitionValues] structure
     * when this method is called.
     */
    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    /**
     * Captures a [Bitmap] of the `view` field of the [TransitionValues] parameter [transitionValues]
     * passed us, and stores it in the `values` field of [transitionValues] under the key
     * [PROPNAME_BITMAP].
     *
     * @param transitionValues the [TransitionValues] in which we should store the [Bitmap] of the
     * view that is being animated.
     */
    private fun captureValues(transitionValues: TransitionValues) {
        if (SUPPORTS_VIEW_OVERLAY) {
            // Store the current appearance of the view as a Bitmap.
            transitionValues.values[PROPNAME_BITMAP] = transitionValues.view.drawToBitmap()
        }
    }

    /**
     * This method creates an animation that will be run for this transition given the information
     * in the [startValues] and [endValues] structures captured earlier for the start and end scenes.
     * If either of our [TransitionValues] parameters [startValues] or [endValues] are `null` or
     * [SUPPORTS_VIEW_OVERLAY] is `false` we return `null` having done nothing.
     *
     * @param sceneRoot   The root of the transition hierarchy.
     * @param startValues The values for a specific target in the start scene.
     * @param endValues   The values for the target in the end scene.
     * @return An [Animator] to be started at the appropriate time in the overall transition for
     * this scene change. A `null` value means no animation should be run.
     */
    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null || !SUPPORTS_VIEW_OVERLAY) {
            return null
        }
        val startBitmap = startValues.values[PROPNAME_BITMAP] as Bitmap
        val endBitmap = endValues.values[PROPNAME_BITMAP] as Bitmap

        // No need to animate if the start and the end look identical.
        if (startBitmap.sameAs(endBitmap)) {
            return null
        }

        val view = endValues.view
        val startDrawable = BitmapDrawable(view.resources, startBitmap).apply {
            setBounds(0, 0, startBitmap.width, startBitmap.height)
        }

        // Use ViewOverlay to show the start bitmap on top of the view that is currently showing the
        // end state. This allows us to overlap the start and end states during the animation.
        val overlay = view.overlay
        overlay.add(startDrawable)

        // Fade out the start bitmap.
        return ObjectAnimator
            // Use [BitmapDrawable#setAlpha(int)] to animate the alpha value.
            .ofInt(startDrawable, "alpha", 255, 0).apply {
                doOnEnd {
                    // Remove the start state from the overlay when the animation is over.
                    // The drawable is completely transparent at this point, but we don't want to
                    // leave it there.
                    overlay.remove(startDrawable)
                }
            }
    }
}
