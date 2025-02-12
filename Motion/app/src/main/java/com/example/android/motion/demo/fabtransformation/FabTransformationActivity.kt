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

package com.example.android.motion.demo.fabtransformation

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.ui.EdgeToEdge
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Layout > FAB transformation
 *
 * FAB transformation is provided by Material Components. This activity demonstrates how to use
 * [FloatingActionButton.setExpanded] to transform the FAB into a sheet. The AndroidManifest.xml
 * "DESCRIPTION" is: A floating action button (FAB) can transform into a card by tweening FAB size
 * and corner radius.
 */
class FabTransformationActivity : AppCompatActivity() {

    /**
     * The [FabTransformationViewModel] which holds the [LiveData] wrapped dataset of 4 `cheeses`
     * which we display in our [CircularRevealCardView] when the FAB button is clicked.
     */
    private val viewModel: FabTransformationViewModel by viewModels()

    /**
     * The [FloatingActionButton] in our UI with ID `R.id.fab`. Its `OnClickListener` is set in our
     * `onCreate` override, and sets its expanded state to `true`.
     */
    private lateinit var fab: FloatingActionButton

    /**
     * The [TextView] in our UI with ID `R.id.message`. The [View.OnClickListener] field
     * [cheeseOnClick] which is used on each of the cheeses displayed in our "sheet" sets its
     * text to display the `name` of the cheese when the cheese item is clicked.
     */
    private lateinit var message: TextView

    /**
     * This is used much like a `ViewHolder` to hold references to the [ImageView] and [TextView] in
     * the [LinearLayout] parameter [parent] passed it, as well as to initialize the `OnClickListener`
     * of the [parent] to the [View.OnClickListener] parameter `listener` passed it.
     */
    private class CheeseItemHolder(val parent: LinearLayout, listener: View.OnClickListener) {

        /**
         * The [ImageView] in the layout file layout/cheese_list_item.xml which is used to display
         * the picture of the cheese that is associated with the cheese displayed in the holder.
         */
        val image: ImageView = parent.findViewById(R.id.image)

        /**
         * The [TextView] in the layout file layout/cheese_list_item.xml which is used to display
         * the name of the cheese that is associated with the cheese displayed in the holder.
         */
        val name: TextView = parent.findViewById(R.id.name)

        init {
            parent.setOnClickListener(listener)
        }
    }

    /**
     * This is the [View.OnClickListener] that is used as the `OnClickListener` for each of the 4
     * `Cheese` items in our "sheet". We initialize our [String] variable `val name` to the tag
     * associated with the [View] clicked that has the key `R.id.tag_name` (it is set to the `name`
     * property of the cheese being displayed the the clicked [View] in our [onCreate] override).
     * We then set the text of our [TextView] field [message] to a string informing the user that
     * "You selected" `name`. Finally we set the expanded state of [FloatingActionButton] field
     * [fab] to `false` causing our sheet to be replaced by the [FloatingActionButton].
     */
    private val cheeseOnClick = View.OnClickListener { v ->
        val name = v.getTag(R.id.tag_name) as String
        message.text = getString(R.string.you_selected, name)
        fab.isExpanded = false
    }

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file `R.layout.fab_transformation_activity`. It
     * consists of a `CoordinatorLayout` (ID "root") holding an `AppBarLayout` (ID "app_bar") with a
     * `MaterialToolbar` child (ID "toolbar"), a `NestedScrollView` (ID "scroll") holding a
     * `FrameLayout` (ID "content") with a `TextView` child (ID "message"). The `CoordinatorLayout`
     * also holds a "scrim" [View] (ID "scrim") filling its entire space which starts out invisible
     * and changes to visible when the [FloatingActionButton] is expanded in order to allow the user
     * to click on it to collapse the [FloatingActionButton]. Next the `CoordinatorLayout` holds the
     * [FloatingActionButton] (ID "fab") at the end|bottom, and a `CircularRevealCardView` (ID "sheet")
     * holding a `LinearLayout` child which holds 4 "layout/cheese_list_item" included layouts with
     * IDs "cheese_1", "cheese_2", "cheese_3", and "cheese_4" (the `CircularRevealCardView` starts
     * out invisible and changes to visible when the [FloatingActionButton] is expanded).
     *
     * Having set up our UI we now proceed to fetch references to all the ViewGroups in the UI in
     * order to configure them:
     *  - We initialize our [CoordinatorLayout] variable `val root` by finding the view with ID
     *  `R.id.root`
     *  - We initialize our [Toolbar] variable `val toolbar` by finding the view with ID `R.id.toolbar`
     *  - We initialize our [CircularRevealCardView] variable `val sheet` by finding the view with
     *  ID `R.id.sheet`
     *  - We initialize our [View] variable `val scrim` by finding the view with ID `R.id.scrim`
     *
     * We initialize our [List] of [CheeseItemHolder] variable `val cheeseHolders` to 4 instances
     * constructed to be associated with the views with IDs `R.id.cheese_1`, `R.id.cheese_2`,
     * `R.id.cheese_3` and `R.id.cheese_4` respectively and to use our [View.OnClickListener] field
     * [cheeseOnClick] as its `OnClickListener`.
     *
     * We initialize our [TextView] field [message] to the view with ID `R.id.message` and our
     * [FloatingActionButton] field [fab] to the view with ID `R.id.fab` then call the method
     * [setSupportActionBar] to have it set `toolbar` to act as the ActionBar for our Activity's
     * window.
     *
     * We next proceed to do what is necessary to set up for edge-to-edge display:
     *  - We call our [EdgeToEdge.setUpRoot] method to configure our Acitiviy root view `root` to
     *  use edge-to-edge display.
     *  - We call our [EdgeToEdge.setUpAppBar] method to configure the app bar with ID `R.id.app_bar`
     *  and our [Toolbar] in that app bar `toolbar` for edge-to-edge display.
     *  - We initialize our variable `val fabMargin` to the resource value with ID
     *  `R.dimen.spacing_medium` (16dp).
     *  - We set an `OnApplyWindowInsetsListener` on `root` to take over the policy for applying
     *  window insets to `root` whose lambda adds `fabMargin` to the current system window insets
     *  for the left, right and bottom margins of both [FloatingActionButton] field [fab] and
     *  [CircularRevealCardView] variable `sheet`.
     *
     * Next we set an [Observer] on the `cheeses` property of [FabTransformationViewModel] field
     * [viewModel] which populates the sheet content when `cheeses` changes value by using the
     * `forEachIndexed` extension function on our list of [CheeseItemHolder] variable `cheeseHolders`
     * to loop over `i` for each of the [CheeseItemHolder] in `cheeseHolders` setting the parent
     * [LinearLayout] to invisible if the size of `cheeses` is less than or equal to the current
     * index into `cheeses`, (there are not enough `Cheese` objects in `cheeses` for the holders,
     * so the remaining ones should be invisible) otherwise:
     *  - We set our `Cheese` variable `val cheese` to the `Cheese` at index `i`
     *  - We set the `parent` [LinearLayout] property of the current `holder` to visible.
     *  - We set the tag with the key `R.id.tag_name` of the `parent` of the current `holder` to the
     *  `name` property of `cheese`
     *  - We set the text of the `name` [TextView] of of the current `holder` to the `name` property
     *  of `cheese`.
     *  - We begin a load with [Glide] that will load the drawable with the resource ID that is in
     *  the `image` property of `cheese` into the [ImageView] property `image` of `holder` after
     *  applying the Transformation [CircleCrop] to the drawable.
     *
     * Lastly we set the `OnClickListener` of [FloatingActionButton] field [fab] to a lambda which
     * sets the `isExpanded` property of [fab] to `true` which causes the CoordinatorLayout to
     * transform the FAB into the view whose "app:layout_behavior" is the  string (resource ID
     * string/fab_transformation_sheet_behavior) FabTransformationSheetBehavior (the view in our
     * layout with ID `R.id.sheet`). Also the view marked with FabTransformationScrimBehavior as
     * its "app:layout_behavior" is faded in as a content scrim (our view with ID `R.id.scrim`
     * uses string/fab_transformation_scrim_behavior for this). And we also set the `OnClickListener`
     * of our [View] variable `scrim` to a lambda which sets the `isExpanded` property of [fab] to
     * `false` which will shrink the menu sheet back into the FAB.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState].
     * We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // See the layout file for how to set up the CoordinatorLayout behaviors.
        setContentView(R.layout.fab_transformation_activity)
        val root: CoordinatorLayout = findViewById(R.id.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val sheet: CircularRevealCardView = findViewById(R.id.sheet)
        val scrim: View = findViewById(R.id.scrim)
        val cheeseHolders: List<CheeseItemHolder> = listOf(
            CheeseItemHolder(findViewById(R.id.cheese_1), cheeseOnClick),
            CheeseItemHolder(findViewById(R.id.cheese_2), cheeseOnClick),
            CheeseItemHolder(findViewById(R.id.cheese_3), cheeseOnClick),
            CheeseItemHolder(findViewById(R.id.cheese_4), cheeseOnClick)
        )
        message = findViewById(R.id.message)
        fab = findViewById(R.id.fab)
        setSupportActionBar(toolbar)

        // Set up edge-to-edge display.
        EdgeToEdge.setUpRoot(root)
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        val fabMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            fab.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                leftMargin = fabMargin + insets.getInsets(systemBars()).left
                rightMargin = fabMargin + insets.getInsets(systemBars()).right
                bottomMargin = fabMargin + insets.getInsets(systemBars()).bottom
            }
            sheet.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                leftMargin = fabMargin + insets.getInsets(systemBars()).left
                rightMargin = fabMargin + insets.getInsets(systemBars()).right
                bottomMargin = fabMargin + insets.getInsets(systemBars()).bottom
            }
            insets
        }

        // Populate the sheet content.
        viewModel.cheeses.observe(this) { cheeses ->
            cheeseHolders.forEachIndexed { i, holder ->
                if (cheeses.size > i) {
                    val cheese = cheeses[i]
                    holder.parent.isVisible = true
                    holder.parent.setTag(R.id.tag_name, cheese.name)
                    holder.name.text = cheese.name
                    Glide.with(holder.image)
                        .load(cheese.image)
                        .transform(CircleCrop())
                        .into(holder.image)
                } else {
                    holder.parent.isVisible = false
                }
            }
        }

        // Bind events.
        fab.setOnClickListener {
            // Expand the FAB. The CoordinatorLayout transforms the FAB into the view marked with
            // FabTransformationSheetBehavior. Also the view marked with
            // FabTransformationScrimBehavior is faded in as a content scrim.
            fab.isExpanded = true
        }
        scrim.setOnClickListener {
            // Shrink the menu sheet back into the FAB.
            fab.isExpanded = false
        }
        addOurOnBackPressedCallback()
    }

    /**
     * This method adds an [OnBackPressedCallback] to the [OnBackPressedDispatcher] that replaces the
     * old `onBackPressed` override. The [OnBackPressedCallback] will be called when the activity has
     * detected the user's press of the back key. If our [FloatingActionButton] field [fab] is
     * expanded we set its `isExpanded` property to `false`, otherwise we just call the [finish]
     * method to close the Activity.
     */
    private fun addOurOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (fab.isExpanded) {
                    fab.isExpanded = false
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}
