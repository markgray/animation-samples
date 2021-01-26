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

package com.example.android.motion.demo.reorder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * [ListAdapter] used by the [ReorderActivity] demo to display [Cheese] objects in its [RecyclerView].
 *
 * @param onItemLongClick lambda which should be used as the `OnLongClickListener` of all of the
 * itemViews displayed in our [RecyclerView].
 */
class CheeseGridAdapter(
    private val onItemLongClick: (holder: RecyclerView.ViewHolder) -> Unit
) : ListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    init {
        /**
         * Indicate that each item in the data set can be represented with a unique identifier
         * of type `Long`. We call with `true` because our `getItemId` override returns the
         * `id` property of the `Cheese` in the position and that property is set to the position
         * of its `Cheese.name` property in the `Cheese.NAMES` list plus 1 guaranteeing a unique ID.
         */
        setHasStableIds(true)
    }

    /**
     * Return the stable ID for the item at [position]. If [hasStableIds] would return `false` this
     * method should return `NO_ID``. The default implementation of this method returns `NO_ID`.
     * The [Cheese.id] property of each [Cheese] in our dataset is stable and unique as it is set to
     * the index of its [Cheese.name] property in the [Cheese.NAMES] list plus 1. So we just return
     * the [Cheese.id] property of the [Cheese] object at position [position] in the current list
     * being used as our dataset by our [ListAdapter] super.
     *
     * @param position Adapter position to query
     * @return the stable ID of the item at position
     */
    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    /**
     * Called when [RecyclerView] needs a new [CheeseViewHolder] of the given type to represent an
     * item. We return a new instance of [CheeseViewHolder] constructed to use our [ViewGroup]
     * parameter [parent] for its layout params after setting the `OnLongClickListener` of its
     * `itemView` to a lambda which calls our [onItemLongClick] field with a reference to the
     * [CheeseViewHolder] then returns `true` to consume the event, and setting the `OnClickListener`
     * of its `itemView` to a lambda which toasts the [Cheese.name] property of the [Cheese] held by
     * the [CheeseViewHolder].
     *
     * @param parent The [ViewGroup] into which the new `View` will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new `View`.
     * @return A new ViewHolder that holds a `View` of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent).apply {
            itemView.setOnLongClickListener {
                onItemLongClick(this)
                true
            }
            itemView.setOnClickListener { v ->
                val cheese = getItem(adapterPosition)
                val context = v.context
                Toast.makeText(
                    context,
                    context.getString(R.string.drag_hint, cheese.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the [CheeseViewHolder.itemView] to reflect the item at the given
     * position. We just call the [CheeseViewHolder.bind] method of our [CheeseViewHolder] parameter
     * [holder] with the [Cheese] at position [position] in our dataset.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * The [RecyclerView.ViewHolder] that is used to display [Cheese] objects from our dataset in our
 * [RecyclerView]. Our constructor just calls our super's constructor with the `View` that the
 * [LayoutInflater] of the context of our [ViewGroup] parameter `parent` inflates from our layout
 * file [R.layout.cheese_staggered_grid_item] when it uses `parent` for the layout params without
 * attaching to it (that `View` becomes our [CheeseViewHolder.itemView]).
 *
 * @param parent the [ViewGroup] that we will be attached to (our [RecyclerView] in our case).
 */
class CheeseViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.cheese_staggered_grid_item, parent, false)
) {

    /**
     * The [ConstraintLayout] in our [itemView] with ID [R.id.cheese], it holds an [ImageView] that
     * displays a picture of a cheese loaded from the resource ID given in the [Cheese.image] property
     * of the [Cheese] we display (one of only 5 available, none of which are likely to actually look
     * like our [Cheese]), and a [TextView] that displays the name of the [Cheese] stored in the
     * [Cheese.name] property of our [Cheese].
     */
    private val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.cheese)

    /**
     * The [ImageView] in our [itemView] with ID [R.id.image], it displays a picture of a cheese
     * loaded from the resource ID given in the [Cheese.image] property of the [Cheese] we display
     * (one of only 5 available, none of which are likely to actually look like our [Cheese]).
     */
    private val image: ImageView = itemView.findViewById(R.id.image)

    /**
     * The [TextView] in our [itemView] with ID [R.id.name], it displays the name of the [Cheese]
     * stored in the [Cheese.name] property of our [Cheese].
     */
    private val name: TextView = itemView.findViewById(R.id.name)

    /**
     * We initialize this [ConstraintSet] variable with a new instance after copying the layout
     * parameters of our [ConstraintLayout] field [constraintLayout] into it. A [ConstraintSet]
     * allows you to define programmatically a set of constraints to be used with [ConstraintLayout],
     * which we do in our [bind] method.
     */
    private val constraintSet = ConstraintSet().apply { clone(constraintLayout) }

    /**
     * Called by the `onBindViewHolder` method of [CheeseGridAdapter] to display our [Cheese]
     * parameter [cheese] in our [itemView]. We configure our [ConstraintSet] field [constraintSet]
     * to set the aspect ratio of the view with ID [R.id.image] (the picture of our [Cheese]) to a
     * ratio that constrains its height (the "H" in the ratio) to the [Cheese.imageWidth] by
     * [Cheese.imageHeight] aspect ratio, then apply [constraintSet] to our [ConstraintLayout] field
     * [constraintLayout]. We then begin a load with [Glide] tied to the lifecycle of our [ImageView]
     * field [image] of the drawable whose resource ID is the [Cheese.image] property of [cheese]
     * and specify that it be loaded into [image] (cancelling any existing loads into the view, and
     * freeing any resources [Glide] may have previously loaded into the view so they may be reused).
     * Finally we set the text of our [TextView] field [name] to the [Cheese.name] property of
     * [cheese].
     *
     * @param cheese the [Cheese] object that we are supposed to display
     */
    fun bind(cheese: Cheese) {
        // The image loaded asynchronously, but the aspect ratio should be set synchronously.
        constraintSet.setDimensionRatio(R.id.image, "H,${cheese.imageWidth}:${cheese.imageHeight}")
        constraintSet.applyTo(constraintLayout)

        // Load the image.
        Glide.with(image).load(cheese.image).into(image)
        name.text = cheese.name
    }
}
