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
                onDemoSelected(getItem(absoluteAdapterPosition))
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the `itemView` of the [DemoViewHolder] to reflect the item at the given
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

/**
 * Callback for calculating the diff between two non-null items in a list.
 */
// It is a constant of sorts
private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Demo>() {

    /**
     * Called to check whether two objects represent the same item. For example, if your items have
     * unique ids, this method should check their id equality.
     *
     * Note: `null` items in the list are assumed to be the same as another `null` item and are
     * assumed to not be the same as a non-`null` item. This callback will not be invoked for
     * either of those cases.
     *
     * We return `true` if the `packageName` and `name` properties of the two [Demo] objects are
     * structurally equal.
     *
     * @param oldItem The [Demo] item in the old list.
     * @param newItem The [Demo] item in the new list.
     * @return `true` if the two items represent the same object or `false` if they are different
     */
    override fun areItemsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem.packageName == newItem.packageName &&
            oldItem.name == newItem.name
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. This method to check equality instead of [equals] so
     * that you can change its behavior depending on your UI. This method is called only if
     * [areItemsTheSame] returns `true` for these items. Note: Two `null` items are assumed to
     * represent the same contents. This callback will not be invoked for this case.
     *
     * We return `true` if the two items are structurally equal.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return `true` if the contents of the items are the same or `false` if they are different.
     */
    override fun areContentsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem == newItem
    }
}

/**
 * The [RecyclerView.ViewHolder] used to hold each of the items displayed by our [RecyclerView].
 * We call our super's constructor with the [ViewGroup] that the [LayoutInflater] from the context
 * of our [ViewGroup] parameter `parent` inflates from our layout file [R.layout.demo_item] using
 * `parent` for the layout params.
 *
 * @param parent the [ViewGroup] which our item view will be added to.
 */
internal class DemoViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.demo_item, parent, false)
) {

    /**
     * The [TextView] which displays the current user-legible textual label associated with our
     * [Demo] item that is read from the `android:label` attribute of the `activity` element in
     * the app's `AndroidManifest.xml` for the activity which demonstrates this [Demo].
     */
    private val label: TextView = itemView.findViewById(R.id.label)

    /**
     * The [TextView] which displays the `android:value` of the `DESCRIPTION` meta-data attribute
     * of the `activity` element in the app's `AndroidManifest.xml` for the activity which
     * demonstrates this [Demo].
     */
    private val description: TextView = itemView.findViewById(R.id.description)

    /**
     * The [List] of [TextView] widgets used in our UI to display the api's that this [Demo] uses
     * which is read from the string array associated with the resource ID which is read from the
     * optional `META_DATA_APIS` meta-data attribute of the `activity` element in the app's
     * `AndroidManifest.xml` for the activity which demonstrates this [Demo].
     */
    private val apis: List<TextView> = listOf(
        itemView.findViewById(R.id.api_1),
        itemView.findViewById(R.id.api_2),
        itemView.findViewById(R.id.api_3),
        itemView.findViewById(R.id.api_4),
        itemView.findViewById(R.id.api_5)
    )

    /**
     * Called to have us update the content of the views in our [ViewGroup] to display the data that
     * is in our [Demo] parameter [demo]. First we set the text of our [TextView] field [label] to
     * the `label` property of [demo] (the user-legible textual label of the activity). Then we set
     * the text of our [TextView] field [description] to the `description` property of [demo] and
     * set the [TextView] to visible only if the `description` property of [demo] is not `null`.
     * Next we loop over `i` through the indices of our [apis] list and if the size of the `apis`
     * array of [demo] is greater than the current value of `i` we set the text of the [TextView]
     * in [apis] at index `i` to the string in the `apis` array of [demo] and set the [TextView] to
     * visible, and once the  size of the `apis` array of [demo] is no longer greater than `i` we
     * set the [TextView] in [apis] at index `i` to invisible.
     *
     * @param demo the [Demo] object whose data we are supposed to display.
     */
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
