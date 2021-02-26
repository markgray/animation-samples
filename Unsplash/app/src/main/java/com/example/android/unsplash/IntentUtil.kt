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
package com.example.android.unsplash

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import java.util.ArrayList

/**
 * Holding intent extra names and utility methods for intent handling.
 */
object IntentUtil {
    /**
     * The key for the extra in which [MainActivity] stores the `textSize` of the `TextView` holding
     * the `author` property of the [Photo] that the user selected in the [Intent] it uses to launch
     * [DetailActivity].
     */
    const val FONT_SIZE = "fontSize"

    /**
     * The key for the extra in which [MainActivity] stores a [Rect] holding the padding of the
     * `TextView` holding the `author` property of the [Photo] that the user selected in the
     * [Intent] it uses to launch [DetailActivity].
     */
    const val PADDING = "padding"

    /**
     * The key for the extra in which [MainActivity] stores a `ParcelableArrayList` holding all of
     * the [Photo] objects in the [ArrayList] of [Photo] objects used as the shared dataset in the
     * [Intent] it uses to launch [DetailActivity].
     */
    const val PHOTO = "photo"

    /**
     *  The key for the extra in which [MainActivity] stores the current color selected for normal
     *  text of the `TextView` holding the `author` property of the [Photo] that the user selected
     *  in the [Intent] it uses to launch [DetailActivity].
     */
    const val TEXT_COLOR = "color"

    /**
     * The key in the [Bundle] passed to the `onSaveInstanceState` override of [MainActivity] which
     * is used to save the [ArrayList] of [Photo] objects used as its dataset (aka the field
     * [MainActivity.relevantPhotos]) and the key which its `onCreate` override uses to restore
     * [MainActivity.relevantPhotos] from the [Bundle] passed it when the activity is being
     * re-initialized after previously being shut down.
     */
    const val RELEVANT_PHOTOS = "relevant"

    /**
     * The key for the [Intent] extra in which both [MainActivity] and [DetailActivity] store the
     * position of the [Photo] object in their UI that was selected by the user. [MainActivity]
     * stores the positon of the selected [Photo] in the [Intent] used to launch [DetailActivity],
     * and in [DetailActivity] if the user scrolls to a different [Photo] in its [ViewPager] it will
     * store the new position in an [Intent] which it returns to [MainActivity] as part of its
     * result (if the user does not move to a new [Photo] no [Intent] is included).
     */
    const val SELECTED_ITEM_POSITION = "selected"

    /**
     * The `requestCode` that [MainActivity] uses to launch [DetailActivity] with in its call to the
     * method `startActivityForResult`. Just an arbitrary [Int] which is ignored by both activities.
     */
    const val REQUEST_CODE = R.id.requestCode

    /**
     * Checks if all extras are present in an intent. We loop for all of the [String] in our `vararg`
     * parameter [extras] assigning each [String] to our variable `extra` in turn and if our [Intent]
     * parameter [intent] does not have the extra whose key is the [String] `extra` in it we return
     * `false` to the caller, otherwise we loop through the rest of the [String]'s in [extras] and
     * if [intent] succeeds in finding them all we return `true` to the caller. This is called by the
     * `onSharedElementStart` override in [DetailSharedElementEnterCallback] to check whether the
     * [Intent] that launched [DetailActivity] contains the extras it needs to configure the `TextView`
     * that contains the [Photo.author] property, and if any is missing it does not bother to change
     * the defaults of that `TextView`.
     *
     * @param intent The [Intent] to check.
     * @param extras The extras to check for.
     * @return `true` if all extras are present, else `false`.
     */
    @JvmStatic
    fun hasAll(intent: Intent, vararg extras: String?): Boolean {
        for (extra in extras) {
            if (!intent.hasExtra(extra)) {
                return false
            }
        }
        return true
    }

    /**
     * Checks if any extra is present in an intent. We loop for all of the [String] in our `vararg`
     * parameter [extras] assigning each [String] to our variable `extra` in turn and if our [Intent]
     * parameter [intent] has the extra whose key is the [String] `extra` in it we return `true` to
     * the caller, otherwise we loop through the rest of the [String]'s in [extras] and if [intent]
     * fails to find any of them we return `false` to the caller. UNUSED
     *
     * @param intent The intent to check.
     * @param extras The extras to check for.
     * @return `true` if any checked extra is present, else `false`.
     */
    @Suppress("unused")
    fun hasAny(intent: Intent, vararg extras: String?): Boolean {
        for (extra in extras) {
            if (intent.hasExtra(extra)) {
                return true
            }
        }
        return false
    }
}