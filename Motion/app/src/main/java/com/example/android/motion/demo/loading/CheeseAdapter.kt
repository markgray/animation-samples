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

@file:Suppress("DEPRECATION") // TODO: PagedListAdapter replaced by PagingDataAdapter, PositionalDataSource replaced by PagingSource

package com.example.android.motion.demo.loading

import android.animation.ObjectAnimator
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.paging.PagedListAdapter // TODO: replaced by PagingDataAdapter
import androidx.paging.PositionalDataSource // TODO: replaced by PagingSource
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * The adapter that displays the `List` of [Cheese] objects whose download from the Internet is
 * similated by our [CheeseDataSource] custom [PositionalDataSource].
 */
internal class CheeseAdapter : PagedListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    /**
     * Called when [RecyclerView] needs a new [CheeseViewHolder] of the given type to represent
     * an item. This new [CheeseViewHolder] should be constructed with a new [View] that can
     * represent the items of the given type. We just return a new instance of [CheeseViewHolder]
     * constructed to use our [ViewGroup] parameter [parent] for any resources it needs.
     *
     * @param parent The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent)
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the item View of the [CheeseViewHolder] parameter [holder] to reflect
     * the item at the given position. We initialize our [Cheese] variable `val cheese` to the
     * item at position [position] in our dataset (if any). If `cheese` is `null` we call the
     * `showPlaceholder` method of our [CheeseViewHolder] parameter [holder] to have it display a
     * "flashing" empty [ViewGroup] to indicate that the cell is waiting for data to "arrive" for
     * it to display. If it is not `null` we call the `bind` method of [holder] with `cheese` to
     * have it do what is necessary to display that particular [Cheese] object.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese: Cheese? = getItem(position)
        if (cheese == null) {
            holder.showPlaceholder()
        } else {
            holder.bind(cheese)
        }
    }
}

/**
 * A dummy adapter that shows placeholders. It is used to display "flashing" empty [CheeseViewHolder]
 * cells while we "await" the first [Cheese] objects to be returned by [CheeseDataSource] when it is
 * first starting the simulated download from the Internet. Once data has "arrived" we are replaced
 * with a [CheeseAdapter].
 */
internal class PlaceholderAdapter : RecyclerView.Adapter<CheeseViewHolder>() {

    /**
     * Returns the total number of items in the data set held by the adapter. We just return the
     * value [Int.MAX_VALUE].
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    /**
     * Called when [RecyclerView] needs a new [CheeseViewHolder] of the given type to represent
     * an item. This new [CheeseViewHolder] should be constructed with a new [View] that can
     * represent the items of the given type. We just return a new instance of [CheeseViewHolder]
     * constructed to use our [ViewGroup] parameter [parent] for any resources it needs.
     *
     * @param parent The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent)
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the item View of the [CheeseViewHolder] parameter [holder] to reflect
     * the item at the given position.
     *
     * We just call the `showPlaceholder` method of our [CheeseViewHolder] parameter [holder] to
     * have it display a "flashing" empty [ViewGroup] to indicate that the cell is waiting for data
     * to "arrive" for it to display.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        // We have to call this method in onBindVH rather than onCreateVH because it uses the
        // adapterPosition of the ViewHolder.
        holder.showPlaceholder()
    }
}

/**
 * The duration of the [ObjectAnimator] animation used to animate the [View.ALPHA] property of the
 * [CheeseViewHolder] empty place holder item view that is being displayed when the `showPlaceholder`
 * method of the [CheeseViewHolder] is called.
 */
private const val FADE_DURATION = 1000L

/**
 * This is the view holder used for both the [CheeseAdapter] and [PlaceholderAdapter] adapters. It
 * can "bind" to display a [Cheese] object, or just be used to display a flashing empty placeholder
 * view while "waiting" for the [Cheese] objects to be "downloaded" by the [CheeseDataSource]
 * internet simulator. The constructor uses the [LayoutInflater] from the context of our [ViewGroup]
 * parameter `parent` to inflate our layout file `R.layout.cheese_list_item` to be used as the
 * item view for our [RecyclerView.ViewHolder]. This layout file consists of a `LinearLayout` holding
 * an [ImageView] with ID `R.id.image` and a [TextView] with ID [R.id.name].
 */
internal class CheeseViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.cheese_list_item, parent, false)
) {
    val image: ImageView = itemView.findViewById(R.id.image)
    val name: TextView = itemView.findViewById(R.id.name)

    /**
     * This is the animation we apply to each of the list items. It animates the alpha value from 1
     * to 0, then back to 1. The animation repeats infinitely until it is manually ended. When the
     * animation is ended the `alpha` value of [itemView] is set to 1.
     */
    private val animation = ObjectAnimator.ofFloat(
        itemView,
        View.ALPHA,
        1f,
        0f,
        1f
    ).apply {
        repeatCount = ObjectAnimator.INFINITE
        duration = FADE_DURATION
        // Reset the alpha on animation end.
        doOnEnd { itemView.alpha = 1f }
    }

    /**
     * Shows an animated "flashing" empty [itemView] in our cell while we are "waiting" for the
     * [Cheese] objects to be "downloaded" by the [CheeseDataSource] internet simulator. First we
     * shift the timing of fade-in/out [animation] for our item depending on its adapter position
     * by setting the position of the [animation] `currentPlayTime` to a time calculated from the
     * `adapterPosition` of our cell. Then we start [animation], set the drawable with resource ID
     * `R.drawable.image_placeholder` to be the content of [ImageView] field [image], set the text
     * of [TextView] field [name] to `null` and its background to the drawable with resource ID
     * `R.drawable.text_placeholder`.
     */
    fun showPlaceholder() {
        // Shift the timing of fade-in/out for each item by its adapter position. We use the
        // elapsed real time to make this independent from the timing of method call.
        animation.currentPlayTime =
            (SystemClock.elapsedRealtime() - adapterPosition * 30L) % FADE_DURATION
        animation.start()
        // Show the placeholder UI.
        image.setImageResource(R.drawable.image_placeholder)
        name.text = null
        name.setBackgroundResource(R.drawable.text_placeholder)
    }

    /**
     * Updates the contents of the item View of our [CheeseViewHolder] to reflect the [Cheese]
     * parameter [cheese]. First we end the "flashing" placeholder animation of our cell, then we
     * begin a load with [Glide] which will load the drawable whose resource ID is the `image`
     * property of [cheese] into our [ImageView] field [image] after applying a [CircleCrop]
     * transformation to it. We then set the text of our [TextView] field [name] to the `name`
     * property of [cheese] and remove the background of the [TextView].
     *
     * @param cheese the [Cheese] object we are supposed to display.
     */
    fun bind(cheese: Cheese) {
        animation.end()
        Glide.with(image).load(cheese.image).transform(CircleCrop()).into(image)
        name.text = cheese.name
        name.setBackgroundResource(0)
    }
}
