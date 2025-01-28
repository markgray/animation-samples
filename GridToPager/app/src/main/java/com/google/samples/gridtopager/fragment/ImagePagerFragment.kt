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
package com.google.samples.gridtopager.fragment

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.google.samples.gridtopager.MainActivity
import com.google.samples.gridtopager.R
import com.google.samples.gridtopager.adapter.ImagePagerAdapter

/**
 * A fragment for displaying a pager of images.
 */
class ImagePagerFragment : Fragment() {
    /**
     * The [ViewPager2] we use for our UI, our [onCreateView] override inflates it from the layout
     * file with ID `R.layout.fragment_pager` (the file layout/fragment_pager.xml). Its ID in that
     * file is `R.id.view_pager`
     */
    private lateinit var viewPager: ViewPager2

    /**
     * Called to have the fragment instantiate its user interface view. We use our [LayoutInflater]
     * parameter [inflater] to inflate the layout file with ID `R.layout.fragment_pager` using our
     * [ViewGroup] parameter [container] for its LayoutParams and set our [ViewPager2] field
     * [viewPager] to the [View] it creates. We then set the `adapter` of [viewPager] to a new
     * instance of [ImagePagerAdapter], set its current item to [MainActivity.currentPosition],
     * and add a [SimpleOnPageChangeListener] to it whose `onPageSelected` override sets
     * [MainActivity.currentPosition] to the position index of the newly selected page.
     *
     * Next we call our [prepareSharedElementTransition] to prepare the shared element transition
     * from and back to the grid fragment. If our [Bundle] parameter [savedInstanceState] is `null`
     * (this is the first time we were called) we cal the [postponeEnterTransition] to postpone the
     * entering Fragment transition until [startPostponedEnterTransition] or the method
     * `executePendingTransactions` of `FragmentManager` has been called (if [savedInstanceState] is
     * not `null` we just had an orientation change so want to skip [postponeEnterTransition]).
     * Finally we return [viewPager] to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any
     * views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewPager = inflater.inflate(R.layout.fragment_pager, container, false) as ViewPager2
        viewPager.adapter = ImagePagerAdapter(this)
        // Set the current position and add a listener that will update the selection coordinator when
        // paging the images.
        viewPager.currentItem = MainActivity.currentPosition
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                MainActivity.currentPosition = position
            }
        })
        prepareSharedElementTransition()

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return viewPager
    }

    /**
     * Prepares the shared element transition from and back to the grid fragment. We initialize our
     * [Transition] variable `val transition` to the instance that the [TransitionInflater] from the
     * the `Context` this fragment is currently associated with inflates from the resource file
     * `R.transition.image_shared_element_transition`. It is a `transitionSet` with a duration of
     * 375, a fast out slow in interpolator which runs together a `changeClipBounds` (captures the
     * View.getClipBounds() before and after the scene change and animates those changes during the
     * transition), `changeTransform` (captures scale and rotation for Views before and after the
     * scene change and animates those changes during the transition) and a `changeBounds` (captures
     * the layout bounds of target views before and after the scene change and animates those changes
     * during the transition). Then we set the Transition that will be used for shared elements
     * transferred into the content Scene (`sharedElementEnterTransition` property) to `transition`.
     * Next we call [setEnterSharedElementCallback] to set the enter transition callback to an
     * anonymous [SharedElementCallback] whose `onMapSharedElements` override locates the [ImageFragment]
     * that is currently visible and maps the first shared element name to the child ImageView.
     */
    private fun prepareSharedElementTransition() {
        val transition: Transition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                /**
                 * Lets the SharedElementCallback adjust the mapping of shared element names to
                 * Views. We initialize our [Fragment] variable `val currentFragment` by having
                 * the `adapter` of our [ViewPager2] field [viewPager] create the page for the
                 * position [MainActivity.currentPosition]. If the view of `currentFragment` is
                 * `null` we return having done nothing, otherwise we set the first shared element
                 * name in our [names] parameter to the `ImageView` with ID `R.id.image` in `view`.
                 *
                 * @param names The names of all shared elements transferred from the calling Activity
                 * or Fragment in the order they were provided.
                 * @param sharedElements The mapping of shared element names to Views. The best guess
                 * will be filled into [sharedElements] based on the transitionNames.
                 */
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the image view at the primary fragment (the ImageFragment that is currently
                    // visible). To locate the fragment, call instantiateItem with the selection position.
                    // At this stage, the method will simply return the fragment at the position and will
                    // not create a new one.
                    val currentFragment = (viewPager.adapter as ImagePagerAdapter? ?: return)
                        .latestFragment as Fragment
                    val view = currentFragment.view ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = view.findViewById(R.id.image)
                }
            })
    }
}
