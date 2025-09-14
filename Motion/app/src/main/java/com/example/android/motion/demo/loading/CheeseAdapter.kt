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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * The adapter that displays the list of [Cheese] objects using Paging 3.
 */
internal class CheeseAdapter : PagingDataAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese: Cheese? = getItem(position)
        // Note: PagingDataAdapter may present null items if placeholders are enabled
        // and data is not yet loaded. Our PagingConfig has placeholders disabled.
        if (cheese != null) {
            holder.bind(cheese)
        } else {
            // Optionally, handle the case where item is null, though with placeholders disabled
            // this path should ideally not be hit for actual list items.
            // If you still want placeholder UI for loading states, consider using LoadStateAdapter.
            holder.showPlaceholder() // Kept for consistency if needed, but review usage.
        }
    }
}

/**
 * The duration of the [ObjectAnimator] animation used to animate the [View.ALPHA] property of the
 * [CheeseViewHolder] empty place holder item view that is being displayed when the `showPlaceholder`
 * method of the [CheeseViewHolder] is called.
 */
private const val FADE_DURATION = 1000L

/**
 * This is the view holder used for the [CheeseAdapter]. It
 * can "bind" to display a [Cheese] object, or just be used to display a flashing empty placeholder
 * view while "waiting" for the [Cheese] objects to be "downloaded".
 * The constructor uses the [LayoutInflater] from the context of our [ViewGroup]
 * parameter `parent` to inflate our layout file `R.layout.cheese_list_item` to be used as the
 * item view for our [RecyclerView.ViewHolder]. This layout file consists of a `LinearLayout` holding
 * an [ImageView] with ID `R.id.image` and a [TextView] with ID `R.id.name`.
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
     * [Cheese] objects to be "downloaded".
     */
    fun showPlaceholder() {
        // Shift the timing of fade-in/out for each item by its adapter position. We use the
        // elapsed real time to make this independent from the timing of method call.
        animation.currentPlayTime =
            (SystemClock.elapsedRealtime() - bindingAdapterPosition * 30L) % FADE_DURATION
        animation.start()
        // Show the placeholder UI.
        image.setImageResource(R.drawable.image_placeholder)
        name.text = null
        name.setBackgroundResource(R.drawable.text_placeholder)
    }

    /**
     * Updates the contents of the item View of our [CheeseViewHolder] to reflect the [Cheese]
     * parameter [cheese].
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
