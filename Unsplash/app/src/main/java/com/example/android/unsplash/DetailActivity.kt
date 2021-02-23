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

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.SharedElementCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.pager.DetailViewPagerAdapter
import java.util.ArrayList

/**
 * Displays the [Photo] selected from the [RecyclerView] of [MainActivity] in a [ViewPager] along
 * with the rest of the [Photo] objects in our shared [ArrayList] dataset. The selected [Photo] uses
 * a shared transition to its page in our "detailed" full screen view, and when the user moves to a
 * different [Photo] in our [ViewPager] and presses (or gestures) "Back" to return to [MainActivity]
 * there will be a shared transition to its position in the [RecyclerView] of [MainActivity].
 */
class DetailActivity : AppCompatActivity() {
    /**
     * The [ViewPager] in our UI with ID [R.id.pager] which we use to display the dataset of [Photo]
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
     * Called when the activity is starting.
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
        slide.interpolator = AnimationUtils.loadInterpolator(this,
            android.R.interpolator.linear_out_slow_in)
        slide.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        transitions.addTransition(slide)
        transitions.addTransition(Fade())
        window.enterTransition = transitions
        val intent = intent
        sharedElementCallback = DetailSharedElementEnterCallback(intent)
        setEnterSharedElementCallback(sharedElementCallback)
        initialItem = intent.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0)
        setUpViewPager(intent.getParcelableArrayListExtra(IntentUtil.PHOTO))
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationOnClickListener(navigationOnClickListener)
        super.onCreate(savedInstanceState)
    }

    private fun setUpViewPager(photos: ArrayList<Photo>?) {
        viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPager.adapter = DetailViewPagerAdapter(this, photos!!, sharedElementCallback)
        viewPager.currentItem = initialItem
        viewPager.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                if (viewPager.childCount > 0) {
                    viewPager.removeOnLayoutChangeListener(this)
                    startPostponedEnterTransition()
                }
            }
        })
        viewPager.pageMargin = resources.getDimensionPixelSize(R.dimen.padding_mini)
        viewPager.setPageMarginDrawable(R.drawable.page_margin)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_INITIAL_ITEM, initialItem)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        initialItem = savedInstanceState.getInt(STATE_INITIAL_ITEM, 0)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        setActivityResult()
        super.onBackPressed()
    }

    override fun finishAfterTransition() {
        setActivityResult()
        super.finishAfterTransition()
    }

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
        private const val STATE_INITIAL_ITEM = "initial"
    }
}