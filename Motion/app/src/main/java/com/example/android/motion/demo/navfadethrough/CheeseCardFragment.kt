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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_card_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val content: FrameLayout = view.findViewById(R.id.content)
        val card: MaterialCardView = view.findViewById(R.id.card)
        val cardContent: ConstraintLayout = view.findViewById(R.id.card_content)
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val mirror: MirrorView = view.findViewById(R.id.article_mirror)

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

        viewModel.cheese.observe(viewLifecycleOwner) { cheese ->
            name.text = cheese.name
            image.setImageResource(cheese.image)
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
