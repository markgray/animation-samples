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

import android.animation.TimeInterpolator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
     * We initialize our [Toolbar] variable `val toolbar` by finding the view with ID [R.id.toolbar],
     * our [ImageView] variable `val image` by finding the view with ID [R.id.image], our
     * [MaterialCardView] variable `val card` by finding the view with ID [R.id.card], and our
     * [MaterialButton] variable `val next` by finding the view with ID [R.id.next]. We call the
     * method [setSupportActionBar] to set `toolbar` to act as the `ActionBar` for our activities
     * window. We call our [EdgeToEdge.setUpRoot] method to set the `systemUiVisibility` of the
     * [ViewGroup] with ID [R.id.root] (the "root' `CoordinatorLayout` of our layout file) to
     * [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] (requests that the system navigation be hidden)
     * and [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] (we would like a stable view of the content insets
     * given, ie. the insets seen there will always represent the worst case that the application
     * can expect as a continuous state). We call the [EdgeToEdge.setUpAppBar] with the view with
     * ID [R.id.app_bar] (the `AppBarLayout` in our layout file) and `toolbar` to have it apply
     * padding to the `AppBarLayout` and [Toolbar] variable `toolbar`. We call our method
     * [EdgeToEdge.setUpScrollingContent] with the view with ID [R.id.content] (the `ConstraintLayout`
     * in our layout file) to have it apply padding to the [ViewGroup].
     *
     * Next we initialize our [Dissolve] variable `val dissolve` to an instance configured using the
     * `apply` extension function to use `image` as the target view, with a duration of 200 ms, and
     * our [TimeInterpolator] of [FAST_OUT_SLOW_IN] (defined in our file demo/Interpolators.kt).
     *
     * We then set an [Observer] on the [LiveData] wrapped resource ID property `image` of our
     * [DissolveViewModel] field [viewModel] whose lambda begins the transition `dissolve` on our
     * [ViewGroup] variable `card`, then sets the drawable with the resource ID of that `image`
     * property to be the content of this [ImageView] variable `image` (the animation to that
     * drawable is then handled by the transition API).
     *
     * Finally we set the `OnClickListener` of both `card` and `next` to lambdas which call the
     * `nextImage` method of [viewModel] which increments its [MutableLiveData] wrapped property
     * `position` which in turn causes its [LiveData] wrapped resource ID property `image` to
     * advance to point to the next drawable to be displayed.
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
        val dissolve: Dissolve = Dissolve().apply {
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
