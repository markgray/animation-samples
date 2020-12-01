/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.gridtopager.adapter

import android.graphics.drawable.Drawable
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.samples.gridtopager.MainActivity
import com.google.samples.gridtopager.R
import com.google.samples.gridtopager.adapter.GridAdapter.ImageViewHolder
import com.google.samples.gridtopager.adapter.ImageData.IMAGE_DRAWABLES
import com.google.samples.gridtopager.fragment.ImagePagerFragment
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A [RecyclerView.Adapter] for a fragment that displays a grid of images in its [RecyclerView].
 */
class GridAdapter(fragment: Fragment) : RecyclerView.Adapter<ImageViewHolder>() {
    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     * The [onLoadCompleted] override is called when [Glide] finishes loading the resource image
     * or fails to load it, and the [onItemClicked] override is called when the view in the grid
     * is clicked by the user.
     */
    interface ViewHolderListener {
        /**
         * This is called when [Glide] either gives up a failed load and calls the `onLoadFailed`
         * override of its listener, or has successfully loaded the image and calls the override
         * of `onResourceReady` of its listener.
         *
         * @param view the [ImageView] which is being loaded into
         * @param adapterPosition the position in our dataset whose drawable was being loaded
         */
        fun onLoadCompleted(view: ImageView?, adapterPosition: Int)

        /**
         * This is called by the `onClick` override of [ImageViewHolder] (it implements the
         * [View.OnClickListener] interface) when the view with ID [R.id.card_view] in the
         * item view of the view holder is clicked.
         *
         * @param view the [View] that was clicked.
         * @param adapterPosition the position in our [IMAGE_DRAWABLES] dataset that was clicked.
         */
        fun onItemClicked(view: View, adapterPosition: Int)
    }

    /**
     * The [Glide] instance of [RequestManager] we use to fetch drawables from our resources.
     */
    private val requestManager: RequestManager = Glide.with(fragment)

    /**
     * The [ViewHolderListener] that every [ImageViewHolder] we create will use. In our case it is
     * an instance of [ViewHolderListenerImpl] which is constructed in our `init` block.
     */
    private val viewHolderListener: ViewHolderListener

    /**
     * Called when [RecyclerView] needs a new [ImageViewHolder] to represent an item. We initialize
     * our [View] variable `val view` by using the [LayoutInflater] of the context of our [ViewGroup]
     * parameter [parent] to inflate our layout file [R.layout.image_card] using [parent] for its
     * layout params. Then we return a new instance of [ImageViewHolder] constructed to hold `view`,
     * use [requestManager] to fetch the correct resource drawable, and to use [viewHolderListener]
     * as its [ViewHolderListener].
     *
     * @param parent The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     * @return A new [ViewHolder] that holds a [View] of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_card, parent, false)
        return ImageViewHolder(view, requestManager, viewHolderListener)
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the [ImageViewHolder]} to reflect the item at the given position.
     * We just call the `onBind` method of our [ImageViewHolder] parameter [holder].
     *
     * @param holder The [ImageViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind()
    }

    /**
     * Returns the total number of items in the data set held by the adapter. We just return the
     * `size` of our dataset array [IMAGE_DRAWABLES].
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return IMAGE_DRAWABLES.size
    }

    /**
     * Default [ViewHolderListener] implementation.
     */
    private class ViewHolderListenerImpl(private val fragment: Fragment) : ViewHolderListener {
        /**
         * Flag we use to make sure we only call `startPostponedEnterTransition` once. Its initial
         * value is `false`, and it is set to `true` when the selected image loading is completed.
         */
        private val enterTransitionStarted: AtomicBoolean = AtomicBoolean()

        /**
         * This is called when [Glide] either gives up a failed load and calls the `onLoadFailed`
         * override of its listener, or has successfully loaded the image and calls the override
         * of `onResourceReady` of its listener. If [MainActivity.currentPosition] is not equal to
         * our parameter [adapterPosition] we return having done nothing. We then Atomically set
         * [enterTransitionStarted] to `true` and return if it was already `true`. Otherwise we call
         * the `startPostponedEnterTransition` method of our [Fragment] field [fragment] to begin
         * postponed transitions after `postponeEnterTransition` was called. `postponeEnterTransition`
         * is called in the `onCreateView` override of `GridFragment`.
         *
         * @param view the [ImageView] which is being loaded into
         * @param adapterPosition the position in our dataset whose drawable was being loaded
         */
        override fun onLoadCompleted(view: ImageView?, adapterPosition: Int) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (MainActivity.currentPosition != adapterPosition) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            fragment.startPostponedEnterTransition()
        }

        /**
         * Handles a view click by setting the current position to the given `position` and starting
         * a [ImagePagerFragment] which displays the image at the position. First we set the current
         * image position [MainActivity.currentPosition] to our parameter [adapterPosition]. Then we
         * fetch the Transition that will be used to move Views out of the scene when the fragment
         * is removed, hidden, or detached of our [Fragment] field [fragment] as a [TransitionSet],
         * and exclude our [View] parameter [view] from the transition (e.g. the card will disappear
         * immediately instead of fading out with the rest to prevent an overlapping animation of
         * fade and move). Then we set our [ImageView] variable `val transitioningView` to the [View]
         * in [view] with ID [R.id.card_image]. We fetch the `FragmentManager` for interacting with
         * fragments associated with our [Fragment] field [fragment] and use it to start a
         * `FragmentTransaction`, enable it to optimize operations within and across transactions,
         * add the shared element [View] `transitioningView` for our disappearing [Fragment] to match
         * with the transition name of `transitioningView` for the [View] in the appearing Fragment,
         * then have it replace our existing [Fragment] in the container with ID [R.id.fragment_container]
         * with a new instance of [ImagePagerFragment] whose TAG is the simple name of the class
         * [ImagePagerFragment], add the `FragmentTransaction` to the back stack with a `null` name,
         * and then schedule a commit of this transaction.
         *
         * @param view the clicked [ImageView] (the shared element view will be re-mapped at the
         * GridFragment's SharedElementCallback)
         * @param adapterPosition the selected view position
         */
        override fun onItemClicked(view: View, adapterPosition: Int) {
            // Update the position.
            MainActivity.currentPosition = adapterPosition

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            (fragment.exitTransition as TransitionSet?)!!.excludeTarget(view, true)
            val transitioningView = view.findViewById<ImageView>(R.id.card_image)
            fragment.fragmentManager!!
                .beginTransaction()
                .setReorderingAllowed(true) // Optimize for shared element transition
                .addSharedElement(transitioningView, transitioningView.transitionName)
                .replace(
                    R.id.fragment_container,
                    ImagePagerFragment(),
                    ImagePagerFragment::class.java.simpleName
                )
                .addToBackStack(null)
                .commit()
        }

    }

    /**
     * ViewHolder for the grid's images.
     *
     * @param itemView the [View] inflated from our layout file [R.layout.image_card] which we use
     * to display our [ImageView].
     * @param requestManager the [RequestManager] we use to have [Glide] load our drawable, always
     * the [GridAdapter] field [requestManager] in our case.
     * @param viewHolderListener the [ViewHolderListener] implementation whose overrides are called
     * when [Glide] finishes loading, or our [ImageView] is clicked, always [ViewHolderListenerImpl]
     * in our case.
     */
    class ImageViewHolder(
        itemView: View,
        private val requestManager: RequestManager,
        private val viewHolderListener: ViewHolderListener
    ) : ViewHolder(itemView), View.OnClickListener {
        /**
         * The [ImageView] in our [View] field [itemView] with ID [R.id.card_image] into which we
         * load our drawable.
         */
        private val image: ImageView = itemView.findViewById(R.id.card_image)

        /**
         * Binds this view holder to the given adapter position. The binding will load the image
         * into the image view, as well as set its transition name for later. We set our variable
         * `val adapterPosition` to the Adapter position of the item represented by this ViewHolder,
         * then call our [setImage] method to have it use [Glide] to load the drawable corresponding
         * to `adapterPosition` into our [ImageView] field [image]. We then set the string value of
         * the image resource in [IMAGE_DRAWABLES] as the unique transition name for [image].
         */
        fun onBind() {
            val adapterPosition = adapterPosition
            setImage(adapterPosition)
            // Set the string value of the image resource as the unique transition name for the view.
            image.transitionName = IMAGE_DRAWABLES[adapterPosition].toString()
        }

        /**
         * Loads [ImageView] field [image] with the image resource in [IMAGE_DRAWABLES] corresponding
         * to our [adapterPosition] parameter using [Glide].
         *
         * @param adapterPosition the Adapter position of the item represented by this ViewHolder.
         */
        private fun setImage(adapterPosition: Int) {
            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            requestManager
                .load(IMAGE_DRAWABLES[adapterPosition])
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolderListener.onLoadCompleted(image, adapterPosition)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolderListener.onLoadCompleted(image, adapterPosition)
                        return false
                    }
                })
                .into(image)
        }

        override fun onClick(view: View) {
            // Let the listener start the ImagePagerFragment.
            viewHolderListener.onItemClicked(view, adapterPosition)
        }

        init {
            itemView.findViewById<View>(R.id.card_view).setOnClickListener(this)
        }
    }

    /**
     * Constructs a new grid adapter for the given `Fragment`.
     */
    init {
        viewHolderListener = ViewHolderListenerImpl(fragment)
    }
}