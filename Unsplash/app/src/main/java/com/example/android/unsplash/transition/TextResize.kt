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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
    @Suppress("unused")
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
    @Suppress("unused")
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
     * A transition does not need to override this method. However, not doing so will mean that th
     * e cancellation logic outlined in the previous paragraph will be skipped for that transition,
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
     * we are interested in inside of [transitionValues].
     *
     * @param transitionValues The holder for any values that the [Transition] wishes to store.
     */
    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view !is TextView) {
            return
        }
        val view = transitionValues.view as TextView
        val fontSize = view.textSize
        transitionValues.values[FONT_SIZE] = fontSize
        val data = TextResizeData(view)
        transitionValues.values[DATA] = data
    }

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
        if (startData!!.gravity != endData!!.gravity) {
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
        val drawable = SwitchBitmapDrawable(textView, startData.gravity,
            startBitmap, startFontSize, startWidth, endBitmap, endFontSize, endWidth)
        textView.overlay.add(drawable)

        // Properties: left, top, font size, text color
        val leftProp = PropertyValuesHolder.ofFloat("left", startData.paddingLeft.toFloat(), endData.paddingLeft.toFloat())
        val topProp = PropertyValuesHolder.ofFloat("top", startData.paddingTop.toFloat(), endData.paddingTop.toFloat())
        val rightProp = PropertyValuesHolder.ofFloat("right", (
            startData.width - startData.paddingRight).toFloat(), (endData.width - endData.paddingRight).toFloat())
        val bottomProp = PropertyValuesHolder.ofFloat("bottom", (
            startData.height - startData.paddingBottom).toFloat(), (endData.height - endData.paddingBottom).toFloat())
        val fontSizeProp = PropertyValuesHolder.ofFloat("fontSize", startFontSize, endFontSize)
        val animator: ObjectAnimator = if (startData.textColor != endData.textColor) {
            val textColorProp = PropertyValuesHolder.ofObject("textColor",
                ArgbEvaluator(), startData.textColor, endData.textColor)
            ObjectAnimator.ofPropertyValuesHolder(drawable,
                leftProp, topProp, rightProp, bottomProp, fontSizeProp, textColorProp)
        } else {
            ObjectAnimator.ofPropertyValuesHolder(drawable,
                leftProp, topProp, rightProp, bottomProp, fontSizeProp)
        }
        val finalFontSize = endFontSize
        val listener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textView.overlay.remove(drawable)
                textView.setTextColor(textColors)
                textView.setHintTextColor(hintColors)
                textView.highlightColor = highlightColor
                textView.setLinkTextColor(linkColors)
            }

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

            override fun onAnimationResume(animation: Animator) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalFontSize)
                textView.setPadding(endData.paddingLeft, endData.paddingTop,
                    endData.paddingRight, endData.paddingBottom)
                textView.setTextColor(endData.textColor)
            }
        }
        animator.addListener(listener)
        animator.addPauseListener(listener)
        return animator
    }

    /**
     * This Drawable is used to scale the start and end bitmaps and switch between them
     * at the appropriate progress.
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
        private val horizontalGravity: Int = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
        private val verticalGravity: Int = gravity and Gravity.VERTICAL_GRAVITY_MASK
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
        private var left = 0f
        private var top = 0f
        private var right = 0f
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
                @Suppress("DEPRECATION")
                setColorFilter(textColor, PorterDuff.Mode.SRC_IN)
                invalidateSelf()
            }

        override fun invalidateSelf() {
            super.invalidateSelf()
            view.invalidate()
        }

        /**
         * Sets the left side of the text. This should be the same as the left padding.
         *
         * @param left The left side of the text in pixels.
         */
        @Suppress("unused")
        fun setLeft(left: Float) {
            this.left = left
            invalidateSelf()
        }

        /**
         * Sets the top of the text. This should be the same as the top padding.
         *
         * @param top The top of the text in pixels.
         */
        @Suppress("unused")
        fun setTop(top: Float) {
            this.top = top
            invalidateSelf()
        }

        /**
         * Sets the right of the drawable.
         *
         * @param right The right pixel of the drawn area.
         */
        @Suppress("unused")
        fun setRight(right: Float) {
            this.right = right
            invalidateSelf()
        }

        /**
         * Sets the bottom of the drawable.
         *
         * @param bottom The bottom pixel of the drawn area.
         */
        @Suppress("unused")
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
        @Suppress("unused")
        fun getRight(): Float {
            return right
        }

        /**
         * @return The bottom of the text.
         */
        @Suppress("unused")
        fun getBottom(): Float {
            return bottom
        }

        override fun draw(canvas: Canvas) {
            val saveCount = canvas.save()
            // The threshold changes depending on the target font sizes. Because scaled-up
            // fonts look bad, we want to switch when closer to the smaller font size. This
            // algorithm ensures that null bitmaps (font size = 0) are never used.
            val threshold = startFontSize / (startFontSize + endFontSize)
            val fontSize = fontSize
            val progress = (fontSize - startFontSize) / (endFontSize - startFontSize)

            // The drawn text width is a more accurate scale than font size. This avoids
            // jump when switching bitmaps.
            val expectedWidth = interpolate(startWidth, endWidth, progress)
            if (progress < threshold) {
                // draw start bitmap
                val scale = expectedWidth / startWidth
                val tx = getTranslationPoint(horizontalGravity, left, right,
                    startBitmap!!.width.toFloat(), scale)
                val ty = getTranslationPoint(verticalGravity, top, bottom,
                    startBitmap.height.toFloat(), scale)
                canvas.translate(tx, ty)
                canvas.scale(scale, scale)
                canvas.drawBitmap(startBitmap, 0f, 0f, paint)
            } else {
                // draw end bitmap
                val scale = expectedWidth / endWidth
                val tx = getTranslationPoint(horizontalGravity, left, right,
                    endBitmap!!.width.toFloat(), scale)
                val ty = getTranslationPoint(verticalGravity, top, bottom,
                    endBitmap.height.toFloat(), scale)
                canvas.translate(tx, ty)
                canvas.scale(scale, scale)
                canvas.drawBitmap(endBitmap, 0f, 0f, paint)
            }
            canvas.restoreToCount(saveCount)
        }

        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        @SuppressLint("RtlHardcoded")
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
        private const val FONT_SIZE = "TextResize:fontSize"
        private const val DATA = "TextResize:data"
        private val PROPERTIES = arrayOf(
            // We only care about FONT_SIZE. If anything else changes, we don't
            // want this transition to be called to create an Animator.
            FONT_SIZE
        )

        private fun setTextViewData(view: TextView, data: TextResizeData?, fontSize: Float) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
            view.setPadding(data!!.paddingLeft, data.paddingTop, data.paddingRight, data.paddingBottom)
            view.right = view.left + data.width
            view.bottom = view.top + data.height
            view.setTextColor(data.textColor)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec)
            view.layout(view.left, view.top, view.right, view.bottom)
        }

        private fun captureTextBitmap(textView: TextView): Bitmap? {
            val background = textView.background
            textView.background = null
            val width = textView.width - textView.paddingLeft - textView.paddingRight
            val height = textView.height - textView.paddingTop - textView.paddingBottom
            if (width == 0 || height == 0) {
                return null
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.translate(-textView.paddingLeft.toFloat(), -textView.paddingTop.toFloat())
            textView.draw(canvas)
            textView.background = background
            return bitmap
        }

        private fun interpolate(start: Float, end: Float, fraction: Float): Float {
            return start + fraction * (end - start)
        }
    }
}