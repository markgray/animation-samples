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

package com.example.android.motion.demo.loading

import android.animation.ObjectAnimator
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.paging.PagedListAdapter
import androidx.paging.PositionalDataSource
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
 * parameter `parent` to inflate our layout file [R.layout.cheese_list_item] to be used as the
 * item view for our [RecyclerView.ViewHolder]. This layout file consists of a `LinearLayout` holding
 * an [ImageView] with ID [R.id.image] and a [TextView] with ID [R.id.name].
 */
internal class CheeseViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.cheese_list_item, parent, false)
) {
    val image: ImageView = itemView.findViewById(R.id.image)
    val name: TextView = itemView.findViewById(R.id.name)

    /**
     * This is the animation we apply to each of the empty list items if our [showPlaceholder]
     * method is called for them. It animates the alpha value from 1 to 0, then back to 1. The
     * animation repeats infinitely until it is manually ended. An action to be invoked when the
     * animation has ended is added to it which sets the `alpha` to 1f when the animation is ended.
     */
    private val animation = ObjectAnimator.ofFloat(itemView, View.ALPHA, 1f, 0f, 1f).apply {
        repeatCount = ObjectAnimator.INFINITE
        duration = FADE_DURATION
        // Reset the alpha on animation end.
        doOnEnd { itemView.alpha = 1f }
    }

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

    fun bind(cheese: Cheese) {
        animation.end()
        Glide.with(image).load(cheese.image).transform(CircleCrop()).into(image)
        name.text = cheese.name
        name.setBackgroundResource(0)
    }
}
