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

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * Takes another view as a substance and draws its content. This is useful for copying an appearance
 * of another view without spending the cost of full instantiation when transitioning to a Scene which
 * does not have a view whose content corresponds to the view being transitioned from apart from its
 * position.
 *
 * @param context The [Context] the view is running in, through which it can
 * access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 * @param defStyleAttr An attribute in the current theme that contains a
 * reference to a style resource that supplies default values for
 * the view. Can be 0 to not look for defaults.
 *
 * @see SharedFade
 */
class MirrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        /**
         * We set this flag to `true` because this view doesn't do any drawing on its own until it
         * has its `_substance` field set. Our `onDraw` override instead passes the call on to the
         * `draw` method of our `View` field `_substance` if it is not `null` and our `setWillNotDraw`
         * flag is nulled out when our `substance` property updates the `View` held in `_substance`
         * to a non-`null` value.
         */
        setWillNotDraw(true)
    }

    /**
     * The "real" [View] that we are supposed to mirror for purposes of transition. Private to
     * prevent direct modification by other classes, public access is provided by our [substance]
     * field.
     */
    private var _substance: View? = null

    /**
     * Public access to our [View] field [_substance]. The getter just returns the value of [_substance]
     * and the setter stores its [View] parameter in [_substance] and calls the [setWillNotDraw] method
     * with `null` to clear the `willNotDraw` flag so that our [onDraw] method will be called now that
     * we have a non-`null` [_substance] to pass the call on to. Set in the `createAnimator` override
     * of [SharedFade] when the [MirrorView] is the `endView` to set [_substance] to the `startView`
     * which is disappearing.
     */
    var substance: View?
        get() = _substance
        set(value) {
            _substance = value
            setWillNotDraw(value == null)
        }

    /**
     * We implement this to do our drawing once [_substance] has been set to a non-`null` value.
     * If our [View] field [_substance] is not `null` we call its [View.draw] method to have it render
     * its [View] (and all of its children) to our [Canvas] parameter [canvas].
     *
     * @param canvas the [Canvas] on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas) {
        _substance?.draw(canvas)
    }
}
