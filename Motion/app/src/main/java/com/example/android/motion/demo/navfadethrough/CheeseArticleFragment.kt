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

package com.example.android.motion.demo.navfadethrough

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.demo.LARGE_COLLAPSE_DURATION
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.demo.plusAssign
import com.example.android.motion.demo.sharedelement.MirrorView
import com.example.android.motion.demo.sharedelement.SharedFade
import com.example.android.motion.demo.transitionTogether
import com.example.android.motion.model.Cheese
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * A [Fragment] which displays the name and image of the [Cheese] which is stored in the [LiveData]
 * wrapped [Cheese] field [CheeseArticleViewModel.cheese]. That [Cheese] is set to the [Cheese] in
 * the [Cheese.ALL] list with the same `id` property as the safe args [Long] passed us when we are
 * navigated to from [CheeseCardFragment] by setting the [CheeseArticleViewModel.cheeseId] field to
 * that [Long] in our [onCreate] override.
 */
class CheeseArticleFragment : Fragment() {

    companion object {
        /**
         * The transition name used for the root [FrameLayout] in our layout file whose ID is
         * [R.id.background].
         */
        const val TRANSITION_NAME_BACKGROUND: String = "background"

        /**
         * The transition name used for the [CoordinatorLayout] in our layout file whose ID is
         * [R.id.coordinator].
         */
        const val TRANSITION_NAME_CARD_CONTENT: String = "card_content"

        /**
         * The transition name used for the [MirrorView] in our layout file whose ID is
         * [R.id.card_mirror].
         */
        const val TRANSITION_NAME_ARTICLE_CONTENT: String = "article_content"
    }

    /**
     * The [NavArgs] safe args passed to us when [CheeseCardFragment] navigated to us. It contains
     * only a [Long] field `cheeseId` which contains the `id` property of the [Cheese] object that
     * we are supposed to display.
     */
    private val args: CheeseArticleFragmentArgs by navArgs()

    /**
     * Our [ViewModel]. Its [CheeseArticleViewModel.cheese] field contains the [Cheese] we are to
     * display. It is set in our [onCreate] override by setting the [CheeseArticleViewModel.cheeseId]
     * field to the [Long] safe args argument passed us when [CheeseCardFragment] navigates to us.
     * The setter of [CheeseArticleViewModel.cheeseId] sets the private backing field `_cheese` by
     * searching the [Cheese.ALL] list for a [Cheese] with the same `id` property as the value we
     * set [CheeseArticleViewModel.cheeseId] to.
     */
    private val viewModel: CheeseArticleViewModel by viewModels()

    /**
     * Called to do initial creation of our fragment. This is called after [onAttach] and before
     * [onCreateView]. First we call our super's implementation of `onCreate`. Then we set the
     * [Transition] that will be used for shared elements transferred into our content Scene to
     * the [TransitionSet] returned from our [createSharedElementTransition] method with the duration
     * [LARGE_EXPAND_DURATION] while excluding the target whose ID is [R.id.article_mirror] (the
     * [MirrorView] in the layout file for [CheeseCardFragment]), and the [Transition] that will be
     * used for shared elements transferred back during a pop of the back stack to the [TransitionSet]
     * returned from our [createSharedElementTransition] method with the duration [LARGE_COLLAPSE_DURATION]
     * while excluding the target whose ID is [R.id.card_mirror] (the [MirrorView] in our layout file).
     * Finally we set the [CheeseArticleViewModel.cheeseId] field of our [CheeseArticleViewModel]
     * field [viewModel] to the `cheeseId` field of our safe args [CheeseArticleFragmentArgs] field
     * [args] which causing the private backing field `_cheese` of the `cheese` field of [viewModel]
     * to be set to the [Cheese] in the [Cheese.ALL] list with the same `id` property as `cheesID`.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These are the shared element transitions.
        sharedElementEnterTransition =
            createSharedElementTransition(LARGE_EXPAND_DURATION, R.id.article_mirror)
        sharedElementReturnTransition =
            createSharedElementTransition(LARGE_COLLAPSE_DURATION, R.id.card_mirror)

        viewModel.cheeseId = args.cheeseId
    }

    /**
     * Constructs a shared element transition. We return the [TransitionSet] returned by our
     * [transitionTogether] method after we set its duration to our [Long] parameter [duration],
     * set its `interpolator` to [FAST_OUT_SLOW_IN], add a [Transition] constructed from our
     * [SharedFade] class (Transitions between a view and its copy contained in a [MirrorView]),
     * add an instance of [ChangeBounds] (a transition which captures the layout bounds of target
     * views before and after the scene change and animates those changes during the transition),
     * add an instance of [ChangeTransform] (a Transition which captures scale and rotation for
     * Views before and after the scene change and animates those changes during the transition),
     * and exclude the view with resource ID [noTransform] from the animation.
     *
     * @param duration length of animation in milliseconds.
     * @param noTransform resource ID of view to be excluded from the transition.
     * @return a [Transition] suitable to be used for either `sharedElementEnterTransition` or
     * `sharedElementReturnTransition`.
     */
    private fun createSharedElementTransition(duration: Long, @IdRes noTransform: Int): Transition {
        return transitionTogether {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            this += SharedFade()
            this += ChangeBounds()
            this += ChangeTransform()
                // The content is already transformed along with the parent. Exclude it.
                .excludeTarget(noTransform, true)
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to **only** inflate the layout in this
     * method and move logic that operates on the returned View to [onViewCreated]. We just return
     * the [View] that our [LayoutInflater] parameter [inflater] inflates from our layout file
     * [R.layout.cheese_article_fragment] using our [ViewGroup] parameter [container] for its
     * `LayoutParams` without attaching to that [ViewGroup].
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_article_fragment, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. The fragment's view hierarchy is not however attached to its parent
     * at this point. First we initialize some [View] variables by finding the views in our [View]
     * parameter [view]:
     *  - `val toolbar` the [Toolbar] with ID [R.id.toolbar].
     *  - `val name` the [TextView] with ID [R.id.name].
     *  - `val image` the [ImageView] with ID [R.id.image].
     *  - `val scroll` the [NestedScrollView] with ID [R.id.scroll].
     *  - `val content` the [LinearLayout] with ID [R.id.content].
     *  - `val background` the [FrameLayout] with ID [R.id.background].
     *  - `val coordinator` the [CoordinatorLayout] with ID [R.id.coordinator].
     *  - `val mirror` the [MirrorView] with ID [R.id.card_mirror].
     *
     * We now proceed to set the transiton names we use for some of these views:
     *  - `background` is named [TRANSITION_NAME_BACKGROUND]
     *  - `coordinator` is named [TRANSITION_NAME_ARTICLE_CONTENT]
     *  - `mirror` is named [TRANSITION_NAME_CARD_CONTENT]
     *
     * Then we enable `coordinator` as a transition group so that the entire [ViewGroup] will
     * transition together.
     *
     * The next step is to adjust the edge-to-edge display, which we do by setting an
     * [OnApplyWindowInsetsListener] to take over the policy for applying window insets to [view].
     * Its lambda updates the [CollapsingToolbarLayout.LayoutParams] of `toolbar` by setting the
     * top margin to the top system window inset in pixels, updates the [CoordinatorLayout.LayoutParams]
     * of `scroll` by setting the bottom margin to the top system window inset in pixels, and updates
     * the padding of `content` to set the `left` padding to the left system window inset in pixels,
     * the `right` padding to the right system window inset in pixels, and the `bottom` padding to
     * the bottom system window inset in pixels.
     *
     * Then we add an observer to the [CheeseArticleViewModel.cheese] field of [viewModel] whose
     * lambda will when `cheese` transitions to non-`null` set the text of `name` to the `name`
     * property of `cheese` and the content of this [ImageView] image to the drawable whose resource
     * ID is the `image` property of `cheeese`.
     *
     * Finally we set a a listener to respond to navigation events for `toolbar` whose lambda will
     * find a NavController associated with `toolbar` and pop the controller's back stack.
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val scroll: NestedScrollView = view.findViewById(R.id.scroll)
        val content: LinearLayout = view.findViewById(R.id.content)

        val background: FrameLayout = view.findViewById(R.id.background)
        val coordinator: CoordinatorLayout = view.findViewById(R.id.coordinator)
        val mirror: MirrorView = view.findViewById(R.id.card_mirror)

        ViewCompat.setTransitionName(background, TRANSITION_NAME_BACKGROUND)
        ViewCompat.setTransitionName(coordinator, TRANSITION_NAME_ARTICLE_CONTENT)
        ViewCompat.setTransitionName(mirror, TRANSITION_NAME_CARD_CONTENT)
        ViewGroupCompat.setTransitionGroup(coordinator, true)

        // Adjust the edge-to-edge display.
        @Suppress("DEPRECATION")
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            toolbar.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            // The collapsed app bar gets taller by the toolbar's top margin. The CoordinatorLayout
            // has to have a bottom margin of the same amount so that the scrolling content is
            // completely visible.
            scroll.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = insets.systemWindowInsetTop
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
                image.setImageResource(cheese.image)
            }
        }

        toolbar.setNavigationOnClickListener { v ->
            v.findNavController().popBackStack()
        }
    }
}
