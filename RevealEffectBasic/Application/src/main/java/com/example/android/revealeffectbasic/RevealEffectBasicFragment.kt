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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.android.common.logger.Log
import kotlin.math.hypot

/**
 * This sample shows a view that is revealed when a button is clicked.
 */
class RevealEffectBasicFragment : Fragment(R.layout.reveal_effect_basic) {

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created. The fragment's view hierarchy
     * is not however attached to its parent at this point.
     *
     * In this sample, this method is used to set up the circular reveal animation.
     *
     * First we call our super's implementation of `onViewCreated`, then we initialize our [View]
     * variable `val shape` by finding the view with ID `R.id.circle` in our UI, and initialize our
     * [Button] variable `val button` by finding the view with ID `R.id.button` in our UI. We set
     * the [View.OnClickListener] of `button` to a new instance of an anonymous class that
     * implements [View.OnClickListener]. When `button` is clicked we initialize our [Animator]
     * variable `val circularReveal` by using the [ViewAnimationUtils] class to create a circular
     * reveal animation that starts clipping the view from the top left corner until the whole view
     * is covered. We set the [Animator.setInterpolator] of `circularReveal` to an
     * [AccelerateDecelerateInterpolator] and then start the animation, and log a message to the
     * console.
     *
     * Next we use the [requireActivity] method to add an anonymous [MenuProvider] to our activity
     * which does nothing in `onCreateMenu` and returns `false` in `onMenuItemSelected`.
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shape: View = view.findViewById(R.id.circle)
        val button: Button = view.findViewById(R.id.button)
        // Set a listener to reveal the view when clicked.
        button.setOnClickListener {
            // Create a reveal {@link Animator} that starts clipping the view from
            // the top left corner until the whole view is covered.
            val circularReveal: Animator = ViewAnimationUtils.createCircularReveal(
                shape,
                0,
                0,
                0f,
                hypot(x = shape.width.toDouble(), y = shape.height.toDouble()).toFloat()
            )
            circularReveal.interpolator = AccelerateDecelerateInterpolator()

            // Finally start the animation
            circularReveal.start()
            Log.d(TAG, "Starting Reveal animation")
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            /**
             * Initialize the contents of the Fragment's standard options menu.
             *
             * @param menu The options menu in which you place your items.
             * @param menuInflater The inflater to be used to inflate the menu.
             */
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here if needed.
            }

            /**
             * This hook is called whenever an item in your options menu is selected. The default
             * implementation simply returns `false` to have the normal processing happen (calling
             * the item's `Runnable` or sending a message to its `Handler` as appropriate). You can
             * use this method for any items for which you would like to do processing without those
             * other facilities.
             *
             * Derived classes should call through to the base class for it to perform the default
             * menu handling.
             *
             * @param menuItem The menu item that was selected.
             * @return Return `false` to allow normal menu processing to proceed, `true` to consume
             * it here.
             */
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    companion object {
        /**
         * TAG used for logging
         */
        private const val TAG = "RevealEffectBasicFragment"
    }
}
