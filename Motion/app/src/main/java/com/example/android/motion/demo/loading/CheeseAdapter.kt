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
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * The adapter that displays the list of [Cheese] objects using Paging 3.
 */
internal class CheeseAdapter : PagingDataAdapter<Cheese, CheeseViewHolder>(
    Cheese.DIFF_CALLBACK
) {

    /**
     * Called when RecyclerView needs a new [CheeseViewHolder] of the given type to represent
     * an item. This new ViewHolder should be constructed with a new View that can represent the
     * items of the given type. You can either create a new View manually or inflate it from an XML
     * layout file. We return a new instance of [CheeseViewHolder]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [CheeseViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent = parent)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * updates the contents of the [CheeseViewHolder.itemView] to reflect the item at the
     * given position.
     *
     * It retrieves the [Cheese] item for the given `position` to initialize its [Cheese] variable
     * `cheese`. If `cheese` is not null, it binds the data to [CheeseViewHolder] parameter [holder].
     * If the item is `null` (which can happen with placeholders in [PagingDataAdapter], though
     * disabled in our configuration), it calls the [CheeseViewHolder.showPlaceholder] method of
     * [holder] to show a placeholder UI.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given `position` in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese: Cheese? = getItem(position)
        // Note: PagingDataAdapter may present `null` items if placeholders are enabled
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
 * This is the view holder used for the [CheeseAdapter]. It can "bind" to display a [Cheese] object,
 * or just be used to display a flashing empty placeholder view while "waiting" for the [Cheese]
 * objects to be "downloaded". The constructor uses the [LayoutInflater] from the context of our
 * [ViewGroup] parameter `parent` to inflate our layout file `R.layout.cheese_list_item` to be used
 * as the [itemView] for our [RecyclerView.ViewHolder]. This layout file consists of a `LinearLayout`
 * holding an [ImageView] with ID `R.id.image` and a [TextView] with ID `R.id.name`.
 *
 * We initialize our [ImageView] property [image] to the [ImageView] with ID `R.id.image` in the
 * [itemView] we inflated from the layout file with ID `R.layout.cheese_list_item`. We initialize
 * our [TextView] property [name] to the [TextView] with ID `R.id.name` in the [itemView] we
 * inflated from the layout file with ID `R.layout.cheese_list_item`.
 *
 * @param parent The ViewGroup into which the new View will be added after it is bound to
 * an adapter position.
 */
internal class CheeseViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    /* itemView = */ LayoutInflater
        .from(/* context = */ parent.context)
        .inflate(
            /* resource = */ R.layout.cheese_list_item,
            /* root = */ parent,
            /* attachToRoot = */ false
        )
) {
    /**
     * The [ImageView] in our `cheese_list_item.xml` layout with ID [R.id.image]. It is used to
     * display the image of the [Cheese] whose data is in the [Cheese] object that the [bind]
     * method is called with.
     */
    val image: ImageView = itemView.findViewById(R.id.image)

    /**
     * The [TextView] in the item view that is used to display the name of the [Cheese] object.
     */
    val name: TextView = itemView.findViewById(R.id.name)

    /**
     * This is the animation we apply to each of the list items. It animates the alpha value from 1
     * to 0, then back to 1. The animation repeats infinitely until it is manually ended. When the
     * animation is ended the `alpha` value of [itemView] is set to 1.
     */
    private val animation = ObjectAnimator.ofFloat(
        /* target = */ itemView,
        /* property = */ View.ALPHA,
        /* ...values = */ 1f, 0f, 1f
    ).apply {
        repeatCount = ObjectAnimator.INFINITE
        duration = FADE_DURATION
        // Reset the alpha on animation end.
        doOnEnd { itemView.alpha = 1f }
    }

    /**
     * Shows an animated "flashing" empty [itemView] in our cell while we are "waiting" for the
     * [Cheese] objects to be "downloaded". We set the [ObjectAnimator.currentPlayTime] to the
     * milliseconds since boot minus 30 times the adapter position modulo the [FADE_DURATION].
     * We call the [ObjectAnimator.start] method to start the animation. We the set the
     * drawable with resource ID `R.drawable.image_placeholder` as the content of this [ImageView]
     * property [image]. We set the text of the [TextView] property [name] to null. We set the
     * background of the [TextView] property [name] to a drawable with resource ID
     * `R.drawable.text_placeholder`.
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
     * parameter [cheese]. We call the [ObjectAnimator.end] method of our [ObjectAnimator] property
     * [animation] to end the animation. Then we call [Glide.with] to begin a load, to which we
     * chain a [RequestManager.load] to have it load the image with resource ID [Cheese.image] of
     * our [Cheese] parameter [cheese], to which we chain a [Transformation] to have it apply the
     * [CircleCrop] transformation, to which we chain an `into` method to have it load the
     * [ImageView] property [image]. We set the text of the [TextView] property [name] to the
     * [Cheese.name] of our [Cheese] parameter [cheese]. We set the background of the [TextView]
     * property [name] to `0`.
     *
     * @param cheese the [Cheese] object we are supposed to display.
     */
    fun bind(cheese: Cheese) {
        animation.end()
        Glide.with(/* view = */ image)
            .load(/* resourceId = */ cheese.image)
            .transform(/* transformation = */ CircleCrop())
            .into(/* view = */ image)
        name.text = cheese.name
        name.setBackgroundResource(0)
    }
}
