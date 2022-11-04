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
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.Fade
import androidx.transition.Transition
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_LINEAR_IN
import com.example.android.motion.demo.LARGE_COLLAPSE_DURATION
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.demo.LINEAR_OUT_SLOW_IN
import com.example.android.motion.demo.sharedelement.MirrorView
import com.example.android.motion.model.Cheese
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView

/**
 * A [Fragment] which displays the name and image of the [Cheese] which is stored in the [LiveData]
 * wrapped [Cheese] field [CheeseCardViewModel.cheese].
 */
class CheeseCardFragment : Fragment() {

    /**
     * Our [ViewModel] which contains the [LiveData] wrapped [Cheese] field
     * [CheeseCardViewModel.cheese] whose name and image we display.
     */
    private val viewModel: CheeseCardViewModel by viewModels()

    /**
     * Called to do initial creation of our fragment. This is called after [onAttach] and before
     * [onCreateView]. First we call our super's implementation of `onCreate`. Then we set the
     * [Transition] that will be used to move Views out of the scene when our fragment is removed,
     * hidden, or detached when not popping the back stack (`exitTransition`) to a [Fade.OUT] fade
     * transition whose duration is our constant [LARGE_EXPAND_DURATION] divided by 2 and whose
     * interpolator is our [FAST_OUT_LINEAR_IN] `PathInterpolatorCompat` for a cubic Bezier curve.
     * Finally we set the [Transition] that will be used to move Views in to our scene when
     * returning due to popping a back stack (`reenterTransition`) to a [Fade.IN] fade transition
     * whose duration is our constant [LARGE_COLLAPSE_DURATION] divided by 2, whose `startDelay` is
     * also [LARGE_COLLAPSE_DURATION] divided by 2, and whose interpolator is our [LINEAR_OUT_SLOW_IN]
     * `PathInterpolatorCompat` for a cubic Bezier curve.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Fade(Fade.OUT).apply {
            duration = LARGE_EXPAND_DURATION / 2
            interpolator = FAST_OUT_LINEAR_IN
        }
        reenterTransition = Fade(Fade.IN).apply {
            duration = LARGE_COLLAPSE_DURATION / 2
            startDelay = LARGE_COLLAPSE_DURATION / 2
            interpolator = LINEAR_OUT_SLOW_IN
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return `null`. This will be called between [onCreate] and
     * [onActivityCreated]. It is recommended to **only** inflate the layout in this method and
     * move logic that operates on the returned View to [onViewCreated]. We just return the [View]
     * that our [LayoutInflater] parameter [inflater] inflates from our [R.layout.cheese_card_fragment]
     * layout file using our [ViewGroup] parameter [container] for its `LayoutParams`.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_card_fragment, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. First we locate all of the important widgets in our [View] parameter
     * [view]:
     *
     *  - `val toolbar` the [Toolbar] in our UI with ID [R.id.toolbar] used for our `ActionBar`.
     *  - `val content` the [FrameLayout] in our UI with ID [R.id.content] fills the rest of our
     *  screen holding the [MaterialCardView] (subclass of [FrameLayout]) which holds our [MirrorView]
     *  (used for transitioning) and the [ConstraintLayout] which displays our [Cheese].
     *  - `val card` the [MaterialCardView] in our UI with ID [R.id.card] which holds our [MirrorView]
     *  (used for transitioning) and the [ConstraintLayout] which displays our [Cheese].
     *  - `val cardContent` the [ConstraintLayout] in our UI with ID [R.id.card_content] which holds
     *  an [ImageView] displaying a picture of our [Cheese], a [TextView] holding the name of the
     *  [Cheese], and a [TextView] holding some fake "caption" text "describing" the [Cheese].
     *  - `val image` the [ImageView] in our UI with ID [R.id.image] which holds a picture of our
     *  [Cheese]. It is at left side of the [ConstraintLayout] `val cardContent` mentioned above.
     *  - `val name` the [TextView] in our UI with ID [R.id.name] which holds the name of our [Cheese].
     *  It is at right top side of the [ConstraintLayout] `val cardContent` mentioned above.
     *  - `val mirror` the [MirrorView] in our UI with ID [R.id.article_mirror] which occupies the
     *  same space as the above [ConstraintLayout] `val cardContent` inside of our [MaterialCardView]
     *  `val card`. It is used for the shared element transition to the [CheeseArticleFragment] UI.
     *
     * Next we add an [OnApplyWindowInsetsListener] to the `parent` of our [View] parameter [view]
     * to replace the `onApplyWindowInsets` of [view] with a lambda which updates the `topMargin`
     * [AppBarLayout.LayoutParams] of `toolbar`, and the `left`, `right` and `bottom` padding of
     * `content`.
     *
     * Now we proceed to set the names of the views to be used to identify Views in Transitions:
     *  - `card` [MaterialCardView] with ID [R.id.card] is named "card"
     *  - `cardContent` [ConstraintLayout] with ID [R.id.card_content] is named "card_content"
     *  - `mirror` [MirrorView] with ID [R.id.article_mirror] is named "article"
     *
     * Then we change `cardContent` to be treated as a single entity during Activity Transitions.
     * We add an observer to the [LiveData] wrapped `cheese` property of our [CheeseCardViewModel]
     * field [viewModel] whose lambda sets the text of [TextView] `name` to the `name` property of
     * `cheese`, and the content of the [ImageView] `image` to be the drawable whose resource ID is
     * the `image` property of `cheese`. Finally we set the [View.OnClickListener] of [MaterialCardView]
     * `card` to a lambda which sets its [Cheese] variable `val cheese` to the [LiveData] wrapped
     * `cheese` property of our [CheeseCardViewModel] field [viewModel] or returns if it is `null`,
     * and if it is not `null` it finds a NavController associated with the View that was clicked
     * and uses it to navigate to the [CheeseArticleFragment] passsing the [Cheese.id] property of
     * `cheese` as the [Long] safe args, supplying the [FragmentNavigatorExtras] mapping `card`
     * to [CheeseArticleFragment.TRANSITION_NAME_BACKGROUND], `cardContent` to
     * [CheeseArticleFragment.TRANSITION_NAME_CARD_CONTENT], and `mirror` to
     * [CheeseArticleFragment.TRANSITION_NAME_ARTICLE_CONTENT]. These [FragmentNavigatorExtras]
     * map the views in our UI to the transition names used for the views in [CheeseArticleFragment]
     * for the shared element transition when we navigate to [CheeseArticleFragment].
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val content: FrameLayout = view.findViewById(R.id.content)
        val card: MaterialCardView = view.findViewById(R.id.card)
        val cardContent: ConstraintLayout = view.findViewById(R.id.card_content)
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val mirror: MirrorView = view.findViewById(R.id.article_mirror)

        @Suppress("DEPRECATION") // TODO: Use getInsets(int) with WindowInsetsCompat.Type.systemBars() instead of systemWindowInset*
        ViewCompat.setOnApplyWindowInsetsListener(view.parent as View) { _, insets ->
            toolbar.updateLayoutParams<AppBarLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            content.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }

        ViewCompat.setTransitionName(card, "card")
        ViewCompat.setTransitionName(cardContent, "card_content")
        ViewCompat.setTransitionName(mirror, "article")
        ViewGroupCompat.setTransitionGroup(cardContent, true)

        viewModel.cheese.observe(viewLifecycleOwner) { (_, name1, image1, _, _) ->
            name.text = name1
            image.setImageResource(image1)
        }

        card.setOnClickListener { v ->
            val cheese = viewModel.cheese.value ?: return@setOnClickListener
            v.findNavController().navigate(
                CheeseCardFragmentDirections.actionArticle(cheese.id),
                FragmentNavigatorExtras(
                    card to CheeseArticleFragment.TRANSITION_NAME_BACKGROUND,
                    cardContent to CheeseArticleFragment.TRANSITION_NAME_CARD_CONTENT,
                    mirror to CheeseArticleFragment.TRANSITION_NAME_ARTICLE_CONTENT
                )
            )
        }
    }
}
