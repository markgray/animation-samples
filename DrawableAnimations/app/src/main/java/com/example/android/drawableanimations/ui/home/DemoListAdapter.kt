/*
 * Copyright 2020 Google LLC
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

package com.example.android.drawableanimations.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.drawableanimations.R
import com.example.android.drawableanimations.databinding.DemoListItemBinding
import com.example.android.drawableanimations.demo.Demo

/**
 * The adapter for the `RecyclerView` used to select between different [Demo] fragments.
 *
 * @param demoClicked the `OnClickListener` used for each of the item views we handle.
 */
internal class DemoListAdapter(
    private val demoClicked: (Demo) -> Unit
) : ListAdapter<Demo, DemoViewHolder>(DIFF_CALLBACK) {
    /**
     * Called when `RecyclerView` needs a new `ViewHolder` of the given type to represent an item.
     * The new `ViewHolder` will be used to display items of the adapter using [onBindViewHolder].
     * Since it will be re-used to display different items in the data set, it is a good idea to
     * cache references to sub views of the View to avoid unnecessary `findViewById` calls. We
     * return a new instance of [DemoViewHolder] constructed to use the [LayoutInflater] from the
     * context of our [ViewGroup] parameter [parent] to inflate the R.layout.demo_list_item layout
     * file using [parent] for the `LayoutParams`, after setting the `OnClickListener` of the
     * `itemView` of the [RecyclerView.ViewHolder] that the super's constructor of [DemoViewHolder]
     * returns to a lambda which calls the [demoClicked] function passed when this adapter was
     * constructed with the the [Demo] item held in the Adapter position of the item represented
     * by the `ViewHolder`.
     *
     * @param parent The [ViewGroup] into which the new `View` will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        return DemoViewHolder(LayoutInflater.from(parent.context), parent).apply {
            itemView.setOnClickListener {
                demoClicked(getItem(adapterPosition))
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the `itemView` of the [DemoViewHolder] to reflect the item at the
     * given position. We just set the text of the `title` property of the `binding` field of
     * our [DemoViewHolder] parameter [holder] to the `title` property of the the [Demo] at the
     * `position` position in our dataset.
     *
     * @param holder The [DemoViewHolder] which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        holder.binding.title.text = getItem(position).title
    }
}

/**
 * The [DiffUtil.ItemCallback] that our [ListAdapter] super uses to calculate the diff between two
 * non-null items in a list to determine if the list has changed.
 */
internal val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Demo>() {

    /**
     * Called to check whether two objects represent the same item. We return `true` if the `title`
     * property of our two parameters are equal.
     *
     * @param oldItem The [Demo] item in the old list.
     * @param newItem The [Demo] item in the new list.
     * @return `true` if the two items represent the same object or `false` if they are different.
     */
    override fun areItemsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem.title == newItem.title
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. This method is called only if [areItemsTheSame] returns
     * `true` for these items. We return `true` if our two parameters are equal.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the contents of the items are the same or false if they are different.
     */
    override fun areContentsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem == newItem
    }
}

/**
 * Our custom [RecyclerView.ViewHolder], our super's constructor sets its `itemView` to the `View`
 * which our [LayoutInflater] parameter `inflater` inflates from our [R.layout.demo_list_item]
 * layout file using our [ViewGroup] parameter `parent` for its layout params.
 *
 * @param inflater the [LayoutInflater] appropriate for our parent's context
 * @param parent the [ViewGroup] we are to be attached to, used for its layout params.
 */
internal class DemoViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.demo_list_item, parent, false)
) {
    /**
     * The [DemoListItemBinding] binding object to our [R.layout.demo_list_item] layout file, whose
     * `bind` extension function we call to bind the `itemView` of our [RecyclerView.ViewHolder]
     * super to it.
     */
    val binding: DemoListItemBinding = DemoListItemBinding.bind(itemView)
}
