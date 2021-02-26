/*
 * Copyright (C) 2019 Google Inc. All Rights Reserved.
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

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.SharedElementCallback
import com.example.android.unsplash.IntentUtil
import com.example.android.unsplash.IntentUtil.hasAll
import com.example.android.unsplash.databinding.DetailViewBinding
import com.example.android.unsplash.databinding.PhotoItemBinding
import java.util.ArrayList

class DetailSharedElementEnterCallback(
    private val intent: Intent
    ) : SharedElementCallback() {

    private var targetTextSize = 0f
    private var targetTextColors: ColorStateList? = null
    private var currentDetailBinding: DetailViewBinding? = null
    private var currentPhotoBinding: PhotoItemBinding? = null
    private var targetPadding: Rect? = null
    override fun onSharedElementStart(
        sharedElementNames: List<String>,
        sharedElements: List<View>,
        sharedElementSnapshots: List<View>
    ) {
        val author = author
        targetTextSize = author.textSize
        targetTextColors = author.textColors
        targetPadding = Rect(author.paddingLeft,
            author.paddingTop,
            author.paddingRight,
            author.paddingBottom)
        if (hasAll(
                intent,
                IntentUtil.TEXT_COLOR,
                IntentUtil.FONT_SIZE,
                IntentUtil.PADDING
            )
        ) {
            author.setTextColor(intent.getIntExtra(IntentUtil.TEXT_COLOR, Color.BLACK))
            val textSize = intent.getFloatExtra(IntentUtil.FONT_SIZE, targetTextSize)
            author.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            val padding = intent.getParcelableExtra<Rect>(IntentUtil.PADDING)
            author.setPadding(padding!!.left, padding.top, padding.right, padding.bottom)
        }
    }

    override fun onSharedElementEnd(
        sharedElementNames: List<String>,
        sharedElements: List<View>,
        sharedElementSnapshots: List<View>
    ) {
        val author = author
        author.setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize)
        if (targetTextColors != null) {
            author.setTextColor(targetTextColors)
        }
        if (targetPadding != null) {
            author.setPadding(
                targetPadding!!.left,
                targetPadding!!.top,
                targetPadding!!.right,
                targetPadding!!.bottom
            )
        }
        if (currentDetailBinding != null) {
            forceSharedElementLayout(currentDetailBinding!!.description)
        }
    }

    override fun onMapSharedElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>
    ) {
        removeObsoleteElements(names, sharedElements, mapObsoleteElements(names))
        mapSharedElement(names, sharedElements, author)
        mapSharedElement(names, sharedElements, photo)
    }

    fun setBinding(binding: DetailViewBinding) {
        currentDetailBinding = binding
        currentPhotoBinding = null
    }

    fun setBinding(binding: PhotoItemBinding) {
        currentPhotoBinding = binding
        currentDetailBinding = null
    }

    private val author: TextView
        get() = when {
            currentPhotoBinding != null -> {
                currentPhotoBinding!!.author
            }
            currentDetailBinding != null -> {
                currentDetailBinding!!.author
            }
            else -> {
                throw NullPointerException("Must set a binding before transitioning.")
            }
        }
    private val photo: ImageView
        get() = when {
            currentPhotoBinding != null -> {
                currentPhotoBinding!!.photo
            }
            currentDetailBinding != null -> {
                currentDetailBinding!!.photo
            }
            else -> {
                throw NullPointerException("Must set a binding before transitioning.")
            }
        }

    /**
     * Maps all views that don't start with "android" namespace.
     *
     * @param names All shared element names.
     * @return The obsolete shared element names.
     */
    private fun mapObsoleteElements(names: List<String>): List<String> {
        val elementsToRemove: MutableList<String> = ArrayList(names.size)
        for (name in names) {
            if (name.startsWith("android")) continue
            elementsToRemove.add(name)
        }
        return elementsToRemove
    }

    /**
     * Removes obsolete elements from names and shared elements.
     *
     * @param names Shared element names.
     * @param sharedElements Shared elements.
     * @param elementsToRemove The elements that should be removed.
     */
    private fun removeObsoleteElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>,
        elementsToRemove: List<String>
    ) {
        if (elementsToRemove.isNotEmpty()) {
            names.removeAll(elementsToRemove)
            for (elementToRemove in elementsToRemove) {
                sharedElements.remove(elementToRemove)
            }
        }
    }

    /**
     * Puts a shared element to transitions and names.
     *
     * @param names The names for this transition.
     * @param sharedElements The elements for this transition.
     * @param view The view to add.
     */
    private fun mapSharedElement(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>,
        view: View
    ) {
        val transitionName = view.transitionName
        names.add(transitionName)
        sharedElements[transitionName] = view
    }

    private fun forceSharedElementLayout(view: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY)
        view.measure(widthSpec, heightSpec)
        view.layout(view.left, view.top, view.right, view.bottom)
    }
}