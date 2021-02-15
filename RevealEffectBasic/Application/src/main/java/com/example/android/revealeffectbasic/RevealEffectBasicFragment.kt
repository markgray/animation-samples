/*
 * Copyright 2014 The Android Open Source Project
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
package com.example.android.revealeffectbasic

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.android.common.logger.Log.d
import kotlin.math.hypot

/**
 * This sample shows a view that is revealed when a button is clicked.
 */
class RevealEffectBasicFragment : Fragment() {
    /**
     * Called to do initial creation of a fragment. This is called after [onAttach] and before
     * [onCreateView]. Note that this can be called while the fragment's activity is still in the
     * process of being created. As such, you can not rely on things like the activity's content
     * view hierarchy being initialized at this point.  If you want to do work once the activity
     * itself is created, see [onActivityCreated]. Any restored child fragments will be created
     * before the base [Fragment.onCreate] method returns.
     *
     * First we call our super's implementation of `onCreate`, then we call [setHasOptionsMenu] with
     * `true` to report that this fragment would like to participate in populating the options menu
     * by receiving a call to [onCreateOptionsMenu] and related methods.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state. We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to **only** inflate the layout in this
     * method and move logic that operates on the returned View to [onViewCreated]. We return the
     * [View] that our [LayoutInflater] parameter [inflater] inflates from our layout file
     * [R.layout.reveal_effect_basic] when it uses our [ViewGroup] parameter [container] for its
     * `LayoutParams` without attaching to it. Our layout file consists of a `RelativeLayout` root
     * [ViewGroup] which hold a [View] with ID [R.id.circle] and a "Reveal" [Button] with the ID
     * [R.id.button] which the user can click to run our animation demo in the [View] with ID
     * [R.id.circle].
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-`null`, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself, but this
     * can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed from a
     * previous saved state as given here. We do not override [onSaveInstanceState] so do not use.
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reveal_effect_basic, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once they
     * know their view hierarchy has been completely created. The fragment's view hierarchy is not
     * however attached to its parent at this point. We initialize our [View] variable `val shape`
     * by finding the [View] in our [View] parameter [view] with ID [R.id.circle] and our [Button]
     * variable `val button` by finding the [Button] in [view] with ID [R.id.button]. We then set
     * the [View.setOnClickListener] of `button` to a lambda which initializes its [Animator]
     * variable `val circularReveal` to the [Animator] which can animate a clipping circle that the
     * [ViewAnimationUtils.createCircularReveal] method constructs for `shape` with center at
     * (0,0), starting radius of 0f, and ending radius the hypotenuse of the `width` and `height` of
     * `shape`. We then set the interpolator of `circularReveal` to an [AccelerateDecelerateInterpolator]
     * and start `circularReveal` running. Having finished setting up the [View.OnClickListener] for
     * `button` we call our super's implementation of `onViewCreated`.
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here. We do not override [onSaveInstanceState] so do not use.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val shape: View = view.findViewById(R.id.circle)
        val button: Button = view.findViewById(R.id.button)
        // Set a listener to reveal the view when clicked.
        button.setOnClickListener { // Create a reveal {@link Animator} that starts clipping the view from
            // the top left corner until the whole view is covered.
            val circularReveal: Animator = ViewAnimationUtils.createCircularReveal(
                shape,
                0,
                0, 0f,
                hypot(shape.width.toDouble(), shape.height.toDouble()).toFloat()
            )
            circularReveal.interpolator = AccelerateDecelerateInterpolator()

            // Finally start the animation
            circularReveal.start()
            d(TAG, "Starting Reveal animation")
        }
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * TAG used for logging
         */
        private const val TAG = "RevealEffectBasicFragment"
    }
}