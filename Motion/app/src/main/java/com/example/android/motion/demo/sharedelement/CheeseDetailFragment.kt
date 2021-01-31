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

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.demo.LARGE_COLLAPSE_DURATION
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.demo.doOnEnd
import com.example.android.motion.demo.plusAssign
import com.example.android.motion.demo.transitionTogether
import com.example.android.motion.model.Cheese
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.card.MaterialCardView
import java.util.concurrent.TimeUnit

/**
 * Shows detail about a [Cheese] that has been clicked in the `RecyclerView` of [CheeseGridFragment]
 */
class CheeseDetailFragment : Fragment() {

    /**
     * The Transition names used by both [CheeseDetailFragment] and the [CheeseGridAdapter] that is
     * used for the `RecyclerView` of [CheeseGridFragment]
     */
    companion object {
        /**
         * The transition name used for the [ImageView] loaded from the [Drawable] whose resource ID
         * is in the [Cheese.image] field of the [Cheese] displayed in the UI of [CheeseDetailFragment]
         * in the view with ID [R.id.image] and in the view with ID [R.id.image] of the `itemView`
         * that was clicked in the `RecyclerView` of [CheeseGridFragment] to transition to the
         * [CheeseDetailFragment].
         */
        const val TRANSITION_NAME_IMAGE = "image"

        /**
         * The transition name used for the [TextView] displaying the [Cheese.name] field of the
         * [Cheese] in the view with ID [R.id.name] of the `itemView` that was clicked in the
         * `RecyclerView` of [CheeseGridFragment] to transition to the [CheeseDetailFragment] and
         * the [MirrorView] with ID [R.id.dummy_name] "displayed" in the UI of [CheeseDetailFragment]
         * which is only used for transitioning (the "real" [TextView] displaying the [Cheese.name]
         * field in [CheeseDetailFragment] is its own view with ID [R.id.name]).
         */
        const val TRANSITION_NAME_NAME = "name"

        /**
         * The transition name used for the [Toolbar] with ID [R.id.toolbar] in our UI and the
         * [MirrorView] with ID [R.id.toolbar] in the `itemView` that was clicked in the `RecyclerView`
         * of [CheeseGridFragment] to transition to the [CheeseDetailFragment] (used for transitioning
         * between real [Toolbar] and the place holder [MirrorView] in the `itemView`)
         */
        const val TRANSITION_NAME_TOOLBAR = "toolbar"

        /**
         * The transition name used for the [CoordinatorLayout] with ID [R.id.detail] in our UI and
         * the [MaterialCardView] with ID [R.id.card] in the `itemView` that was clicked in the
         * `RecyclerView` of [CheeseGridFragment] to transition to the [CheeseDetailFragment]. Used
         * as the epicenter of all the fragment transitions, including Explode for non-shared elements.
         */
        const val TRANSITION_NAME_BACKGROUND = "background"

        /**
         * The transition name used for the [MirrorView] with ID [R.id.favorite] in our UI and
         * the [ImageView] with ID [R.id.favorite] in the `itemView` that was clicked in the
         * `RecyclerView` of [CheeseGridFragment] to transition to the [CheeseDetailFragment].
         * Used for transitioning between the real [ImageView] in the `itemView` and the place
         * holder [MirrorView] in our UI.
         */
        const val TRANSITION_NAME_FAVORITE = "favorite"

        /**
         * The transition name used for the [MirrorView] with ID [R.id.bookmark] in our UI and
         * the [ImageView] with ID [R.id.bookmark] in the `itemView` that was clicked in the
         * `RecyclerView` of [CheeseGridFragment] to transition to the [CheeseDetailFragment].
         * Used for transitioning between the real [ImageView] in the `itemView` and the place
         * holder [MirrorView] in our UI.
         */
        const val TRANSITION_NAME_BOOKMARK = "bookmark"

        /**
         * The transition name used for the [MirrorView] with ID [R.id.share] in our UI and
         * the [ImageView] with ID [R.id.share] in the `itemView` that was clicked in the
         * `RecyclerView` of [CheeseGridFragment] to transition to the [CheeseDetailFragment].
         * Used for transitioning between the real [ImageView] in the `itemView` and the place holder
         * [MirrorView] in our UI.
         */
        const val TRANSITION_NAME_SHARE = "share"

        /**
         * The transition name used for the [NestedScrollView] with ID [R.id.scroll] in our UI and
         * the [MirrorView] with ID [R.id.body] in the `itemView` that was clicked in the
         * `RecyclerView` of [CheeseGridFragment] to transition to the [CheeseDetailFragment].
         * Used for transitioning from the real [ImageView] in the `itemView` to the place holder
         * [MirrorView] in our UI. Used for transitioning between our real [NestedScrollView] and
         * the place holder [MirrorView] in the `itemView`.
         */
        const val TRANSITION_NAME_BODY = "body"
    }

    /**
     * The safe args passed us when [CheeseGridFragment] navigates to us. It contains one [Long]
     * field [CheeseDetailFragmentArgs.cheeseId] which holds the [Cheese.id] property of the [Cheese]
     * that was displayed in the `itemView` that was clicked in the [CheeseGridFragment] `RecylerView`
     * causing the navigation to [CheeseDetailFragment].
     */
    private val args: CheeseDetailFragmentArgs by navArgs()

    /**
     * The [CheeseDetailViewModel] we use as our [ViewModel] to hold our data. It holds a private
     * [MutableLiveData] `_cheese` field with public access provided using its `cheese` property,
     * as well as a `cheeseId` property whose getter retrieves the [Cheese.id] property of `_cheese`
     * if `_cheese` is not `null` or `null` if it is, and whose setter sets the value of `_cheese`
     * to the [Cheese] in the [Cheese.ALL] list whose [Cheese.id] property is equal to the `value`
     * that on is setting `cheeseId` to.
     */
    private val viewModel: CheeseDetailViewModel by viewModels()

    /**
     * Called when the [Fragment] is starting. First we call our super's implementation of `onCreate`.
     * We then set the Transition that will be used for shared elements transferred into the content
     * Scene to the [Transition] returned by our [createSharedElementTransition] method for a duration
     * of [LARGE_EXPAND_DURATION] (300 milliseconds) and set the Transition that will be used for
     * shared elements transferred back during a pop of the back stack to the [Transition] returned
     * by our [createSharedElementTransition] method for a duration of [LARGE_COLLAPSE_DURATION]
     * (250 milliseconds). Finally we call the setter of the [CheeseDetailViewModel.cheeseId] property
     * of our [CheeseDetailViewModel] field [viewModel] to have it set [CheeseDetailViewModel] field
     * `_cheese` to the [Cheese] whose [Cheese.id] property is that same as the `cheeseId` field of
     * the [CheeseDetailFragmentArgs] safe args passed us from our [args] field (that [Cheese] will
     * be the same [Cheese] object that was displayed in the `itemView` that was clicked in the
     * [CheeseGridFragment] `RecylerView` to get us here).
     *
     * @param savedInstanceState we ignore.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These are the shared element transitions.
        sharedElementEnterTransition = createSharedElementTransition(LARGE_EXPAND_DURATION)
        sharedElementReturnTransition = createSharedElementTransition(LARGE_COLLAPSE_DURATION)

        viewModel.cheeseId = args.cheeseId
    }

    /**
     * Returns a [TransitionSet] whose ordering is [TransitionSet.ORDERING_TOGETHER], whose duration
     * is [duration] milliseconds, whose `interpolator` is [FAST_OUT_SLOW_IN] which animates using
     * a [SharedFade], a [ChangeImageTransform], a [ChangeBounds], and a [ChangeTransform] Transition:
     *  - [SharedFade] Transitions between a view and its copy by [MirrorView]. This can be typically
     *  used in a shared element transition where the shared element is necessary only during the
     *  animation. The shared element needs to exist and laid out on both sides of the transition in
     *  order to animate between them, but it can be wasteful to create the exact same view on the
     *  side where it is not functional. This transition matches the substance and its mirror and
     *  animates between them. Depending on which of the start or the end state is the substance of
     *  [MirrorView], the animation either fades into it or fades out of it.
     *  - [ChangeImageTransform] This Transition captures an ImageView's matrix before and after the
     *  scene change and animates it during the transition. In combination with [ChangeBounds],
     *  [ChangeImageTransform] allows ImageViews that change size, shape, or ImageView.ScaleType to
     *  animate contents smoothly.
     *  - [ChangeBounds] This transition captures the layout bounds of target views before and after
     *  the scene change and animates those changes during the transition.
     *  - [ChangeTransform] This Transition captures scale and rotation for Views before and after
     *  the scene change and animates those changes during the transition. A change in parent is
     *  handled as well by capturing the transforms from the parent before and after the scene
     *  change and animating those during the transition.
     *
     * @param duration the duration in milliseconds of the [Transition]
     * @return a [Transition] with duration [duration] usable for both [setSharedElementEnterTransition]
     * and [setSharedElementReturnTransition] (aka in kotlin: `sharedElementEnterTransition`, and
     * `sharedElementReturnTransition`).
     */
    private fun createSharedElementTransition(duration: Long): Transition {
        return transitionTogether {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            this += SharedFade()
            this += ChangeImageTransform()
            this += ChangeBounds()
            this += ChangeTransform()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // We are expecting an enter transition from the grid fragment.
        postponeEnterTransition(500L, TimeUnit.MILLISECONDS)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val dummyName: View = view.findViewById(R.id.dummy_name)
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val scroll: NestedScrollView = view.findViewById(R.id.scroll)
        val content: LinearLayout = view.findViewById(R.id.content)
        val coordinator: CoordinatorLayout = view.findViewById(R.id.detail)
        val favorite: View = view.findViewById(R.id.favorite)
        val bookmark: View = view.findViewById(R.id.bookmark)
        val share: View = view.findViewById(R.id.share)

        // Transition names. Note that they don't need to match with the names of the selected grid
        // item. They only have to be unique in this fragment.
        ViewCompat.setTransitionName(image, TRANSITION_NAME_IMAGE)
        ViewCompat.setTransitionName(dummyName, TRANSITION_NAME_NAME)
        ViewCompat.setTransitionName(toolbar, TRANSITION_NAME_TOOLBAR)
        ViewCompat.setTransitionName(coordinator, TRANSITION_NAME_BACKGROUND)
        ViewCompat.setTransitionName(favorite, TRANSITION_NAME_FAVORITE)
        ViewCompat.setTransitionName(bookmark, TRANSITION_NAME_BOOKMARK)
        ViewCompat.setTransitionName(share, TRANSITION_NAME_SHARE)
        ViewCompat.setTransitionName(scroll, TRANSITION_NAME_BODY)
        ViewGroupCompat.setTransitionGroup(scroll, true)

        // Adjust the edge-to-edge display.
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            toolbar.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            content.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }

        viewModel.cheese.observe(viewLifecycleOwner) { cheese ->
            if (cheese != null) {
                name.text = cheese.name
                Glide
                    .with(image)
                    .load(cheese.image)
                    // It is important to call `dontTransform` here.
                    // Glide, as well as many other image loading libraries, crops the image before
                    // setting it to an ImageView and caching it. As a result, the image will have
                    // a different aspect ratio than the original image. This is problematic for
                    // `ChangeImageTransform` during shared element transitions because it expects
                    // the image to have the same aspect ratio both on the start and the end states.
                    // `dontTransform` suppresses the cropping.
                    .dontTransform()
                    // We can start the transition when the image is loaded.
                    .doOnEnd(this::startPostponedEnterTransition)
                    .into(image)
            }
        }

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
