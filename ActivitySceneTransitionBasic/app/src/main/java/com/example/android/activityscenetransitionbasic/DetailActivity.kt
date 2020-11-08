/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.activityscenetransitionbasic

import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.squareup.picasso.Picasso

/**
 * Our secondary Activity which is launched from [MainActivity]. Has a simple detail UI
 * which has a large banner image, title and body text.
 */
class DetailActivity : AppCompatActivity() {
    /**
     * The [ImageView] with ID [R.id.imageview_header] in our layout file layout/details.xml which
     * holds the large banner image.
     */
    private lateinit var  mHeaderImageView: ImageView

    /**
     * The [TextView] with ID [R.id.textview_title] in our layout file layout/details.xml which
     * holds the title.
     */
    private lateinit var mHeaderTitle: TextView

    /**
     * The [Item] object whose details we are to display, its ID is passed us as an extra in the
     * `Intent` that launched us stored under the key [EXTRA_PARAM_ID] ("detail:_id")
     */
    private lateinit var mItem: Item

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.details]. First we initialize our
     * [Item] field [mItem] by calling the [Item.getItem] static method to retrieve the [Item] in
     * the dataset [Item.ITEMS] which has the ID that was passed us as an extra in the `Intent` that
     * launched us stored under the key [EXTRA_PARAM_ID] ("detail:_id"). We then initialize our
     * [ImageView] field [mHeaderImageView] by finding the view with ID [R.id.imageview_header], and
     * initialize our [TextView] field [mHeaderTitle] by finding the view with ID [R.id.textview_title].
     * We then set the name of [mHeaderImageView] to be used to identify Views in Transitions to
     * [VIEW_NAME_HEADER_IMAGE] ("detail:header:image"), and set the name of [mHeaderTitle] to be
     * used to identify Views in Transitions to [VIEW_NAME_HEADER_TITLE] ("detail:header:title").
     * Finally we call our method [loadItem] to have it set the text of [mHeaderTitle] and to call
     * either [loadThumbnail] or [loadFullSizeImage] as appropriate to load the correct image into
     * [mHeaderImageView].
     *
     * @param savedInstanceState We do not call [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        // Retrieve the correct Item instance, using the ID provided in the Intent
        mItem = Item.getItem(intent.getIntExtra(EXTRA_PARAM_ID, 0))!!
        mHeaderImageView = findViewById(R.id.imageview_header)
        mHeaderTitle = findViewById(R.id.textview_title)

        // BEGIN_INCLUDE(detail_set_view_name)
        /*
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE)
        ViewCompat.setTransitionName(mHeaderTitle, VIEW_NAME_HEADER_TITLE)
        // END_INCLUDE(detail_set_view_name)
        loadItem()
    }

    /**
     * Sets the text of the title [TextView] field [mHeaderTitle] to the name and author of the [Item]
     * field [mItem], then loads the appropriate image into [ImageView] field [mHeaderImageView]
     * depending on whether the SDK version is `LOLLIPOP` or greater and whether our method
     * [addTransitionListener] can add a [Transition.TransitionListener] to the entering shared
     * element [Transition]. If these are both true we call our method [loadThumbnail] to load the
     * thumbnail of [mItem] (the listener [addTransitionListener] added will load the full-size image
     * when the transition is complete), otherwise we call our method [loadFullSizeImage] to just
     * load the full-size image of [mItem] now.
     */
    private fun loadItem() {
        // Set the title TextView to the item's name and author
        mHeaderTitle.text = getString(R.string.image_header, mItem.name, mItem.author)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            // If we're running on Lollipop and we have added a listener to the shared element
            // transition, load the thumbnail. The listener will load the full-size image when
            // the transition is complete.
            loadThumbnail()
        } else {
            // If all other cases we should just load the full-size image now
            loadFullSizeImage()
        }
    }

    /**
     * Load the item's thumbnail image into our [ImageView]. We use the global default Picasso instance
     * passing it the context of our [ImageView] field [mHeaderImageView] to start an image request
     * using the path specified by the `thumbnailUrl` property of [Item] field [mItem], disable the
     * brief fade in of images loaded from the disk cache or network, and request if to asynchronously
     * fulfill the request into the [ImageView] field [mHeaderImageView].
     */
    private fun loadThumbnail() {
        Picasso.with(mHeaderImageView.context)
                .load(mItem.thumbnailUrl)
                .noFade()
                .into(mHeaderImageView)
    }

    /**
     * Load the item's full-size image into our [ImageView]. We use the global default Picasso instance
     * passing it the context of our [ImageView] field [mHeaderImageView] to start an image request
     * using the path specified by the `photoUrl` property of [Item] field [mItem], disable the brief
     * fade in of images loaded from the disk cache or network, explicitly opt-out to having a placeholder
     * set while waiting, and request if to asynchronously fulfill the request into the [ImageView]
     * field [mHeaderImageView].
     */
    private fun loadFullSizeImage() {
        Picasso.with(mHeaderImageView.context)
                .load(mItem.photoUrl)
                .noFade()
                .noPlaceholder()
                .into(mHeaderImageView)
    }

    /**
     * Try and add a [Transition.TransitionListener] to the entering shared element
     * [Transition]. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return `true` if we were successful in adding a listener to the enter transition
     */
    @RequiresApi(21)
    private fun addTransitionListener(): Boolean {
        val transition = window.sharedElementEnterTransition
        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(object : Transition.TransitionListener {
                /**
                 * Notification about the end of the transition. Canceled transitions will always
                 * notify listeners of both the cancellation and end events. That is, [onTransitionEnd]
                 * is always called, regardless of whether the transition was canceled or played
                 * through to completion. We call our method [loadFullSizeImage] to load the item's
                 * full-size image into our [ImageView] field [mHeaderImageView], then call the
                 * `removeListener` method of [transition] to remove `this` as a listener.
                 *
                 * @param transition The [Transition] which reached its end.
                 */
                override fun onTransitionEnd(transition: Transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullSizeImage()

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this)
                }

                /**
                 * Notification about the start of the transition. We do nothing.
                 *
                 * @param transition The started [Transition].
                 */
                override fun onTransitionStart(transition: Transition) {
                    // No-op
                }

                /**
                 * Notification about the cancellation of the transition. We just call the
                 * `removeListener` method of [transition] to remove `this` as a listener.
                 *
                 * @param transition The [Transition] which was canceled.
                 */
                override fun onTransitionCancel(transition: Transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this)
                }

                /**
                 * Notification when a transition is paused. We do nothing.
                 *
                 * @param transition The [Transition] which was paused.
                 */
                override fun onTransitionPause(transition: Transition) {
                    // No-op
                }

                /**
                 * Notification when a transition is resumed. We do nothing.
                 *
                 * @param transition The [Transition] which was resumed.
                 */
                override fun onTransitionResume(transition: Transition) {
                    // No-op
                }
            })
            return true
        }

        // If we reach here then we have not added a listener
        return false
    }

    companion object {
        /**
         * Key of the ID of the [Item] passed us in our extra [Bundle] that we are to display
         */
        const val EXTRA_PARAM_ID = "detail:_id"

        /**
         * View name of the header image. Used for activity scene transitions.
         */
        const val VIEW_NAME_HEADER_IMAGE = "detail:header:image"

        /**
         * View name of the header title. Used for activity scene transitions
         */
        const val VIEW_NAME_HEADER_TITLE = "detail:header:title"
    }
}