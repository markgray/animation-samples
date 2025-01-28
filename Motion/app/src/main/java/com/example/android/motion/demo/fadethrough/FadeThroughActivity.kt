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

package com.example.android.motion.demo.fadethrough

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.demo.MEDIUM_COLLAPSE_DURATION
import com.example.android.motion.demo.MEDIUM_EXPAND_DURATION
import com.example.android.motion.demo.fadeThrough
import com.example.android.motion.ui.EdgeToEdge
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * Layout > Fade through
 *
 * Fade through involves one element fading out completely before a new one fades in. These
 * transitions can be applied to text, icons, and other elements that don't perfectly overlap.
 * This technique lets the background show through during a transition, and it can provide
 * continuity between screens when paired with a shared transformation.
 */
class FadeThroughActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file `R.layout.fade_through_activity`. Next we
     * locate the various widgets in our UI:
     *  - [Toolbar] variable `val toolbar` with ID `R.id.toolbar`
     *  - [MaterialCardView] variable `val card` with ID `R.id.card`
     *  - [ConstraintLayout] variable `val contact` with ID `R.id.card_contact`
     *  - [ConstraintLayout] variable `val cheese` with ID `R.id.card_cheese`
     *  - [MaterialButton] variable `val toggle` with ID `R.id.toggle`
     *  - [ImageView] variable `val icon` with ID `R.id.contact_icon`
     *
     * We then call the [setSupportActionBar] method to set `toolbar` to act as the ActionBar for
     * this Activity's window, and call the various [EdgeToEdge] methods to set up our UI for
     * edge to edge display. We begin a load with [Glide] that will load our `R.drawable.cheese_2`
     * drawable into `icon` after applying a circle crop transformation.
     *
     * We initialize our [Transition] variable `val fadeThrough` with a new instance of our
     * [fadeThrough] transition (it is a [TransitionSet] which performs a [Fade.OUT] followed by
     * a [Fade.IN]).
     *
     * Finally we set the `OnClickListener` of our `toggle` button to a lambda which branches on
     * whether our `contact` [ConstraintLayout] is visible or not:
     *  - `isVisible` is `true`: we use the [TransitionManager] to begin begin a delayed animation of
     *  `card` using our `fadeThrough` [Transition] when the toggling of the visibility of `contact`
     *  to invisible and the toggling of the visibility of `cheese` to visible takes effect.
     *  - `isVisible` is `false`: we use the [TransitionManager] to begin begin a delayed animation of
     *  `card` using our `fadeThrough` [Transition] when the toggling of the visibility of `contact`
     *  to visible and the toggling of the visibility of `cheese` to invisible takes effect.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fade_through_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val card: MaterialCardView = findViewById(R.id.card)
        val contact: ConstraintLayout = findViewById(R.id.card_contact)
        val cheese: ConstraintLayout = findViewById(R.id.card_cheese)
        val toggle: MaterialButton = findViewById(R.id.toggle)
        val icon: ImageView = findViewById(R.id.contact_icon)

        // Set up the layout.
        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(findViewById(R.id.content))
        Glide.with(icon).load(R.drawable.cheese_2).transform(CircleCrop()).into(icon)

        // This is the transition we use for the fade-through effect.
        val fadeThrough: Transition = fadeThrough()

        toggle.setOnClickListener {
            // We are only toggling the visibilities of the card contents here.
            if (contact.isVisible) {
                // Delays the fade-through transition until the layout change caused by changing
                // the visibility of `contact` and `cheese` below takes effect.
                TransitionManager.beginDelayedTransition(
                    card,
                    fadeThrough.setDuration(MEDIUM_EXPAND_DURATION)
                )
                contact.isVisible = false
                cheese.isVisible = true
            } else {
                // Delays the fade-through transition until the layout change caused by changing
                // the visibility of `contact` and `cheese` below takes effect.
                TransitionManager.beginDelayedTransition(
                    card,
                    fadeThrough.setDuration(MEDIUM_COLLAPSE_DURATION)
                )
                contact.isVisible = true
                cheese.isVisible = false
            }
        }
    }

}
