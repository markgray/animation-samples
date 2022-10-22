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
package com.example.android.unsplash.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import com.example.android.unsplash.R

/**
 * A custom [AppCompatImageView] which allows a drawable to be set which will be always drawn on top
 * of our [AppCompatImageView]. This feature does not appear to be used in `Unsplash` however. It is
 * used as the super of [ThreeTwoImageView] for some unknown reason.
 * TODO: figure out why is is used as the super of ThreeTwoImageView
 *
 * @param context The [Context] the view is running in, through which it can access the
 * current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 */
open class ForegroundImageView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {
    /**
     * The [Drawable] which we should draw on top of our [AppCompatImageView]
     */
    private var foreground: Drawable? = null

    /**
     * This is called during layout when the size of this view has changed. If you were just added
     * to the view hierarchy, you're called with the old values of 0. First we call our super's
     * implementation of `onSizeChanged`, then if our [Drawable] field [foreground] is not `null`
     * we call its [Drawable.setBounds] method to specify the bounding rectangle for the [Drawable]
     * to be a [Rect] whose `left` is at 0, `top` is at 0, `right` is at [w] and `bottom` is at [h]
     * (ie. the new size and relative postion of our [View]). This is where the drawable will draw
     * when its `draw` method is called.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (foreground != null) {
            (foreground ?: return).setBounds(0, 0, w, h)
        }
    }

    /**
     * Returns whether this [View] has content which overlaps. This function, intended to be overridden
     * by specific [View] types, is an optimization when alpha is set on a view. If rendering overlaps
     * in a view with alpha < 1, that view is drawn to an offscreen buffer and then composited into
     * place, which can be expensive. If the view has no overlapping rendering, the view can draw each
     * primitive with the appropriate alpha value directly. An example of overlapping rendering is a
     * `TextView` with a background image, such as a `Button`. An example of non-overlapping rendering
     * is a `TextView` with no background, or an `ImageView` with only the foreground image.
     *
     * The default implementation returns true; subclasses should override if they have cases which
     * can be optimized. _Note:_ The return value of this method is ignored if the method
     * [forceHasOverlappingRendering] has been called on this view.
     *
     * We return `false` because we have no overlapping content and optimization is possible.
     *
     * @return `true` if the content in this view might overlap, `false` otherwise.
     */
    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    /**
     * If your view subclass is displaying its own [Drawable] objects, it should override this
     * function and return `true` for any [Drawable] it is displaying. This allows animations for
     * those drawables to be scheduled. Be sure to call through to the super class when overriding
     * this function. We return the short circuit `or` of the value returned by our super's
     * implementation of `verifyDrawable` for our [Drawable] parameter [who] or else the result of
     * checking the referential equality of [who] and our [Drawable] field [foreground].
     *
     * @param who The [Drawable] to verify. Return `true` if it is one you are displaying, else
     * return the result of calling through to the super class.
     * @return boolean If `true` than the [Drawable] is being displayed in the view; else `false`
     * and it is not allowed to animate.
     */
    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foreground
    }

    /**
     * Call [Drawable.jumpToCurrentState] on all [Drawable] objects associated with this view. Also
     * calls `StateListAnimator.jumpToCurrentState` if there is a `StateListAnimator` attached to
     * this view. First we call our super's implementation of `jumpDrawablesToCurrentState`, then if
     * our [Drawable] field [foreground] is not `null` we call its `jumpToCurrentState` method to
     * ask that it immediately jump to the current state and skip any active animations between the
     * states.
     */
    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foreground != null) (foreground ?: return).jumpToCurrentState()
    }

    /**
     * This function is called whenever the state of the view changes in such a way that it impacts
     * the state of drawables being shown. If the View has a `StateListAnimator`, it will also be
     * called to run necessary state change animations. First we call our super's implementation of
     * `drawableStateChanged`. Then if our [Drawable] field [foreground] is not `null` and its
     * `isStateful` property indicates that it will change its appearance based on state we set its
     * `state` property to the [Int] array of resource IDs of the drawable states representing the
     * current state of our view.
     */
    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (foreground != null && (foreground ?: return).isStateful) {
            (foreground ?: return).state = drawableState
        }
    }

    /**
     * Returns the drawable used as the foreground of this view. The foreground drawable, if
     * non-`null`, is always drawn on top of our children. We just return our [Drawable] field
     * [foreground] to our caller.
     *
     * @return A [Drawable] or `null` if no foreground was set.
     */
    override fun getForeground(): Drawable? {
        return foreground
    }

    /**
     * Supply a [Drawable] that is to be rendered on top of the contents of this `ImageView`. If
     * our [Drawable] parameter [drawable] is the same [Drawable] as our current [Drawable] field
     * [foreground] we just return having down nothing. Otherwise if [foreground] is not `null` we
     * set its [Drawable.Callback] to `null` (used for an animated drawable) and call the method
     * [unscheduleDrawable] with [foreground] to unschedule any events associated with it.
     *
     * Having taken care of the old [foreground] we next set [foreground] to [drawable] and branch
     * on whether the new [foreground] is `null`:
     *  - Not `null`: we set the bounding rectangle of [foreground] to a [Rect] that matches our
     *  current `width` and `height`, call [setWillNotDraw] with `false` to indicate that we plan
     *  to draw, set the [Drawable.Callback] of [foreground] to `this` [ForegroundImageView], and if
     *  its `isStateful` property indicates that it will change its appearance based on state we set
     *  its `state` property to the [Int] array of resource IDs of the drawable states representing
     *  the current state of our view.
     *  - is `null`: we call [setWillNotDraw] with `true` to indicate that we do not plan to draw.
     *
     * Finally we call [invalidate] to invalidate the whole view. If the view is visible, [onDraw]
     * will be called at some point in the future.
     *
     * @param drawable The [Drawable] to be drawn on top of the `ImageView`
     */
    override fun setForeground(drawable: Drawable) {
        if (foreground !== drawable) {
            if (foreground != null) {
                (foreground ?: return).callback = null
                unscheduleDrawable(foreground)
            }
            foreground = drawable
            @Suppress("KotlinConstantConditions") // TODO Fix nullability confusion
            if (foreground != null) {
                (foreground ?: return).setBounds(0, 0, width, height)
                setWillNotDraw(false)
                (foreground ?: return).callback = this
                if ((foreground ?: return).isStateful) {
                    (foreground ?: return).state = drawableState
                }
            } else {
                setWillNotDraw(true)
            }
            invalidate()
        }
    }

    /**
     * Manually render this view (and all of its children) to the given [Canvas]. First we call our
     * super's implementaton of `draw`, then if our [Drawable] field [foreground] is not `null` we
     * call its `draw` method to have it draw itself on our [Canvas] parameter [canvas].
     *
     * @param canvas The Canvas to which the View is rendered.
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (foreground != null) {
            (foreground ?: return).draw(canvas)
        }
    }

    /**
     * This function is called whenever the view hotspot changes and needs to be propagated to
     * drawables or child views managed by the view. Dispatching to child views is handled by
     * [dispatchDrawableHotspotChanged]. First we call our super's implementation of
     * `drawableHotspotChanged`, then if our [Drawable] field [foreground] is not `null` we call
     * its `setHotspot` method with our [x] and [y] parameter to specify the new hotspot location
     * within the drawable.
     *
     * @param x hotspot x coordinate
     * @param y hotspot y coordinate
     */
    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (foreground != null) {
            (foreground ?: return).setHotspot(x, y)
        }
    }

    init {
        /**
         * We retrieve the styled attribute information for our `R.styleable.ForegroundImageView`
         * custom attributes into our `TypedArray` variable `val a` using our field `attrs`
         * (attributes from the xml we are inflated from) as the base set of attribute values.
         */
        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView)

        /**
         * We initialize our `Drawable` variable `val d` by retrieving the `Drawable` for the attribute
         * in `a` for `R.styleable.ForegroundImageView_android_foreground` (defined in the file
         * values/attrs_foreground_view.xml by: attr name="android:foreground"). This will be `null`
         * if there is no such attribute used in the xml that we are inflated from.
         */
        val d: Drawable? = a.getDrawable(R.styleable.ForegroundImageView_android_foreground)
        /**
         * If `d` is not `null` we use our method `setForeground` to set our `Drawable` field
         * `foreground` to `d`
         */
        d?.let { setForeground(it) }
        /**
         * Recycles the `TypedArray` variable `a` so it may be re-used by a later caller.
         */
        a.recycle()
        /**
         * Sets the `ViewOutlineProvider` of our view, which generates the Outline that defines the
         * shape of the shadow it casts, and enables outline clipping to `ViewOutlineProvider.BOUNDS`
         * which maintains the outline of our View to match its rectangular bounds, at 1.0f alpha.
         * This is used to enable Views that are opaque but lack a background to cast a shadow.
         */
        outlineProvider = ViewOutlineProvider.BOUNDS
    }
}