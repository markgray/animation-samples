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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.android.unsplash.R
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.DetailViewBinding
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.ImageSize

/**
 * Adapter for paging detail views.
 */
class DetailViewPagerAdapter(
    activity: AppCompatActivity,
    photos: List<Photo>,
    callback: DetailSharedElementEnterCallback
) : PagerAdapter() {
    private val allPhotos: List<Photo> = photos
    private val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
    private val photoWidth: Int = activity.resources.displayMetrics.widthPixels
    private val host: AppCompatActivity = activity
    private val sharedElementCallback: DetailSharedElementEnterCallback = callback

    override fun getCount(): Int {
        return allPhotos.size
    }

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

    private fun onViewBound(binding: DetailViewBinding) {
        Glide.with(host)
            .load(binding.data!!.getPhotoUrl(photoWidth))
            .placeholder(R.color.placeholder)
            .override(ImageSize.NORMAL[0], ImageSize.NORMAL[1])
            .into(binding.photo)
    }

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