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
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
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
        @SuppressLint("ObsoleteSdkInt") // The statement will serve to remind one if reusing code
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
     * [SUPPORTS_VIEW_OVERLAY] is `false` we return `null` having done nothing. Otherwise we set our
     * [Bitmap] variable `val startBitmap` to the [Bitmap] stored under the key [PROPNAME_BITMAP] in
     * the `values` property of our [TransitionValues] parameter [startValues] and our [Bitmap]
     * variable `val endBitmap` to the [Bitmap] stored under the key [PROPNAME_BITMAP] in the
     * `values` property of our [TransitionValues] parameter [endValues]. If `startBitmap` is the
     * same as `endBitmap` we also return `null` having done nothing. Otherwise we initialize our
     * [View] variable `val view` to the `view` property of [endValues], and our [BitmapDrawable]
     * variable `val startDrawable` to the drawable constructed from `startBitmap`, after we use
     * the `apply` extension function to set the bounding rectangle for the [Drawable] to be the
     * same width and height as `startBitmap`. We initialize our [ViewOverlay] variable `val overlay`
     * to the overlay for `view`, creating it if it does not yet exist, and add `startDrawable` to
     * the overlay (drawables added to the overlay will cause them to be displayed whenever the view
     * itself is redrawn).
     *
     * Finally we return a new instance of [ObjectAnimator] that animates the "alpha" property of
     * `startDrawable` between the int values 255 to 0, to which we add an action which will be
     * invoked when the animation has ended whose purpose is to remove `startDrawable` from the
     * [ViewOverlay] `overlay`.
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

        val view: View = endValues.view
        val startDrawable = BitmapDrawable(view.resources, startBitmap).apply {
            setBounds(0, 0, startBitmap.width, startBitmap.height)
        }

        // Use ViewOverlay to show the start bitmap on top of the view that is currently showing the
        // end state. This allows us to overlap the start and end states during the animation.
        val overlay: ViewOverlay = view.overlay
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
