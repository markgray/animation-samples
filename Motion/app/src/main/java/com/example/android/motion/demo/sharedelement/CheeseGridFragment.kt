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

package com.example.android.motion.demo.sharedelement

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Explode
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_LINEAR_IN
import com.example.android.motion.demo.LARGE_COLLAPSE_DURATION
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.demo.LINEAR_OUT_SLOW_IN
import com.example.android.motion.demo.plusAssign
import com.example.android.motion.demo.transitionTogether
import com.example.android.motion.model.Cheese
import com.example.android.motion.widget.SpaceDecoration
import com.google.android.material.appbar.AppBarLayout
import java.util.concurrent.TimeUnit

/**
 * Shows a 3 wide grid list of cheeses. Clicking on one of the [Cheese] item views in the grid will
 * navigate to [CheeseDetailFragment] to have it display the "details" of that [Cheese] in a full
 * screen display. The image of the [Cheese] is used as the focal element of the shared element
 * transition to [CheeseDetailFragment].
 */
class CheeseGridFragment : Fragment() {

    /**
     * Our [ViewModel]. It consists of one field: [CheeseGridViewModel.cheeses] which is a [LiveData]
     * wrapped [List] of [Cheese] objects that is initialized to a [MutableLiveData] wrapped copy of
     * the lazily constructed [Cheese.ALL] list of all of the available [Cheese] objects.
     */
    private val viewModel: CheeseGridViewModel by viewModels()

    /**
     * The [CheeseGridAdapter] used to feed data to the [RecyclerView] in our UI with ID [R.id.grid]
     * whose app:layoutManager is a `GridLayoutManager` with a app:spanCount of 3. We initialize
     * with a new instance constructed with a `onReadyToTransition` callback consisting of a lambda
     * which calls [startPostponedEnterTransition] to begin postponed transitions when [Glide]
     * finishes loading the drawable of its [Cheese] into the [ImageView] widget of the itemView.
     */
    private val adapter = CheeseGridAdapter(onReadyToTransition = {
        startPostponedEnterTransition()
    })

    /**
     * Called when the fragment is starting. First we call our super's implementation of `onCreate`.
     * We set the `exitTransition` (the [androidx.transition.Transition] that will be used to move
     * Views out of the scene when the fragment is removed, hidden, or detached when not popping the
     * back stack) to a [TransitionSet] whose duration is [LARGE_EXPAND_DURATION] divided by 2, whose
     * interpolator is [FAST_OUT_LINEAR_IN], to which we add a [Slide] transition whose slide edge
     * direction is [Gravity.TOP], whose mode is [Slide.MODE_OUT] (makes the transition operate on
     * targets that are disappearing) and whose target is the [AppBarLayout] with ID [R.id.app_bar].
     * We also add an [Explode] transition whose mode is [Explode.MODE_OUT] (makes the transition
     * operate on targets that are disappearing) which excludes the [AppBarLayout] with ID
     * [R.id.app_bar] (ie only the grid items are exploded out).
     *
     * Finally we set the `reenterTransition` (the [androidx.transition.Transition] for non-shared
     * elements when we are return back from the detail screen) to a [TransitionSet] whose duration
     * is [LARGE_COLLAPSE_DURATION] divided by 2, whose interpolator is [LINEAR_OUT_SLOW_IN], to
     * which we add a [Slide] transition whose slide edge direction is [Gravity.TOP], whose mode is
     * [Slide.MODE_IN] (makes the transition operate on targets that are appearing) and whose target
     * is the [AppBarLayout] with ID [R.id.app_bar]. We also add an [Explode] transition with a start
     * delay of [LARGE_COLLAPSE_DURATION] divided by 2, whose mode is [Explode.MODE_IN] (makes the
     * transition operate on targets that are appearing) which excludes the [AppBarLayout] with ID
     * [R.id.app_bar] (ie only the grid items are "imploded" in).
     *
     * @param savedInstanceState we do not use this here, but it is used by our [onViewCreated].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the transition to be used for non-shared elements when we are opening the detail
        // screen.
        exitTransition = transitionTogether {
            duration = LARGE_EXPAND_DURATION / 2
            interpolator = FAST_OUT_LINEAR_IN
            // The app bar.
            this += Slide(Gravity.TOP).apply {
                mode = Slide.MODE_OUT
                addTarget(R.id.app_bar)
            }
            // The grid items.
            this += Explode().apply {
                mode = Explode.MODE_OUT
                excludeTarget(R.id.app_bar, true)
            }
        }

        // This is the transition to be used for non-shared elements when we are return back from
        // the detail screen.
        reenterTransition = transitionTogether {
            duration = LARGE_COLLAPSE_DURATION / 2
            interpolator = LINEAR_OUT_SLOW_IN
            // The app bar.
            this += Slide(Gravity.TOP).apply {
                mode = Slide.MODE_IN
                addTarget(R.id.app_bar)
            }
            // The grid items.
            this += Explode().apply {
                // The grid items should start imploding after the app bar is in.
                startDelay = LARGE_COLLAPSE_DURATION / 2
                mode = Explode.MODE_IN
                excludeTarget(R.id.app_bar, true)
            }
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to <**only** inflate the layout in this
     * method and move logic that operates on the returned [View] to [onViewCreated]. We just return
     * the [View] that our [LayoutInflater] parameter [inflater] inflates from our layout file
     * [R.layout.cheese_grid_fragment] when it uses our [ViewGroup] parameter [container] for its
     * LayoutParams without attaching to it. This [View] consists of a `CoordinatorLayout` holding
     * an [AppBarLayout] (for its scrolling effects) holding a [Toolbar] and a [RecyclerView] whose
     * app:layoutManager is a `GridLayoutManager` with an app:spanCount of 3.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_grid_fragment, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. The fragment's view hierarchy is not however attached to its parent
     * at this point. If our [Bundle] parameter [savedInstanceState] is not `null` we call the
     * `restoreInstanceState` method of our [CheeseGridAdapter] field [adapter] to have it restore
     * its `lastSelectedId` field from the value in [savedInstanceState] stored under the key
     * [STATE_LAST_SELECTED_ID] (its `saveInstanceState` method saved this in the [Bundle] that our
     * [onSaveInstanceState] override passed it when our activity was last killed).
     *
     * If the `expectsTransition` property of [adapter] is `true` than we are transitioning back from
     * [CheeseDetailFragment] and we want to postpone the transition animation until the destination
     * item is ready so we call the method [postponeEnterTransition] to postpone the entering Fragment
     * transition for 500 milliseconds after which [postponeEnterTransition] will then call
     * [startPostponedEnterTransition].
     *
     * We initialize our [Toolbar] variable `val toolbar` by finding the view in [view] with the ID
     * [R.id.toolbar] and initialize our [RecyclerView] variable `val grid` by finding the view in
     * [view] with ID [R.id.grid]. We initialize our variable `val gridPadding` to the value of the
     * dimension stored in our `Resources` under the ID [R.dimen.spacing_tiny] (4dp) converted to
     * pixels, and then we set the `OnApplyWindowInsetsListener` of the parent of [view] to a lambda
     * which:
     *  - Updates the `LayoutParams` of `toolbar` to set its `topMargin` to the top system window
     *  inset in pixels
     *  - Updates the `left` padding to `gridPadding` plus the left system window inset in pixels
     *  - Updates the `right` padding to `gridPadding` plus the right system window inset in pixels
     *  - Updates the `bottom` padding to `gridPadding` plus the bottom system window inset in pixels
     *
     * We next add an `ItemDecoration` to `grid` consisting of a [SpaceDecoration] of the value in
     * pixels of the dimension stored under ID [R.dimen.spacing_tiny] in our resources (4dp).
     *
     * We set the `adapter` of `grid` to our [CheeseGridAdapter] field [adapter], then add an observer
     * to the [CheeseGridViewModel.cheeses] field of our [CheeseGridViewModel] field [viewModel] that
     * is controlled by a `LifecycleOwner` that represents our fragments [View] whose lambda submits
     * [CheeseGridViewModel.cheeses] to [adapter] to be diffed and displayed whenever `cheese` changes
     * value.
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here. The [CheeseGridAdapter] for our [RecyclerView]
     * saves its field `lastSelectedId` under the key [STATE_LAST_SELECTED_ID] in the [Bundle]
     * we pass it when our [onSaveInstanceState] calls its `saveInstanceState` method and its
     * `restoreInstanceState` method restores `lastSelectedId` from [savedInstanceState] when
     * we call it from this method.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            adapter.restoreInstanceState(savedInstanceState)
        }
        if (adapter.expectsTransition) {
            // We are transitioning back from CheeseDetailFragment.
            // Postpone the transition animation until the destination item is ready.
            postponeEnterTransition(500L, TimeUnit.MILLISECONDS)
        }

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val grid: RecyclerView = view.findViewById(R.id.grid)

        // Adjust the edge-to-edge display.
        val gridPadding = resources.getDimensionPixelSize(R.dimen.spacing_tiny)
        ViewCompat.setOnApplyWindowInsetsListener(view.parent as View) { _, insets ->
            toolbar.updateLayoutParams<AppBarLayout.LayoutParams> {
                @Suppress("DEPRECATION") // TODO: Fix systemWindowInsetTop deprecation
                topMargin = insets.systemWindowInsetTop
            }
            @Suppress("DEPRECATION") // TODO: Fix systemWindowInset* deprecations
            grid.updatePadding(
                left = gridPadding + insets.systemWindowInsetLeft,
                right = gridPadding + insets.systemWindowInsetRight,
                bottom = gridPadding + insets.systemWindowInsetBottom
            )
            insets
        }

        grid.addItemDecoration(
            SpaceDecoration(resources.getDimensionPixelSize(R.dimen.spacing_tiny))
        )

        grid.adapter = adapter
        viewModel.cheeses.observe(viewLifecycleOwner) { cheeses ->
            adapter.submitList(cheeses)
        }
    }

    /**
     * This method is called before our activity may be killed so that when it comes back some time
     * in the future it can restore its state. If a new instance of the fragment later needs
     * to be created, the data you place in the [Bundle] here will be available in the [Bundle] given
     * to [onCreate], [onCreateView], and [onActivityCreated]. We call the `saveInstanceState` method
     * of our [CheeseGridAdapter] field [adapter] with our [Bundle] parameter [outState] to have it
     * save its field `lastSelectedId` under the key [STATE_LAST_SELECTED_ID] in the [Bundle], then
     * call our super's implementation of `onSaveInstanceState`
     *
     * @param outState [Bundle] in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        adapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
