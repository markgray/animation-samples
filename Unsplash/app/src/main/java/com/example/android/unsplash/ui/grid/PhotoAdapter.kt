/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.unsplash.ui.grid

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.unsplash.MainActivity
import com.example.android.unsplash.R
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.PhotoItemBinding
import com.example.android.unsplash.ui.ImageSize
import java.util.ArrayList

/**
 * This is the [RecyclerView.Adapter] used to feed data to the [RecyclerView] used by the UI of the
 * activity [MainActivity] for its grid.
 *
 * @param context the [Context] of [MainActivity] in our case.
 * @param photos the dataset of [Photo] objects we are to use.
 */
class PhotoAdapter(
    context: Context,
    private val photos: ArrayList<Photo?>
) : RecyclerView.Adapter<PhotoViewHolder>() {
    /**
     * The requested width in pixels of the image we download, which is the absolute width of the
     * available display size in pixels in our case. The [Photo.getPhotoUrl] method uses the string
     * value of this when it constructs the URL for the image of the [Photo] object.
     */
    private val requestedPhotoWidth: Int = context.resources.displayMetrics.widthPixels

    /**
     * The [LayoutInflater] that we use to inflate our layout file `R.layout.photo_item` into a
     * [PhotoItemBinding] to use in constructing a new instance of [PhotoViewHolder] in our override
     * of [onCreateViewHolder]
     */
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * Called when RecyclerView needs a new [PhotoViewHolder] to represent a [Photo] item. The new
     * [PhotoViewHolder] will be used to display items of the adapter using [onBindViewHolder]. We
     * have the [DataBindingUtil.inflate] method use our [LayoutInflater] field [layoutInflater] to
     * inflate our layout file `R.layout.photo_item` using our [ViewGroup] parameter [parent] for
     * its layout parameters without attaching to it into a [PhotoItemBinding] and return a
     * [PhotoViewHolder] constructed to use that view binding to the caller.
     *
     * @param parent The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [PhotoViewHolder] that holds a view that displays a [Photo] object.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater,
                R.layout.photo_item,
                parent,
                false
            ) as PhotoItemBinding
        )
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the [PhotoViewHolder.itemView] to reflect the item at the given position.
     * We initialize our [PhotoItemBinding] variable `val binding` to the `binding` property of our
     * [PhotoViewHolder] parameter [holder], and our [Photo] variable `val data` to the [Photo] at
     * position [position] in our dataset [photos]. We set the `data` variable of `binding` to `data`,
     * then call the `executePendingBindings` method of `binding` to have it evaluate the pending
     * bindings, updating any Views that have expressions bound to the modified variable. Finally we
     * begin a load with Glide using the `context` [Context] of our [LayoutInflater] field
     * [layoutInflater] which will load the URL that the [Photo.getPhotoUrl] method of the `data`
     * [Photo] variable of the `binding` property of [holder] constructs for that [Photo] and for
     * our [requestedPhotoWidth] requested photo width, using the [Drawable] with resource ID
     * `R.color.placeholder` to display while the image is downloaded, overriding the `Target` width
     * and height with the two entries in the [ImageSize.NORMAL] array, and setting the [ImageView]
     * that the jpeg downloaded will be loaded into to the `photo` [ImageView] of the `binding`
     * property of [holder].
     *
     * @param holder The [PhotoViewHolder] which should be updated to represent the contents of the
     * item at the given [position] in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val binding: PhotoItemBinding = holder.binding
        val data: Photo? = photos[position]
        binding.data = (data ?: return)
        binding.executePendingBindings()
        Glide.with(layoutInflater.context)
            .load((holder.binding.data ?: return)
                .getPhotoUrl(requestedPhotoWidth))
            .placeholder(R.color.placeholder)
            .override(ImageSize.NORMAL[0], ImageSize.NORMAL[1])
            .into(holder.binding.photo)
    }

    /**
     * Returns the total number of items in the data set held by the adapter. We just return the
     * `size` of our [ArrayList] of [Photo] field [photos].
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return photos.size
    }

    /**
     * Return the stable ID for the item at [position]. If [hasStableIds] would return `false` this
     * method should return `NO_ID`. We return the `id` property of the [Photo] at position [position]
     * in our [ArrayList] of [Photo] field [photos].
     *
     * @param position Adapter position to query
     * @return the stable ID of the item at position
     */
    override fun getItemId(position: Int): Long {
        return photos[position]!!.id
    }

}
