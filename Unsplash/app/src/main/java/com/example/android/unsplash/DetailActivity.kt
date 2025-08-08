/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.unsplash

import android.animation.TimeInterpolator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.SharedElementCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.pager.DetailViewPagerAdapter
import java.util.ArrayList
import androidx.core.view.isNotEmpty

/**
 * Displays the [Photo] selected from the [RecyclerView] of [MainActivity] in a [ViewPager] along
 * with the rest of the [Photo] objects in our shared [ArrayList] dataset. The selected [Photo] uses
 * a shared transition to its page in our "detailed" full screen view, and when the user moves to a
 * different [Photo] in our [ViewPager] and presses (or gestures) "Back" to return to [MainActivity]
 * there will be a shared transition to its position in the [RecyclerView] of [MainActivity].
 */
class DetailActivity : AppCompatActivity() {
    /**
     * The [ViewPager] in our UI with ID `R.id.pager` which we use to display the dataset of [Photo]
     * objects which is passed to us in the [Intent] used to launch us as a Parcelable ArrayList
     * Extra with the key [IntentUtil.PHOTO]. The selected photo from [MainActivity] set as the
     * currently selected page in our [ViewPager].
     */
    private lateinit var viewPager: ViewPager

    /**
     * The position of the selected [Photo] in the [RecyclerView] of [MainActivity], passed to us in
     * the [Intent] that launched us under the key [IntentUtil.SELECTED_ITEM_POSITION].
     */
    private var initialItem: Int = 0

    /**
     * The [View.OnClickListener] that we use as the listener that responds to navigation events
     * whenever the user clicks the navigation button at the start of the toolbar. An icon must be
     * set for the navigation button to appear. It is also called when the user uses a gesture to
     * go back to [MainActivity] on devices using gesture navigation.
     */
    private val navigationOnClickListener = View.OnClickListener { finishAfterTransition() }

    /**
     * The custom [SharedElementCallback] that we use as our EnterSharedElementCallback. It will be
     * called to handle shared elements on the _launched_ Activity. Its constructor is called in our
     * [onCreate] override with the [Intent] used to launch us, and the extras stored in the [Intent]
     * are used to orchestrate our side of the shared transition.
     */
    private lateinit var sharedElementCallback: DetailSharedElementEnterCallback

    /**
     * Called when the activity is starting. First we set our content view to our layout file
     * `R.layout.activity_detail` (it consists of a `FrameLayout` which holds a [ViewPager] and
     * a [Toolbar]). Then we call the [postponeEnterTransition] method to postpone the entering
     * activity shared element transitions until all our data is loaded. We initialize our
     * [TransitionSet] variable `val transitions` to a new instance and our [Slide] variable
     * `val slide` to a new instance whose slide edge direction is [Gravity.BOTTOM]. We set the
     * [TimeInterpolator] of `slide` to the system's `linear_out_slow_in` interpolator, and its
     * duration to the value of the system resource [android.R.integer.config_shortAnimTime]
     * (200ms). We then add `slide` to `transitions` as well as a new instance of [Fade] (fades
     * targets in and out). We then set the [Transition] that will be used to move Views into the
     * the current [Window] for the activity to `transitions`
     *
     * Next we initialize our [Intent] variable `val intent` to the intent that started this activity,
     * then initialize our [DetailSharedElementEnterCallback] field [sharedElementCallback] to a new
     * instance constructed to harvest the info it needs from the various extras in `intent`, and we
     * then call the [setEnterSharedElementCallback] method to have it use [sharedElementCallback]
     * to handle shared elements on our launched Activity. We initialize our [Int] field [initialItem]
     * to the value stored under the [IntentUtil.SELECTED_ITEM_POSITION] key in `intent` (the position
     * of the [Photo] in the [RecyclerView] of [MainActivity] that was selected, and call our method
     * [setUpViewPager] to have it use the Parcelable ArrayList stored under the [IntentUtil.PHOTO]
     * key in `intent` (the dataset of all the [Photo] objects we shared with [MainActivity]) to set
     * up the [ViewPager] in our UI to display the [Photo] objects.
     *
     * Next we initialize our [Toolbar] variable `val toolbar` to the view in our UI with resource
     * ID `R.id.toolbar` and set its listener that responds to navigation events to our [View.OnClickListener]
     * field [navigationOnClickListener]. Then we call our super's implementation of `onCreate` and
     * finally we call our [addOurOnBackPressedCallback] method to add an [OnBackPressedCallback] to
     * the [OnBackPressedDispatcher] that replaes the deprecated onBackPressed` override.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState].
     * We restore our state in our [onRestoreInstanceState] override so do not use it here.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_detail)
        postponeEnterTransition()
        val transitions = TransitionSet()
        val slide = Slide(Gravity.BOTTOM)
        slide.interpolator = AnimationUtils.loadInterpolator(
            this,
            android.R.interpolator.linear_out_slow_in
        )
        slide.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        transitions.addTransition(slide)
        transitions.addTransition(Fade())
        window.enterTransition = transitions
        val intent = intent
        sharedElementCallback = DetailSharedElementEnterCallback(intent)
        setEnterSharedElementCallback(sharedElementCallback)
        initialItem = intent.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setUpViewPager(intent.getParcelableArrayListExtra(IntentUtil.PHOTO, Photo::class.java))
        } else {
            @Suppress("DEPRECATION") // Needed for Build.VERSION.SDK_INT < Build.VERSION_CODES.T
            setUpViewPager(intent.getParcelableArrayListExtra(IntentUtil.PHOTO))
        }
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationOnClickListener(navigationOnClickListener)
        super.onCreate(savedInstanceState)
        addOurOnBackPressedCallback()
    }

    /**
     * This method adds an [OnBackPressedCallback] to the [OnBackPressedDispatcher] that replaces the
     * old `onBackPressed` override. The [OnBackPressedCallback] will be called when the activity has
     * detected the user's press of the back key. We call the [setActivityResult] method then  call
     * the [finishAfterTransition] method which Reverses the Activity Scene entry Transition and
     * triggers the calling Activity to reverse its exit Transition. When the exit Transition
     * completes, [finish] is called.
     */
    private fun addOurOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setActivityResult()
                finishAfterTransition()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    /**
     * Sets up our [ViewPager] field [viewPager] to display our [ArrayList] of [Photo] objects
     * [photos]. First we initialize our [ViewPager] field [viewPager] to the [ViewPager] in our
     * UI with ID `R.id.pager`, we set its adapter to a new instance of [DetailViewPagerAdapter]
     * constructed to use [photos] as its dataset and our field [sharedElementCallback] as its
     * [DetailSharedElementEnterCallback] (it will be called to handle shared elements on our
     * launched Activity), and we set the currently selected page of [viewPager] to [initialItem].
     * We then add an anonymous [View.OnLayoutChangeListener] whose `onLayoutChange` override will
     * if the `childCount` of [viewPager] is greater than 0 -- remove itself from [viewPager] as
     * an [View.OnLayoutChangeListener] and then call the [startPostponedEnterTransition] method
     * to begin the postponed enter transitions now that we have a [View] to transition into.
     * We then set the margin between pages of [viewPager] to the resource `R.dimen.padding_mini`
     * (4dp) in pixels, and set the drawable that will be used to fill the margin between pages to
     * our drawable `R.drawable.page_margin` (a rectangle `shape` drawn using using our color
     * `R.color.page_margin` -- a light gray).
     *
     * @param photos the [ArrayList] of [Photo] objects we use as the dataset for our [ViewPager].
     */
    private fun setUpViewPager(photos: ArrayList<Photo>?) {
        viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPager.adapter = DetailViewPagerAdapter(this, photos ?: return, sharedElementCallback)
        viewPager.currentItem = initialItem
        viewPager.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                if (viewPager.isNotEmpty()) {
                    viewPager.removeOnLayoutChangeListener(this)
                    startPostponedEnterTransition()
                }
            }
        })
        viewPager.pageMargin = resources.getDimensionPixelSize(R.dimen.padding_mini)
        viewPager.setPageMarginDrawable(R.drawable.page_margin)
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in [onCreate] or [onRestoreInstanceState] (the [Bundle] populated by this
     * method will be passed to both). This method is called before an activity may be killed so
     * that when it comes back some time in the future it can restore its state. We just save our
     * [Int] field [initialItem] in [outState] under the key [STATE_INITIAL_ITEM] and then call our
     * super's implementation of `onSaveInstanceState`.
     *
     * @param outState [Bundle] in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_INITIAL_ITEM, initialItem)
        super.onSaveInstanceState(outState)
    }

    /**
     * This method is called after [onStart] when the activity is being re-initialized from a
     * previously saved state, given here in [savedInstanceState]. We just set our [Int] field
     * [initialItem] to the value stored under the key [STATE_INITIAL_ITEM] in [savedInstanceState]
     * and then call our super's implementation of `onRestoreInstanceState`.
     *
     * @param savedInstanceState the data most recently supplied in [onSaveInstanceState].
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        initialItem = savedInstanceState.getInt(STATE_INITIAL_ITEM, 0)
        super.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * Reverses the Activity Scene entry Transition and triggers the calling Activity to reverse its
     * exit Transition. When the exit Transition completes, [finish] is called. If no entry
     * Transition was used, [finish] is called immediately and the Activity exit Transition is run.
     * We call our method [setActivityResult] to have it set the result that our activity will return
     * to its caller, then we call our super's implementation of `finishAfterTransition`.
     */
    override fun finishAfterTransition() {
        setActivityResult()
        super.finishAfterTransition()
    }

    /**
     * Sets the result that our activity will return to its caller. If our [initialItem] field
     * (the position that the user selected in the [RecyclerView] of [MainActivity]) is equal to the
     * index of the currently displayed page of [viewPager] (its `currentItem` property) we just set
     * the result that our activity will return to its caller to `RESULT_OK` (operation succeeded)
     * and return. If the user has used the [ViewPager] to move to another page we initialize our
     * [Intent] variable `val intent` to a new instance, store the index of the currently displayed
     * page of [viewPager] as an extra under the key [IntentUtil.SELECTED_ITEM_POSITION] and set the
     * result that our activity will return to its caller to `RESULT_OK` and include `intent` as
     * data to propagate back to the originating activity.
     */
    private fun setActivityResult() {
        if (initialItem == viewPager.currentItem) {
            setResult(RESULT_OK)
            return
        }
        val intent = Intent()
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, viewPager.currentItem)
        setResult(RESULT_OK, intent)
    }

    companion object {
        /**
         * The key we use to store our field [initialItem] under in the [Bundle] passed it when our
         * [onSaveInstanceState] override is called, and we use to restore [initialItem] from the
         * [Bundle] passed our [onRestoreInstanceState] override.
         */
        private const val STATE_INITIAL_ITEM = "initial"
    }
}
