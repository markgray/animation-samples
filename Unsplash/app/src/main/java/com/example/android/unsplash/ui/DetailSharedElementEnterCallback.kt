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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.android.unsplash.DetailActivity
import com.example.android.unsplash.IntentUtil
import com.example.android.unsplash.IntentUtil.hasAll
import com.example.android.unsplash.MainActivity
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.DetailViewBinding
import com.example.android.unsplash.databinding.PhotoItemBinding
import com.example.android.unsplash.ui.pager.DetailViewPagerAdapter
import java.util.ArrayList

/**
 * [MainActivity] uses an instance of this class as its `ExitSharedElementCallback` in its override
 * of `onActivityReenter` constructed using the [Intent] that [DetailActivity] returns its results
 * in and [DetailActivity] uses an instance of this class as its `EnterSharedElementCallback` in its
 * override of `onCreate` constructed using the [Intent] that [MainActivity] used to launch it.
 *
 * @param intent the [Intent] containing the results of running [DetailActivity] in the case of
 * the `onActivityReenter` override in [MainActivity] and the [Intent] that [MainActivity] used to
 * launch [DetailActivity] in its `onCreate` override.
 */
class DetailSharedElementEnterCallback(
    private val intent: Intent
    ) : SharedElementCallback() {

    /**
     * The text size of the [TextView] used to display the [Photo.author] property of the [Photo]
     * being displayed in the target of the shared element transition. That [TextView] is either
     * `currentDetailBinding.author` or `currentPhotoBinding.author` depending on which of the
     * two binding objects holding the `author` property is non-`null` and that of course depends
     * on which of our two [setBinding] methods are called: the one taking a [DetailViewBinding]
     * instance or the one taking a [PhotoItemBinding] (rather clever).
     */
    private var targetTextSize = 0f

    /**
     * The text colors for the different states (normal, selected, focused) of the [TextView] used
     * to display the [Photo.author] property of the [Photo] being displayed in the target of the
     * shared element transition. That [TextView] is either `currentDetailBinding.author` or
     * `currentPhotoBinding.author` depending on which of the two binding objects holding the
     * `author` property is non-`null` and that of course depends on which of our two [setBinding]
     * methods are called: the one taking a [DetailViewBinding] instance or the one taking a
     * [PhotoItemBinding].
     */
    private var targetTextColors: ColorStateList? = null

    /**
     * If our [setBinding] method is called from the `setPrimaryItem` override of [DetailViewPagerAdapter]
     * then this is set to the [DetailViewBinding] of the item that is currently considered to be the
     * "primary", that is the one shown to the user as the current page, and if our [setBinding] method
     * is called from the `onActivityReenter` override of [MainActivity] it is set to `null`.
     */
    private var currentDetailBinding: DetailViewBinding? = null

    /**
     * If our [setBinding] method is called from the `onActivityReenter` override of [MainActivity]
     * it is set to the binding of the `PhotoViewHolder` of the item in the `RecyclerView` of its
     * grid corresponding to the [Photo] that the user scrolled to in [DetailActivity] before
     * returning to [MainActivity], and if our [setBinding] method is called from the `setPrimaryItem`
     * override of [DetailViewPagerAdapter] it is set to `null`.
     */
    private var currentPhotoBinding: PhotoItemBinding? = null

    /**
     * The padding [Rect] of the [TextView] used to display the [Photo.author] property of the [Photo]
     * being displayed in the target of the shared element transition. That [TextView] is either
     * `currentDetailBinding.author` or `currentPhotoBinding.author` depending on which of the two
     * binding objects holding the `author` property is non-`null` and that of course depends on which
     * of our two [setBinding] methods are called: the one taking a [DetailViewBinding] instance or
     * the one taking a [PhotoItemBinding].
     */
    private var targetPadding: Rect? = null

    /**
     * In Activity Transitions, [onSharedElementStart] is called immediately before capturing the
     * start of the shared element state on enter and reenter transitions and immediately before
     * capturing the end of the shared element state for exit and return transitions. First we
     * initialize our [TextView] variable `val author` to our property [author], set our [targetTextSize]
     * field to the `textSize` of `author` set our [ColorStateList] to the `textColors` of `author`,
     * and set our [Rect] field [targetPadding] to a [Rect] constructed to use the `paddingLeft`,
     * `paddingTop`, `paddingRight` and `paddingBottom` properties of `author` as its `left`, `right`,
     * `top` and `bottom` respectively. Then if our [hasAll] method determines that our [Intent] field
     * [intent] has extras for the all of the keys [IntentUtil.TEXT_COLOR], [IntentUtil.FONT_SIZE],
     * and [IntentUtil.PADDING] we:
     *
     *  - set the text color of `author` to the the [Int] stored under the key [IntentUtil.TEXT_COLOR]
     *  in `intent`
     *  - initialize our [Float] variable `val textSize` to the the [Float] stored under the key
     *  [IntentUtil.FONT_SIZE] in `intent` then set the text size of `author` to `textSize` in pixels.
     *  - initialize our [Rect] variable `val padding` to the the [Rect] stored under the key
     *  [IntentUtil.PADDING] in `intent` then set the padding of `author` to the `left`, `right`,
     * `top` and `bottom` properties of `padding`.
     *
     * @param sharedElementNames The names of the shared elements that were accepted into
     * the [View] hierarchy.
     * @param sharedElements The shared elements that are part of the View hierarchy.
     * @param sharedElementSnapshots The Views containing snap shots of the shared element from the
     * launching Window. These elements will not be part of the scene, but will be positioned
     * relative to the Window decor View. This list is null for Fragment
     */
    override fun onSharedElementStart(
        sharedElementNames: List<String>,
        sharedElements: List<View>,
        sharedElementSnapshots: List<View>
    ) {
        val author: TextView = author
        targetTextSize = author.textSize
        targetTextColors = author.textColors
        targetPadding = Rect(
            author.paddingLeft,
            author.paddingTop,
            author.paddingRight,
            author.paddingBottom
        )
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

    /**
     * In Activity Transitions, [onSharedElementEnd] is called immediately before capturing the end
     * of the shared element state on enter and reenter transitions and immediately before capturing
     * the start of the shared element state for exit and return transitions. First we initialize our
     * [TextView] variable `val author` to our property [author], then we set the text size of `author`
     * to our [Float] field [targetTextSize] interpreted as pixels. If our [ColorStateList] field
     * [targetTextColors] is not `null` we set the text color of `author` to [targetTextColors], and
     * if our [Rect] field [targetPadding] is not `null` we set the padding of `author` to the
     * `left`, `right`, `top` and `bottom` properties of [targetPadding]. Finally if our
     * [DetailViewBinding] field [currentDetailBinding] is not `null` we call our method
     * [forceSharedElementLayout] with the `LinearLayout` in [currentDetailBinding] whose ID is
     * `description` to have it call the [View.layout] method of `description` to assign a size and
     * position to it and all of its descendants.
     *
     * @param sharedElementNames The names of the shared elements that were accepted into
     * the View hierarchy.
     * @param sharedElements The shared elements that are part of the View hierarchy.
     * @param sharedElementSnapshots The Views containing snap shots of the shared element from the
     * launching Window. These elements will not be part of the scene, but will be positioned
     * relative to the Window decor View. This list will be null for Fragment Transitions.
     */
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

    /**
     * Lets the [SharedElementCallback] adjust the mapping of shared element names to Views. First
     * we call our method [removeObsoleteElements] with our [MutableList] of [String] parameter
     * [names] and our [MutableMap] of [String] to [View] parameter [sharedElements] as well as
     * the [List] of [String] that our [mapObsoleteElements] method returns when it filters [names]
     * for all of the [String]'s in it which do not start with the [String] "android" (our method
     * [removeObsoleteElements] will remove the names in the [List] returned by [mapObsoleteElements]
     * from [names] as well as from the [MutableMap] parameter [sharedElements]). We then call our
     * method [mapSharedElement] to have it add our [TextView] field [author] to both [names], and
     * [sharedElements] and to have it add our [ImageView] field [photo] to both [names], and
     * [sharedElements].
     *
     * @param names The names of all shared elements transferred from the calling Activity
     * or Fragment in the order they were provided.
     * @param sharedElements The mapping of shared element names to Views. The best guess
     * will be filled into sharedElements based on the transitionNames.
     */
    override fun onMapSharedElements(
        names: MutableList<String>,
        sharedElements: MutableMap<String, View>
    ) {
        removeObsoleteElements(names, sharedElements, mapObsoleteElements(names))
        mapSharedElement(names, sharedElements, author)
        mapSharedElement(names, sharedElements, photo)
    }

    /**
     * Called from the `setPrimaryItem` override of [DetailViewPagerAdapter] with the [DetailViewBinding]
     * of the primary view it is displaying in its [ViewPager]. Sets our [DetailViewBinding] field
     * [currentDetailBinding] to its parameter [binding], and sets our [PhotoItemBinding] field
     * [currentPhotoBinding] to `null`. The getters for our [author] and [photo] properties rather
     * cleverly will chose between [currentPhotoBinding] and [currentDetailBinding] depending on
     * which is not `null` when it fetches their value for its caller.
     *
     * @param binding the [DetailViewBinding] of the primary view [DetailViewPagerAdapter] is
     * displaying in its [ViewPager].
     */
    fun setBinding(binding: DetailViewBinding) {
        currentDetailBinding = binding
        currentPhotoBinding = null
    }

    /**
     * Called from the `onActivityReenter` override of [MainActivity] with the [PhotoItemBinding] of
     * the item in its [RecyclerView] that corresponds to the item that was selected by the user in
     * the [ViewPager] of [DetailActivity] before the return to [MainActivity]. Sets our [PhotoItemBinding]
     * field [currentPhotoBinding] to its parameter [binding], and sets our [DetailViewBinding] field
     * [currentDetailBinding] to `null`. The getters for our [author] and [photo] properties rather
     * cleverly will chose between [currentPhotoBinding] and [currentDetailBinding] depending on which
     * is not `null` when it fetches their value for its caller.
     *
     * @param binding the [PhotoItemBinding] in the [RecyclerView] of [MainActivity] that corresponds
     * to the item that was selected by the user in the [ViewPager] of [DetailActivity] before the
     * return to [MainActivity].
     */
    fun setBinding(binding: PhotoItemBinding) {
        currentPhotoBinding = binding
        currentDetailBinding = null
    }

    /**
     * The [TextView] that is used to display the [Photo.author] property of the current [Photo]
     * located using either the binding for the file layout/photo_item.xml [currentPhotoBinding] if
     * it is not `null` or located using the binding for the file layout/detail_view.xml
     * [currentDetailBinding] if it is not `null`. One or the other of these binding objects must be
     * non-`null` or a [NullPointerException] will be thrown. The binding [currentPhotoBinding] is
     * set in the `onActivityReenter` override of [MainActivity] to the binding of the `PhotoViewHolder`
     * of the item in the `RecyclerView` of its grid corresponding to the [Photo] that the user
     * scrolled to in [DetailActivity] before returning to [MainActivity], and [currentDetailBinding]
     * is set in the `setPrimaryItem` override of [DetailViewPagerAdapter] to the [DetailViewBinding]
     * of the item that is currently considered to be the "primary", that is the one shown to the user
     * as the current page. The method overloading of our [setBinding] method when called with a
     * [PhotoItemBinding] sets [currentPhotoBinding] to it and sets [currentDetailBinding] to `null`,
     * and when called with a [DetailViewBinding] sets [currentDetailBinding] to it and sets
     * [currentPhotoBinding] to `null`
     */
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

    /**
     * The [ImageView] that is used to display the jpeg that `Glide` fetches from the server. The
     * URL for that photo is constructed from the [Photo.id] property of the current [Photo]. The
     * [ImageView] is located using either the binding for the file layout/photo_item.xml
     * [currentPhotoBinding] if it is not `null` or located using the binding for the file
     * layout/detail_view.xml [currentDetailBinding] if it is not `null`. One or the other of these
     * binding objects must be non-`null` or a [NullPointerException] will be thrown. The binding
     * [currentPhotoBinding] is set in the `onActivityReenter` override of [MainActivity] to the
     * binding of the `PhotoViewHolder` of the item in the `RecyclerView` of its grid corresponding
     * to the [Photo] that the user scrolled to in [DetailActivity] before returning to [MainActivity],
     * and [currentDetailBinding] is set in the `setPrimaryItem` override of [DetailViewPagerAdapter]
     * to the [DetailViewBinding] of the item that is currently considered to be the "primary", that
     * is the one shown to the user as the current page. The method overloading of our [setBinding]
     * method when called with a [PhotoItemBinding] sets [currentPhotoBinding] to it and sets
     * [currentDetailBinding] to `null`, and when called with a [DetailViewBinding] sets
     * [currentDetailBinding] to it and sets [currentPhotoBinding] to `null`
     */
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