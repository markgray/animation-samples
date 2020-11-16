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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.android.drawableanimations.R
import com.example.android.drawableanimations.databinding.HomeFragmentBinding
import com.example.android.drawableanimations.demo.Demo
import com.example.android.drawableanimations.demo.animated.AnimatedFragment
import com.example.android.drawableanimations.demo.seekable.SeekableFragment
import com.example.android.drawableanimations.viewBindings

/**
 * This is the starting [Fragment] of our demo, its UI consists of just a `RecyclerView` whose
 * child view's `ViewHolder` loads either [AnimatedFragment] or [SeekableFragment] when the view
 * is clicked. We used the alternate constructor of our [Fragment] super class to have it inflate
 * our layout file [R.layout.home_fragment] in its [onCreateView] method and set it to be our
 * content view.
 */
class HomeFragment : Fragment(R.layout.home_fragment) {

    /**
     * The [HomeFragmentBinding] view binding created from our layout file layout/home_fragment.xml
     * `viewBindings` is an extension function of [Fragment] created in `ViewBindingDelegates.kt`
     */
    private val binding by viewBindings(HomeFragmentBinding::bind)

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once they
     * know their view hierarchy has been completely created. The fragment's view hierarchy is not
     * however attached to its parent at this point.
     *
     * We initialize our [DemoListAdapter] variable `val adapter` with a new instance constructed
     * to use a lambda for the `demoClicked` field that is used as the `OnClickListener` for the
     * item view held by the `DemoViewHolder` class. When the item is clicked the lambda uses the
     * `FragmentActivity` this fragment is currently associated with to fetch the `FragmentManager`
     * for interacting with fragments associated with the activity to commit a `FragmentTransaction`
     * with the [Fragment] operations to replace the fragment in the container with ID [R.id.main]
     * with the [Fragment] that the `createFragment` field of the [Demo] object held by the
     * `DemoViewHolder` that was clicked (either [AnimatedFragment], or [SeekableFragment] in our
     * case), and adds the `FragmentTransaction` to the back stack. Then the lambda sets the title
     * of the `FragmentActivity` to the `title` field of the [Demo] that was clicked.
     *
     * Having constructed our [DemoListAdapter] we set the adapter of the `RecyclerView` in our
     * [HomeFragmentBinding] field [binding] whose ID is `list` to this `adapter`. We then submit
     * a list of [Demo] objects to `adapter` for our fragments [AnimatedFragment] (titled
     * "AnimatedVectorDrawableCompat") and [SeekableFragment] (titled "SeekableAnimatedVectorDrawable")
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DemoListAdapter { demo ->
            activity?.let { activity ->
                activity.supportFragmentManager.commit {
                    replace(R.id.main, demo.createFragment())
                    addToBackStack(null)
                }
                activity.title = demo.title
            }
        }
        binding.list.adapter = adapter
        adapter.submitList(listOf(
            Demo("AnimatedVectorDrawableCompat") { AnimatedFragment() },
            Demo("SeekableAnimatedVectorDrawable") { SeekableFragment() }
        ))
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view hierarchy
     * instantiated. We call our super's implementation of `onActivityCreated` then set the
     * title of our `FragmentActivity` to "DrawableAnimations".
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.app_name)
    }
}
