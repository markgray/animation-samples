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
import android.graphics.drawable.Drawable
import android.util.AttributeSet
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
    private var foreground: Drawable? = null
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (foreground != null) {
            foreground!!.setBounds(0, 0, w, h)
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foreground
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foreground != null) foreground!!.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (foreground != null && foreground!!.isStateful) {
            foreground!!.state = drawableState
        }
    }

    /**
     * Returns the drawable used as the foreground of this view. The
     * foreground drawable, if non-null, is always drawn on top of the children.
     *
     * @return A Drawable or null if no foreground was set.
     */
    override fun getForeground(): Drawable {
        return foreground!!
    }

    /**
     * Supply a Drawable that is to be rendered on top of the contents of this ImageView
     *
     * @param drawable The Drawable to be drawn on top of the ImageView
     */
    override fun setForeground(drawable: Drawable) {
        if (foreground !== drawable) {
            if (foreground != null) {
                foreground!!.callback = null
                unscheduleDrawable(foreground)
            }
            foreground = drawable
            if (foreground != null) {
                foreground!!.setBounds(0, 0, width, height)
                setWillNotDraw(false)
                foreground!!.callback = this
                if (foreground!!.isStateful) {
                    foreground!!.state = drawableState
                }
            } else {
                setWillNotDraw(true)
            }
            invalidate()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (foreground != null) {
            foreground!!.draw(canvas)
        }
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (foreground != null) {
            foreground!!.setHotspot(x, y)
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView)
        val d = a.getDrawable(R.styleable.ForegroundImageView_android_foreground)
        d?.let { setForeground(it) }
        a.recycle()
        outlineProvider = ViewOutlineProvider.BOUNDS
    }
}