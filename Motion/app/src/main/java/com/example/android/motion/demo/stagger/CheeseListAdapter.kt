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

package com.example.android.motion.demo.stagger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewGroupCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * The [ListAdapter] used as the adapter for the [RecyclerView] displayed by [StaggerActivity].
 */
class CheeseListAdapter : ListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    /**
     * Called when [RecyclerView] needs a new [CheeseViewHolder] of the given type to represent
     * an item. We just return a new instance of [CheeseViewHolder] constructed to use our
     * [ViewGroup] parameter [parent] for its context.
     *
     * @param parent The [ViewGroup] into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [CheeseViewHolder] that holds a [View] of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent)
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the [View] field [CheeseViewHolder.itemView] of its [holder] parameter
     * to reflect the item at the given position. We just call the [CheeseViewHolder.bind] method of
     * our parameter [holder] with the [Cheese] object in position [position] in our dataset.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given [position] in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * Our custom [RecyclerView.ViewHolder], designed to display a [Cheese] object in a [RecyclerView].
 * Our constructor just calls our super's constructor with the [View] that the [LayoutInflater] from
 * context of our [ViewGroup] parameter `parent` inflates from the layout file with resource ID
 * [R.layout.cheese_list_item] when it uses `parent` for its `LayoutParams` without attaching to it.
 *
 * @param parent the [ViewGroup] that we will be attached to.
 */
class CheeseViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.cheese_list_item, parent, false)
) {

    private val image: ImageView = itemView.findViewById(R.id.image)
    private val name: TextView = itemView.findViewById(R.id.name)

    init {
        ViewGroupCompat.setTransitionGroup(itemView as ViewGroup, true)
    }

    fun bind(cheese: Cheese) {
        Glide.with(image).load(cheese.image).transform(CircleCrop()).into(image)
        name.text = cheese.name
    }
}
