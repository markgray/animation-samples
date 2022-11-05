/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.unsplash.transition

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.transition.Transition
import android.transition.TransitionSet
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOverlay
import android.widget.TextView
import kotlin.math.roundToInt

/**
 * Transitions a TextView from one font size to another. This does not
 * do any animation of TextView content and if the text changes, this
 * transition will not run.
 *
 * The animation works by capturing a bitmap of the text at the start
 * and end states. It then scales the start bitmap until it reaches
 * a threshold and switches to the scaled end bitmap for the remainder
 * of the animation. This keeps the jump in bitmaps in the middle of
 * the animation, where it is less noticeable than at the beginning
 * or end of the animation. This transition does not work well with
 * cropped text. TextResize also does not work with changes in
 * TextView gravity.
 *
 * This class is used as a [Transition] in the file transition/shared_main_detail.xml which is
 * used as part of the "App.Details" style in values/styles.xml as an element with the item name
 * "android:windowSharedElementEnterTransition"
 */
class TextResize : Transition {
    /**
     * Adds the [TextView] Class as a target view that this Transition is interested in animating.
     * By default, there are no targetTypes, and a Transition will listen for changes on every view
     * in the hierarchy below the sceneRoot of the Scene being transitioned into. Setting targetTypes
     * constrains the Transition to only listen for, and act on, views with these classes. Views with
     * different classes will be ignored.
     */
    @Suppress("unused") // Unused but instructional
    constructor() {
        addTarget(TextView::class.java)
    }

    /**
     * Constructor used from XML. Adds the [TextView] Class as a target view that this Transition is
     * interested in animating. By default, there are no targetTypes, and a Transition will listen
     * for changes on every view in the hierarchy below the sceneRoot of the Scene being transitioned
     * into. Setting targetTypes constrains the Transition to only listen for, and act on, views with
     * these classes. Views with different classes will be ignored.
     */
    @Suppress("unused") // Unused but instructional
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        addTarget(TextView::class.java)
    }

    /**
     * Returns the set of property names used stored in the [TransitionValues] object passed into
     * [captureStartValues] that this transition cares about for the purposes of canceling overlapping
     * animations. When any transition is started on a given scene root, all transitions currently
     * running on that same scene root are checked to see whether the properties on which they based
     * their animations agree with the end values of the same properties in the new transition. If
     * the end values are not equal, then the old animation is canceled since the new transition will
     * start a new animation to these new values. If the values are equal, the old animation is
     * allowed to continue and no new animation is started for that transition.
     *
     * A transition does not need to override this method. However, not doing so will mean that the
     * cancellation logic outlined in the previous paragraph will be skipped for that transition,
     * possibly leading to artifacts as old transitions and new transitions on the same targets run
     * in parallel, animating views toward potentially different end values.
     *
     * We return our array [PROPERTIES] which contains only the [String] constant [FONT_SIZE] since
     * we only care about [FONT_SIZE] ("TextResize:fontSize").
     *
     * @return An array of property names as described in the class documentation for
     * [TransitionValues]. The default implementation returns `null`.
     */
    override fun getTransitionProperties(): Array<String> {
        return PROPERTIES
    }

    /**
     * Captures the values in the start scene for the properties that this transition monitors. These
     * values are then passed as the `startValues` structure in a later call to [createAnimator].
     * The main concern for an implementation is what the properties are that the transition cares
     * about and what the values are for all of those properties. The start and end values will be
     * compared later during the [createAnimator] method to determine what, if any, animations,
     * should be run.
     *
     * Subclasses must implement this method. The method should only be called by the transition
     * system; it is not intended to be called from external classes.
     *
     * We just call our [captureValues] method with our [TransitionValues] parameter [transitionValues]
     * to have it store all of the properties of the `view` [TextView] of [transitionValues] that we
     * are interested in inside of [transitionValues].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     * Values are stored in the `values` field of this [TransitionValues] object and are keyed from
     * a [String] value. For example, to store a view's rotation value, a transition might call:
     *
     *     transitionValues.values.put(
     *         "appname:transitionname:rotation",
     *         view.getRotation()
     *     )
     *
     * The target view will already be stored in the [transitionValues] structure when this method
     * is called.
     */
    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    /**
     * Captures the values in the end scene for the properties that this transition monitors. These
     * values are then passed as the `endValues` structure in a later call to [createAnimator].
     * The main concern for an implementation is what the properties are that the transition cares
     * about and what the values are for all of those properties. The start and end values will be
     * compared later during the [createAnimator] method to determine what, if any, animations,
     * should be run.
     *
     * Subclasses must implement this method. The method should only be called by the transition
     * system; it is not intended to be called from external classes.
     *
     * We just call our [captureValues] method with our [TransitionValues] parameter [transitionValues]
     * to have it store all of the properties of the `view` [TextView] of [transitionValues] that we
     * are interested in inside of [transitionValues].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     * Values are stored in the `values` field of this [TransitionValues] object and are keyed from
     * a [String] value. For example, to store a view's rotation value, a transition might call:
     *
     *     transitionValues.values.put(
     *         "appname:transitionname:rotation",
     *         view.getRotation()
     *     )
     *
     * The target view will already be stored in the [transitionValues] structure when this method
     * is called.
     */
    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    /**
     * Called from both [captureStartValues] and [captureEndValues] to have us store all of the
     * properties of the `view` [TextView] of [TransitionValues] parameter [transitionValues] that
     * we are interested in inside of [transitionValues]. First we make sure that the `view` of
     * our [TransitionValues] parameter [transitionValues] is a [TextView] and if it is not we
     * return having done nothing. Otherwise we initialize our [TextView] variable `val view` to
     * the `view` of [transitionValues], and initialize our [Float] variable `val fontSize` to the
     * text size of `view`. We then store `fontSize` under the key [FONT_SIZE] in the `values` field
     * of [transitionValues]. Next we initialize our [TextResizeData] variable `val data` to a new
     * instance constructed to capture all the non-font-size data of `view` used by our [TextResize]
     * transition, and then store `data` under the key [DATA] in the `values` field of
     * [transitionValues].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     */
    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view !is TextView) {
            return
        }
        val view: TextView = transitionValues.view as TextView
        val fontSize: Float = view.textSize
        transitionValues.values[FONT_SIZE] = fontSize
        val data = TextResizeData(view)
        transitionValues.values[DATA] = data
    }

    /**
     * This method creates an animation that will be run for this [Transition] given the information
     * in the [TransitionValues] parameters [startValues] and [endValues] structures captured earlier
     * for the start and end scenes. Subclasses of [Transition] should override this method. The
     * method should only be called by the transition system -- it is not intended to be called from
     * external classes.
     *
     * This method is called by the transition's parent (all the way up to the topmost [Transition]
     * in the hierarchy) with the [sceneRoot] and start/end values that the transition may need to
     * set up initial target values and construct an appropriate animation. For example, if an
     * overall [Transition] is a [TransitionSet] consisting of several child transitions in sequence,
     * then some of the child transitions may want to set initial values on target views prior to
     * the overall [Transition] commencing, to put them in an appropriate state for the delay between
     * that start and the child [Transition] start time. For example, a transition that fades an item
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
     * If there is an [Animator] created and returned from this method, the transition mechanism will
     * apply any applicable duration, startDelay, and interpolator to that animation and start it.
     * A return value of `null` indicates that no animation should run. The default implementation
     * returns `null`.
     *
     * The method is called for every applicable target object, which is stored in the
     * [TransitionValues.view] field.
     *
     * If either of our [TransitionValues] parameters [startValues] or [endValues] is `null` we
     * return `null` having done nothing. Otherwise we initialize our [TextResizeData] variable
     * `val startData` to the value stored under the key [DATA] in the `values` field of [startValues]
     * and our [TextResizeData] variable `val endData` to the value stored under the key [DATA] in the
     * `values` field of [endValues]. If the `gravity` property of `startData` is not equal to the
     * `gravity` property of `endData` we return `null` since we cannot deal with changes in gravity.
     *
     * Next we initialize our [TextView] variable `val textView` to the `view` field of [endValues],
     * and our [Float] variable `var startFontSize` to the value stored under the key [FONT_SIZE] in
     * the `values` field of [startValues]. We then call our method [setTextViewData] to have it
     * set the properties of `textView` (the end `view` recall) to the properties stored in our
     * [TextResizeData] variable `startData` (from the start `view` recall), and the font size to
     * our variable `startFontSize` (the font size of the start `view` recall). We initialize our
     * [Float] variable `val endWidth` to the width of the text in `textView`, and initialize our
     * [Bitmap] variable `val endBitmap` to the [Bitmap] that our [captureTextBitmap] method captures
     * from `textView`, and if this is `null` we set `endFontSize` to 0f. Then if both `startFontSize`
     * and `endFontSize` are `null` we return `null` since we cannot animate null bitmaps.
     *
     * We save the `textColors`, `hintTextColors`, `highlightColor` and `linkTextColors` properties
     * of `textView` in local variables of the same name then set these properties all to the color
     * [Color.TRANSPARENT] (we do this so that nothing is drawn -- we will only draw the bitmaps in
     * the overlay).
     *
     * Next we create a [SwitchBitmapDrawable] to initialize our variable `val drawable` that we will
     * use to scale the start and end bitmaps and switch between them at the appropriate progress,
     * and fetch or create a [ViewOverlay] for `textView` and add `drawable` to it.
     *
     * We next create [PropertyValuesHolder]'s for animating the "left", "top", "right", "bottom",
     * and "fontSize" from their values found in `startData` to their values found in `endData` and
     * initialze our variables `val leftProp`, `val topProp`, `val rightProp`, `val bottomProp`, and
     * `val fontSizeProp` respectively. We then initialize our [ObjectAnimator] variable `val animator`
     * to an instance which will animate all these [PropertyValuesHolder]'s as well as another one
     * `val textColorProp` which will animate the "textColor" property using a [ArgbEvaluator] if
     * and only if the `textColor` of `startData` is not equal to the `textColor` of `endData`.
     *
     * Next we initialize our variable `val finalFontSize` to `endFontSize` and initialize our
     * [AnimatorListenerAdapter] variable `val listener` to an anonymous class with the overrides:
     *  - `onAnimationEnd` - which is called at the end of the animation, where we remove `drawable`
     *  as an overlay and restore the `textColors`, `hintTextColors`, `highlightColor` and
     *  `linkTextColors` properties of `textView` to the values we saved in our local variables.
     *  - `onAnimationPause` - called when the animation is paused, where we set various properties
     *  of `textView` to the current values of `drawable`.
     *  -onAnimationResume - called when the animation is resumed, after being previously paused,
     *  where we set various properties of `textView` to the values found in `endData`.
     *
     * Next we add `listener` as an [Animator.AnimatorListener] to `animator`, and also add it as an
     * [Animator.AnimatorPauseListener] to `animator`. Finally we return `animator` to the caller.
     *
     * @param sceneRoot The root of the transition hierarchy.
     * @param startValues The values for a specific target in the start scene.
     * @param endValues The values for the target in the end scene.
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
        val startData = startValues.values[DATA] as TextResizeData?
        val endData = endValues.values[DATA] as TextResizeData?
        if ((startData ?: return null).gravity != (endData ?: return null).gravity) {
            return null // Can't deal with changes in gravity
        }
        val textView = endValues.view as TextView
        var startFontSize = startValues.values[FONT_SIZE] as Float
        // Capture the start bitmap -- we need to set the values to the start values first
        setTextViewData(textView, startData, startFontSize)
        val startWidth = textView.paint.measureText(textView.text.toString())
        val startBitmap = captureTextBitmap(textView)
        if (startBitmap == null) {
            startFontSize = 0f
        }
        var endFontSize = endValues.values[FONT_SIZE] as Float

        // Set the values to the end values
        setTextViewData(textView, endData, endFontSize)
        val endWidth = textView.paint.measureText(textView.text.toString())

        // Capture the end bitmap
        val endBitmap = captureTextBitmap(textView)
        if (endBitmap == null) {
            endFontSize = 0f
        }
        if (startFontSize == 0f && endFontSize == 0f) {
            return null // Can't animate null bitmaps
        }

        // Set the colors of the TextView so that nothing is drawn.
        // Only draw the bitmaps in the overlay.
        val textColors = textView.textColors
        val hintColors = textView.hintTextColors
        val highlightColor = textView.highlightColor
        val linkColors = textView.linkTextColors
        textView.setTextColor(Color.TRANSPARENT)
        textView.setHintTextColor(Color.TRANSPARENT)
        textView.highlightColor = Color.TRANSPARENT
        textView.setLinkTextColor(Color.TRANSPARENT)

        // Create the drawable that will be animated in the TextView's overlay.
        // Ensure that it is showing the start state now.
        val drawable = SwitchBitmapDrawable(
            textView,
            startData.gravity,
            startBitmap,
            startFontSize,
            startWidth,
            endBitmap,
            endFontSize,
            endWidth
        )
        textView.overlay.add(drawable)

        // Properties: left, top, right, bottom, fontSize, and text color
        val leftProp = PropertyValuesHolder.ofFloat(
            "left",
            startData.paddingLeft.toFloat(),
            endData.paddingLeft.toFloat()
        )
        val topProp = PropertyValuesHolder.ofFloat(
            "top",
            startData.paddingTop.toFloat(),
            endData.paddingTop.toFloat()
        )
        val rightProp = PropertyValuesHolder.ofFloat(
            "right",
            (startData.width - startData.paddingRight).toFloat(),
            (endData.width - endData.paddingRight).toFloat()
        )
        val bottomProp = PropertyValuesHolder.ofFloat(
            "bottom",
            (startData.height - startData.paddingBottom).toFloat(),
            (endData.height - endData.paddingBottom).toFloat()
        )
        val fontSizeProp = PropertyValuesHolder.ofFloat(
            "fontSize",
            startFontSize,
            endFontSize
        )
        val animator: ObjectAnimator = if (startData.textColor != endData.textColor) {
            val textColorProp = PropertyValuesHolder.ofObject(
                "textColor",
                ArgbEvaluator(),
                startData.textColor,
                endData.textColor
            )
            ObjectAnimator.ofPropertyValuesHolder(
                drawable,
                leftProp,
                topProp,
                rightProp,
                bottomProp,
                fontSizeProp,
                textColorProp
            )
        } else {
            ObjectAnimator.ofPropertyValuesHolder(
                drawable,
                leftProp,
                topProp,
                rightProp,
                bottomProp,
                fontSizeProp
            )
        }
        val finalFontSize = endFontSize
        val listener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            /**
             * Called at the end of the animation, where we remove `drawable` as an overlay and
             * restore the `textColors`, `hintTextColors`, `highlightColor` and `linkTextColors`
             * properties of `textView` to the values we saved in our local variables above.
             *
             * @param animation The animation which reached its end.
             */
            override fun onAnimationEnd(animation: Animator) {
                textView.overlay.remove(drawable)
                textView.setTextColor(textColors)
                textView.setHintTextColor(hintColors)
                textView.highlightColor = highlightColor
                textView.setLinkTextColor(linkColors)
            }

            /**
             * Called when the animation is paused. We set various properties  of `textView` to the
             * current values of `drawable`.
             *
             * @param animation The animation being paused.
             */
            override fun onAnimationPause(animation: Animator) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, drawable.fontSize)
                val paddingLeft = drawable.getLeft().roundToInt()
                val paddingTop = drawable.getTop().roundToInt()
                val fraction = animator.animatedFraction
                val paddingRight = interpolate(
                    startData.paddingRight.toFloat(),
                    endData.paddingRight.toFloat(),
                    fraction
                ).roundToInt()
                val paddingBottom = interpolate(
                    startData.paddingBottom.toFloat(),
                    endData.paddingBottom.toFloat(),
                    fraction
                ).roundToInt()
                textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
                textView.setTextColor(drawable.textColor)
            }

            /**
             * Called when the animation is resumed, after being previously paused. We set various
             * properties of `textView` to the values found in `endData`.
             *
             * @param animation The animation being resumed.
             */
            override fun onAnimationResume(animation: Animator) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalFontSize)
                textView.setPadding(
                    endData.paddingLeft, endData.paddingTop,
                    endData.paddingRight, endData.paddingBottom
                )
                textView.setTextColor(endData.textColor)
            }
        }
        animator.addListener(listener)
        animator.addPauseListener(listener)
        return animator
    }

    /**
     * This [Drawable] is used to scale the start and end bitmaps and switch between them
     * at the appropriate progress.
     *
     * @param view the [TextView] that we are overlaying with our [SwitchBitmapDrawable].
     * @param gravity the horizontal and vertical alignment of both of the [TextView]'s
     * @param startBitmap the [Bitmap] of the starting [TextView] captured by [captureTextBitmap]
     * @param startFontSize the font size of the starting [TextView].
     * @param startWidth the measured width of the text in the starting [TextView]
     * @param endBitmap the [Bitmap] of the ending [TextView] captured by [captureTextBitmap]
     * @param endFontSize the font size of the ending [TextView].
     * @param endWidth the measured width of the text in the ending [TextView]
     */
    private class SwitchBitmapDrawable(
        private val view: TextView,
        gravity: Int,
        private val startBitmap: Bitmap?,
        private val startFontSize: Float,
        private val startWidth: Float,
        private val endBitmap: Bitmap?,
        private val endFontSize: Float,
        private val endWidth: Float
    ) : Drawable() {
        /**
         * The absolute horizontal gravity of our field `gravity`
         */
        private val horizontalGravity: Int = gravity and Gravity.HORIZONTAL_GRAVITY_MASK

        /**
         * The vertical gravity of our field `gravity`
         */
        private val verticalGravity: Int = gravity and Gravity.VERTICAL_GRAVITY_MASK

        /**
         * The [Paint] we use to draw our [Bitmap] field [startBitmap] to the [Canvas] passed to our
         * `draw` override.
         */
        private val paint = Paint()

        /**
         * The font size in pixels of the scaled bitmap text.
         */
        var fontSize = 0f
            /**
             * Sets the font size that the text should be displayed at. We set the backing field of
             * our property (referenced using the `field` keyword) to our [Float] parameter [fontSize]
             * then call our [invalidateSelf] method to have it use the current [Drawable.Callback]
             * implementation to have its [Drawable] redrawn.
             *
             * @param fontSize The font size in pixels of the scaled bitmap text.
             */
            set(fontSize) {
                field = fontSize
                invalidateSelf()
            }

        /**
         * The left side of the text in pixels.
         */
        private var left = 0f

        /**
         * The top of the text in pixels.
         */
        private var top = 0f

        /**
         * The right side of the text in pixels.
         */
        private var right = 0f

        /**
         * The bottom of the text in pixels.
         */
        private var bottom = 0f

        /**
         * The color of the text being displayed.
         */
        var textColor = 0
            /**
             * Sets the color of the text to be displayed.
             *
             * @param textColor The color of the text to be displayed.
             */
            set(textColor) {
                field = textColor
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    colorFilter = BlendModeColorFilter(textColor, BlendMode.SRC_IN)
                } else {
                    @Suppress("DEPRECATION") // Needed for Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                    setColorFilter(textColor, PorterDuff.Mode.SRC_IN)
                }
                invalidateSelf()
            }

        /**
         * Invalidates our entire view so that we will be redrawn. First we call our super's
         * implementation of `invalidateSelf` which uses the current [Drawable.Callback]
         * implementation to have this [Drawable] redrawn. Does nothing if there is no `Callback`
         * attached to the [Drawable]. Then we call the [View.invalidate] method of the [TextView]
         * field [view]  that we are overlaying with our [SwitchBitmapDrawable] to have it
         * invalidate the whole view. If the view is visible, `onDraw` will be called at some point
         * in the future.
         */
        override fun invalidateSelf() {
            super.invalidateSelf()
            view.invalidate()
        }

        /**
         * Sets the left side of the text. This should be the same as the left padding.
         *
         * @param left The left side of the text in pixels.
         */
        @Suppress("unused") // Unused but instructional
        fun setLeft(left: Float) {
            this.left = left
            invalidateSelf()
        }

        /**
         * Sets the top of the text. This should be the same as the top padding.
         *
         * @param top The top of the text in pixels.
         */
        @Suppress("unused") // Unused but instructional
        fun setTop(top: Float) {
            this.top = top
            invalidateSelf()
        }

        /**
         * Sets the right of the drawable.
         *
         * @param right The right pixel of the drawn area.
         */
        @Suppress("unused") // Unused but instructional
        fun setRight(right: Float) {
            this.right = right
            invalidateSelf()
        }

        /**
         * Sets the bottom of the drawable.
         *
         * @param bottom The bottom pixel of the drawn area.
         */
        @Suppress("unused") // Unused but instructional
        fun setBottom(bottom: Float) {
            this.bottom = bottom
            invalidateSelf()
        }

        /**
         * @return The left side of the text.
         */
        fun getLeft(): Float {
            return left
        }

        /**
         * @return The top of the text.
         */
        fun getTop(): Float {
            return top
        }

        /**
         * @return The right side of the text.
         */
        @Suppress("unused") // Unused but instructional
        fun getRight(): Float {
            return right
        }

        /**
         * @return The bottom of the text.
         */
        @Suppress("unused") // Unused but instructional
        fun getBottom(): Float {
            return bottom
        }

        /**
         * We override this to do our drawing. First we `save` the current matrix and clip of our
         * [Canvas] parameter [canvas] onto a private stack, using the [Int] value it returns to
         * initialize our variable `val saveCount` (this is the count to pass to `restoreToCount`
         * to balance this `save`). We initialize our [Float] variable `val threshold` to our field
         * [startFontSize] divided by the sum of our fields [startFontSize] and [endFontSize] (this
         * is the point in our animation of the font size where we switch between [startBitmap] and
         * [endBitmap], we do this because scaled-up fonts look bad, so we want to switch when closer
         * to the smaller font size). We initialize our variable `val fontSize` to the current value
         * of our [fontSize] property, and initialize our [Float] variable `val progress` to `fontSize`
         * minus [startFontSize] divided by the quantity [endFontSize] minus [startFontSize] (the
         * progress we have made animating from [startFontSize] to [endFontSize] which we use to
         * compare against our bitmap switch threshold `threshold`). We then initialize our [Float]
         * variable `val expectedWidth` to the value returned by our [interpolate] when it guesses
         * the width of the drawn text by interpolating between [startWidth] and [endWidth] given
         * our current value of `progress` (drawn text width is a more accurate scale than font size
         * so this avoids a jump when switching bitmaps).
         *
         * We now branch depending on whether `progress` has reached `threshold` where we want to
         * switch from using [startBitmap] to using [endBitmap]:
         *  - `progress` is less than `threshold` we are drawing using [startBitmap], so we initialize
         *  our [Float] variable `val scale` to `expectedWidth` divided by [startWidth], call our
         *  [getTranslationPoint] method to calculate a value to initialize our [Float] variable
         *  `val tx` with the X distance in pixels we need to translate our [Canvas] parameter [canvas],
         *  and call our [getTranslationPoint] method to calculate a value to initialize our [Float]
         *  variable `val ty` with the Y distance in pixels we need to translate our [Canvas] parameter
         *  [canvas]. We call the [Canvas.translate] method of [canvas] to translate the [Canvas] to
         *  to the point (`tx`, `ty`), call the [Canvas.scale] method of [canvas] to scale the [Canvas]
         *  by our `scale` variable in both the X and Y direction, and then call the [Canvas.drawBitmap]
         *  method of [canvas] to draw [startBitmap] at the position (0, 0) using our [Paint] field
         *  [paint].
         *  - `progress` is greater than or equal to `threshold` we are drawing using [endBitmap],
         *  so we initialize  our [Float] variable `val scale` to `expectedWidth` divided by [endWidth],
         *  call our [getTranslationPoint] method to calculate a value to initialize our [Float] variable
         *  `val tx` with the X distance in pixels we need to translate our [Canvas] parameter [canvas],
         *  and call our [getTranslationPoint] method to calculate a value to initialize our [Float]
         *  variable `val ty` with the Y distance in pixels we need to translate our [Canvas] parameter
         *  [canvas]. We call the [Canvas.translate] method of [canvas] to translate the [Canvas] to
         *  to the point (`tx`, `ty`), call the [Canvas.scale] method of [canvas] to scale the [Canvas]
         *  by our `scale` variable in both the X and Y direction, and then call the [Canvas.drawBitmap]
         *  method of [canvas] to draw [endBitmap] at the position (0, 0) using our [Paint] field
         *  [paint].
         *
         * Finally we call the [Canvas.restoreToCount] method of [canvas] to have it pop any calls to
         * [Canvas.save] that happened after the save count reached `saveCount` (recall that we set
         * `saveCount` to the value returned from the [Canvas.save] method of [canvas] at the beginnng
         * of this method so this will pop to the state that [canvas] was in when we were called).
         *
         * @param canvas The [Canvas] to draw into.
         */
        override fun draw(canvas: Canvas) {
            val saveCount: Int = canvas.save()
            // The threshold changes depending on the target font sizes. Because scaled-up
            // fonts look bad, we want to switch when closer to the smaller font size. This
            // algorithm ensures that null bitmaps (font size = 0) are never used.
            val threshold: Float = startFontSize / (startFontSize + endFontSize)
            val fontSize = fontSize
            val progress: Float = (fontSize - startFontSize) / (endFontSize - startFontSize)

            // The drawn text width is a more accurate scale than font size. This avoids
            // jump when switching bitmaps.
            val expectedWidth: Float = interpolate(startWidth, endWidth, progress)
            if (progress < threshold) {
                // draw start bitmap
                val scale: Float = expectedWidth / startWidth
                val tx: Float = getTranslationPoint(
                    horizontalGravity,
                    left,
                    right,
                    (startBitmap ?: return).width.toFloat(),
                    scale
                )
                val ty = getTranslationPoint(
                    verticalGravity,
                    top,
                    bottom,
                    startBitmap.height.toFloat(),
                    scale
                )
                canvas.translate(tx, ty)
                canvas.scale(scale, scale)
                canvas.drawBitmap(startBitmap, 0f, 0f, paint)
            } else {
                // draw end bitmap
                val scale = expectedWidth / endWidth
                val tx = getTranslationPoint(
                    horizontalGravity,
                    left,
                    right,
                    (endBitmap ?: return).width.toFloat(),
                    scale
                )
                val ty = getTranslationPoint(
                    verticalGravity,
                    top,
                    bottom,
                    endBitmap.height.toFloat(),
                    scale
                )
                canvas.translate(tx, ty)
                canvas.scale(scale, scale)
                canvas.drawBitmap(endBitmap, 0f, 0f, paint)
            }
            canvas.restoreToCount(saveCount)
        }

        /**
         * Specify an alpha value for the drawable. 0 means fully transparent, and 255 means fully
         * opaque. We ignore.
         *
         * @param alpha the alpha value to set our alpha to.
         */
        override fun setAlpha(alpha: Int) {}

        /**
         * Specify an optional color filter for the drawable. If a Drawable has a [ColorFilter],
         * each output pixel of the [Drawable]'s drawing contents will be modified by the color
         * filter before it is blended onto the render target of a Canvas. Pass `null` to remove
         * any existing color filter. We just call the [Paint.setColorFilter] method of our [Paint]
         * field [paint] to have it set its `colorFilter` property to our [ColorFilter] parameter
         * [colorFilter].
         *
         * @param colorFilter The [ColorFilter] to apply, or `null` to remove the existing color filter
         */
        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        /**
         * Return the opacity/transparency of this Drawable. The returned value is one of the abstract
         * format constants in [android.graphics.PixelFormat]: `UNKNOWN`, `TRANSLUCENT`, `TRANSPARENT`,
         * or `OPAQUE`.
         *
         * An `OPAQUE` drawable is one that draws all all content within its bounds, completely
         * covering anything behind the drawable. A `TRANSPARENT` drawable is one that draws nothing
         * within its bounds, allowing everything behind it to show through. A `TRANSLUCENT` drawable
         * is a drawable in any other state, where the drawable will draw some, but not all,
         * of the content within its bounds and at least some content behind the drawable will
         * be visible. If the visibility of the drawable's contents cannot be determined, the
         * safest/best return value is `TRANSLUCENT`. We just return [PixelFormat.TRANSLUCENT].
         *
         * @return int The opacity class of the Drawable.
         */
        @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        /**
         * Called to calculate the distance that the [Canvas] passed to our [draw] method needs to
         * be translated before it draws its [Bitmap] to it. Works for both the X and Y distance.
         * We branch based on the type of gravity passed to use in our [gravity] parameter:
         *  - [Gravity.CENTER_HORIZONTAL] or [Gravity.CENTER_VERTICAL] we return [start] plus [end]
         *  minus [dim] times [scale] all divided by 2.
         *  - [Gravity.RIGHT] or [Gravity.BOTTOM] we return [end] minus [dim] times [scale]
         *  - [Gravity.LEFT] or [Gravity.TOP] we return [start]
         *  - for all other values of [gravity] we return [start]
         *
         * @param gravity the [horizontalGravity] component of our [gravity] field for X distance,
         * and the [verticalGravity] component of our [gravity] field for Y distance.
         * @param start The [left] side of the text in pixels for X distance, and the [top] of the
         * text in pixels for Y distance.
         * @param end the [right] side of the text in pixels for X distance, and the [bottom] of the
         * text in pixels for Y distance.
         * @param dim the `width` of the [Bitmap] being drawn (either [startBitmap] or [endBitmap]
         * depending on the progress of the the animation) for X distance, and the `height` of that
         * same [Bitmap] for Y distance.
         * @param scale the scale that the [Canvas] is going to be scaled by before drawing the
         * [Bitmap] to it.
         */
        @SuppressLint("RtlHardcoded") // This is a US only app
        private fun getTranslationPoint(
            gravity: Int,
            start: Float,
            end: Float,
            dim: Float,
            scale: Float
        ): Float {
            return when (gravity) {
                Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL -> (start + end - dim * scale) / 2f
                Gravity.RIGHT, Gravity.BOTTOM -> end - dim * scale
                Gravity.LEFT, Gravity.TOP -> start
                else -> start
            }
        }

    }

    /**
     * Contains all the non-font-size data used by the TextResize transition.
     * None of these values should trigger the transition, so they are not listed
     * in PROPERTIES. These are captured together to avoid boxing of all the
     * primitives while adding to TransitionValues.
     */
    internal class TextResizeData(textView: TextView) {
        val paddingLeft: Int = textView.paddingLeft
        val paddingTop: Int = textView.paddingTop
        val paddingRight: Int = textView.paddingRight
        val paddingBottom: Int = textView.paddingBottom
        val width: Int = textView.width
        val height: Int = textView.height
        val gravity: Int = textView.gravity
        val textColor: Int = textView.currentTextColor
    }

    companion object {
        /**
         * The key under which we store the `textSize` of the [TextView] in the [TransitionValues]
         * object passed to our [captureValues] method by our [captureStartValues] and [captureEndValues]
         * overrides.
         */
        private const val FONT_SIZE = "TextResize:fontSize"

        /**
         * The key under which we store the [TextResizeData] instance that is used to store all the
         * non-fontSize properties of the [TextView] in the [TransitionValues] object passed to our
         * [captureValues] method by our [captureStartValues] and [captureEndValues] overrides.
         */
        private const val DATA = "TextResize:data"

        /**
         * The array of property names stored in the [TransitionValues] object passed into [captureStartValues]
         * that this transition cares about for the purposes of canceling overlapping animations. We
         * only care about FONT_SIZE. This is the array that our [getTransitionProperties] override
         * returns to its caller.
         */
        private val PROPERTIES = arrayOf(
            // We only care about FONT_SIZE. If anything else changes, we don't
            // want this transition to be called to create an Animator.
            FONT_SIZE
        )

        /**
         * Called to configure the [TextView] parameter [view] to the properties stored in the
         * [TextResizeData] parameter [data] and the font size parameter [fontSize], have it measure
         * itself and then use its [View.layout] method to assign its size and position. The [TextView]
         * used is the one from the end scene and it is configured for the values of the start scene
         * to save the start [Bitmap] from it, and then configured for the values of the end scene
         * to save the end [Bitmap] from it when our [createAnimator] override calls us.
         *
         * @param view the [TextView] we are to configure and layout, it is the one from the end
         * scene which is used for both the start and end [Bitmap].
         * @param data the [TextResizeData] that contains the properties we do not animate.
         * @param fontSize the font size of the start or end scene depending on which scene is being
         * captured to a [Bitmap].
         */
        private fun setTextViewData(view: TextView, data: TextResizeData?, fontSize: Float) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
            view.setPadding(
                (data ?: return).paddingLeft,
                data.paddingTop,
                data.paddingRight,
                data.paddingBottom
            )
            view.right = view.left + data.width
            view.bottom = view.top + data.height
            view.setTextColor(data.textColor)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec)
            view.layout(view.left, view.top, view.right, view.bottom)
        }

        /**
         * Creates a [Bitmap] which it draws its [TextView] parameter [textView] into and then returns
         * that [Bitmap] to the caller. First we initialize our [Drawable] variable `val background`
         * to the `background` property of our [TextView] parameter [textView], then we set the
         * `background` of [textView] to `null`. We initialize our [Int] variable `val width` to the
         * `width` of [textView] minus its `paddingLeft` and minus its `paddingRight`, and initialize
         * our [Int] variable `val height` to the `height` of [textView] minus its `paddingTop` and
         * minus its `paddingTop`. If either the `width` or `height` variable turns out to be 0 we
         * just return `null` to the caller. Otherwise we initialize our [Bitmap] variable `val bitmap`
         * to a `width` by `height` instance configured to use the `ARGB_8888` bitmap configuration.
         * We construct a [Canvas] that will draw into the [Bitmap] `bitmap` to initialize our variable
         * `val canvas`, translate `canvas` by minus the `paddingLeft` of [textView] in the X direction,
         * and minus the `paddingTop` of [textView] in the Y direction, then instruct [textView] to
         * draw into `canvas`. Finally we restore the `background` of [textView] to the [Drawable] we
         * saved in our `background` variable and return `bitmap` to the caller.
         *
         * @param textView the [TextView] which we are to capture into a [Bitmap].
         */
        private fun captureTextBitmap(textView: TextView): Bitmap? {
            val background: Drawable? = textView.background
            textView.background = null
            val width: Int = textView.width - textView.paddingLeft - textView.paddingRight
            val height: Int = textView.height - textView.paddingTop - textView.paddingBottom
            if (width == 0 || height == 0) {
                return null
            }
            val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.translate(-textView.paddingLeft.toFloat(), -textView.paddingTop.toFloat())
            textView.draw(canvas)
            textView.background = background
            return bitmap
        }

        /**
         * Called to interpolate between a property from the start scene and that property of the
         * end scene based on the current animation fraction of our [Animator]. We return the [start]
         * value plus [fraction] times the quantity [end] minus [start].
         *
         * @param start the value of the property of interest in the start scene.
         * @param end the value of the property of interest in the end scene.
         * @param fraction the current animation fraction, which is the elapsed/interpolated fraction
         * used in the most recent frame update on the animation.
         */
        private fun interpolate(start: Float, end: Float, fraction: Float): Float {
            return start + fraction * (end - start)
        }
    }
}