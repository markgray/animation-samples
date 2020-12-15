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

package com.example.android.motion.ui.demolist


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.R
import com.example.android.motion.model.Demo

/**
 * This is the [ListAdapter] used by `DemoListFragment` for the [RecyclerView] with ID [R.id.demo_list]
 *
 * @param onDemoSelected lambda which will be called with the [Demo] held by the [DemoViewHolder] of
 * the item in the [RecyclerView] which has been clicked.
 */
internal class DemoListAdapter(
    private val onDemoSelected: (demo: Demo) -> Unit
) : ListAdapter<Demo, DemoViewHolder>(DIFF_CALLBACK) {

    /**
     * Called when RecyclerView needs a new [DemoViewHolder] of the given type to represent an item.
     * We return a new instance of [DemoViewHolder] constructed to use our [ViewGroup] parameter
     * [parent] for its layout params which we have configured to use a lambda which calls our
     * [onDemoSelected] field as the `OnClickListener` for the item view held by the [DemoViewHolder],
     * calling [onDemoSelected] with the [Demo] item held by the adapter position of the item clicked.
     *
     * @param parent The ViewGroup into which the new `View` will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [DemoViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        return DemoViewHolder(parent).apply {
            itemView.setOnClickListener {
                onDemoSelected(getItem(adapterPosition))
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position. We just call the [DemoViewHolder.bind] method of our [DemoViewHolder] parameter
     * [holder] with the [Demo] object whose position within the adapter's data set is our
     * parameter [position]. It will update all of the views in the item view's [ViewGroup]
     * to display the [Demo] object it is passed.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Demo>() {

    override fun areItemsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem.packageName == newItem.packageName &&
                oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem == newItem
    }
}

internal class DemoViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.demo_item, parent, false)
) {

    private val label: TextView = itemView.findViewById(R.id.label)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val apis: List<TextView> = listOf(
        itemView.findViewById(R.id.api_1),
        itemView.findViewById(R.id.api_2),
        itemView.findViewById(R.id.api_3),
        itemView.findViewById(R.id.api_4),
        itemView.findViewById(R.id.api_5)
    )

    fun bind(demo: Demo) {
        label.text = demo.label
        description.text = demo.description
        description.isVisible = demo.description != null
        for (i in apis.indices) {
            if (demo.apis.size > i) {
                apis[i].run {
                    text = demo.apis[i]
                    isVisible = true
                }
            } else {
                apis[i].isVisible = false
            }
        }
    }
}
