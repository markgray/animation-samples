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

package com.example.android.motion.demo.dissolve

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.transition.TransitionManager
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * This demo uses our [Dissolve] animation pattern to transition between different pictures of
 * cheeses.
 *
 * A dissolve creates a smooth transition between elements that completely overlap one another,
 * such as photos inside a card or other container. A foreground element fades in (appears) or
 * out (disappears) to show or hide an element behind it.
 */
class DissolveActivity : AppCompatActivity() {

    /**
     * Our [DissolveViewModel] instance holds the current position in the [Cheese.IMAGES] array that
     * is being displayed in its private [MutableLiveData] property `position`, and the resource ID
     * of the image corresponding to that position in its [LiveData] wrapped property `image`. Our
     * [onCreate] override adds an `Observer` to `image` which transitions the image in the
     * [MaterialCardView] with ID [R.id.card] to the new image whenever the value of `image` changes.
     */
    private val viewModel: DissolveViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.dissolve_activity]. It consists of
     * a `CoordinatorLayout` holding an `AppBarLayout` and a `ConstraintLayout` which holds a
     * `MaterialCardView` with the [ImageView] with ID [R.id.card] with a [MaterialButton] with ID
     * [R.id.next] below it.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState].
     * __Note: Otherwise it is null.__ We do not override [onSaveInstanceState] so do not use it.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dissolve_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val image: ImageView = findViewById(R.id.image)
        val card: MaterialCardView = findViewById(R.id.card)
        val next: MaterialButton = findViewById(R.id.next)

        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(findViewById(R.id.content))

        // This is the transition we use for dissolve effect of the image view.
        val dissolve = Dissolve().apply {
            addTarget(image)
            duration = 200L
            interpolator = FAST_OUT_SLOW_IN
        }
        viewModel.image.observe(this) { resId ->
            // This delays the dissolve to be invoked at the next draw frame.
            TransitionManager.beginDelayedTransition(card, dissolve)
            // Here, we are simply changing the image shown on the image view. The animation is
            // handled by the transition API.
            image.setImageResource(resId)
        }

        card.setOnClickListener { viewModel.nextImage() }
        next.setOnClickListener { viewModel.nextImage() }
    }
}
