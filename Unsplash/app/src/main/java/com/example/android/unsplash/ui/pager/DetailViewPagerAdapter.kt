/*
 * Copyright (C) 2019 Google Inc. All Rights Reserved.
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
package com.example.android.unsplash.ui.pager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.android.unsplash.R
import com.example.android.unsplash.DetailActivity
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.DetailViewBinding
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.ImageSize

/**
 * Adapter for paging detail views in the UI of [DetailActivity].
 *
 * @param activity the [AppCompatActivity] to use for accessing resources, [DetailActivity] in our case.
 * @param photos the [List] of [Photo] objects we should use as our dataset.
 * @param callback the [DetailSharedElementEnterCallback] to use as our `EnterSharedElementCallback`
 */
class DetailViewPagerAdapter(
    activity: AppCompatActivity,
    photos: List<Photo>,
    callback: DetailSharedElementEnterCallback
) : PagerAdapter() {
    /**
     * Our dataset.
     */
    private val allPhotos: List<Photo> = photos

    /**
     * The [LayoutInflater] from the [Context] of the [AppCompatActivity] parameter `activity` of
     * our constructor which we use to inflate our layout file [R.layout.detail_view] into a
     * [DetailViewBinding] binding object.
     */
    private val layoutInflater: LayoutInflater = LayoutInflater.from(activity)

    /**
     * The absolute width of the available display size in pixels which we use in our [onViewBound]
     * method in a call to the [Photo.getPhotoUrl] method of the [Photo] object to be displayed in
     * order to generate an URL that [Glide] can use to download the proper image.
     */
    private val photoWidth: Int = activity.resources.displayMetrics.widthPixels

    /**
     * The [AppCompatActivity] to use for accessing resources, [DetailActivity] in our case.
     */
    private val host: AppCompatActivity = activity

    /**
     * The [DetailSharedElementEnterCallback] to use as our `EnterSharedElementCallback`
     */
    private val sharedElementCallback: DetailSharedElementEnterCallback = callback

    /**
     * Return the number of views available. We just return the size of our [allPhotos] dataset.
     *
     * @return the number of views available.
     */
    override fun getCount(): Int {
        return allPhotos.size
    }

    /**
     * Create the page for the given position. The adapter is responsible for adding the view to the
     * container given here, although it only must ensure this is done by the time it returns from
     * [finishUpdate]. We initialize our [DetailViewBinding] variable `val binding` by having the
     * [DataBindingUtil.inflate] method use our [LayoutInflater] field [layoutInflater] to inflate
     * our layout file [R.layout.detail_view] using our [ViewGroup] parameter [container] for layout
     * params without attaching to it. We set the `data` variable of `binding` to the [Photo] in
     * position [position] in our dataset [allPhotos], call our [onViewBound] method with `binding`
     * to have it start a [Glide] load of the image of the [Photo], and then call the method
     * `executePendingBindings` of `binding` to have it evaluate the pending binding, updating any
     * Views that have expressions bound to the modified variable. Finally we call the `addView`
     * method of our [ViewGroup] parameter [container] to have it add the outermost [View] in the
     * layout file associated with the [DetailViewBinding] variable `binding` as a child view, and
     * then we return `binding` to the caller.
     *
     * @param container The containing [ViewGroup] in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page. This does not
     * need to be a [View], but can be some other container of the page.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: DetailViewBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.detail_view,
            container,
            false
        )
        binding.data = allPhotos[position]
        onViewBound(binding)
        binding.executePendingBindings()
        container.addView(binding.root)
        return binding
    }

    /**
     * Starts a [Glide] load of the image associated with the [Photo] object in the `data` variable
     * of our [DetailViewBinding] parameter [binding] into the `photo` [ImageView] of [binding]. We
     * begin a load with [Glide] that will tied to the lifecycle of our [AppCompatActivity] field
     * [host] and that uses its default options, and have it load the URL that the [Photo.getPhotoUrl]
     * method constructs of the `data` variable [Photo] of [binding] given our field [photoWidth].
     * We set the [R.color.placeholder] color as the Drawable resource to display while the image is
     * loading, override the target width and height with the two values in the [ImageSize.NORMAL]
     * array, and specify the `photo` [ImageView] of [binding] to be the [ImageView] that the image
     * will be loaded into.
     *
     * @param binding the [DetailViewBinding] binding object of the page that is being loaded into.
     */
    private fun onViewBound(binding: DetailViewBinding) {
        Glide.with(host)
            .load(binding.data!!.getPhotoUrl(photoWidth))
            .placeholder(R.color.placeholder)
            .override(ImageSize.NORMAL[0], ImageSize.NORMAL[1])
            .into(binding.photo)
    }

    /**
     * Called to inform the adapter of which item is currently considered to be the "primary", that
     * is the one show to the user as the current page.
     *
     * @param container The containing [ViewGroup] which is displaying this adapter's page views.
     * @param position The page position that is now the primary.
     * @param binding The same object that was returned by [instantiateItem].
     */
    override fun setPrimaryItem(container: ViewGroup, position: Int, binding: Any) {
        if (binding is DetailViewBinding) {
            sharedElementCallback.setBinding(binding)
        }
    }

    override fun isViewFromObject(view: View, binding: Any): Boolean {
        return (binding is DetailViewBinding
            && view == binding.root)
    }

    override fun destroyItem(container: ViewGroup, position: Int, binding: Any) {
        container.removeView((binding as DetailViewBinding).root)
    }

}