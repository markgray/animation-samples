/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("DEPRECATION")

package com.google.samples.gridtopager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.samples.gridtopager.adapter.ImageData.IMAGE_DRAWABLES
import com.google.samples.gridtopager.fragment.ImageFragment.Companion.newInstance

/**
 * The Adapter used by the [Fragment] `ImagePagerFragment` for its [ViewPager]. We call our super's
 * constructor with the flag `BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT` to indicate that only the current
 * fragment will be in the Lifecycle.State.RESUMED state. All other Fragments are capped at
 * Lifecycle.State.STARTED.
 *
 * @param fragment the `ImagePagerFragment` that constructed us, used in our call to our super's
 * constructor to retrieve a private `FragmentManager` for placing and managing Fragments inside of
 * the Fragment which our super can use as the fragment manager that will interact with this adapter.
 */
class ImagePagerAdapter(
    fragment: Fragment
) : FragmentStatePagerAdapter(fragment.childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    /**
     * Return the number of views available. We just return the size of the [IMAGE_DRAWABLES] array
     * of resource IDs.
     *
     * @return the number of views that are available.
     */
    override fun getCount(): Int {
        return IMAGE_DRAWABLES.size
    }

    /**
     * Return the [Fragment] associated with a specified position. We just return the `ImageFragment`
     * returned by the [newInstance] factory method of `ImageFragment` for the drawable whose resource
     * ID is to be found in the [position] index in [IMAGE_DRAWABLES].
     *
     * @param position the position in our dataset of the `ImageFragment` we are to display.
     * @return a new instance of `ImageFragment` which will display the correct drawable.
     */
    override fun getItem(position: Int): Fragment {
        return newInstance(IMAGE_DRAWABLES[position])
    }
}