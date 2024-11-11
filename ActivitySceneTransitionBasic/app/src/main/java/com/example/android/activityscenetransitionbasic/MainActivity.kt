/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.activityscenetransitionbasic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.squareup.picasso.Picasso

/**
 * Our main Activity in this sample. Displays a grid of items with an image and title. When the
 * user clicks on an item, [DetailActivity] is launched, using the Activity Scene Transitions
 * framework to animatedly do so.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.grid]. We initialize our [GridView]
     * variable `val grid` by finding the view with ID [R.id.grid], and set its `onItemClickListener`
     * to our [OnItemClickListener] field [mOnItemClickListener]. We initialize our [GridAdapter]
     * variable `val adapter` to a new instance, and set the `adapter` of `grid` to it.
     *
     * @param savedInstanceState We do not call [onSaveInstanceState] so do not use.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grid)

        // Setup the GridView and set the adapter
        val grid = findViewById<GridView>(R.id.grid)
        ViewCompat.setOnApplyWindowInsetsListener(grid) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        grid.onItemClickListener = mOnItemClickListener
        val adapter = GridAdapter()
        grid.adapter = adapter
    }

    /**
     * The [OnItemClickListener] we use for the items in our [GridView]. The SAM is `onItemClick`
     * and the the parameters passed to the lambda are:
     *
     *     `adapterView` The AdapterView where the click happened.
     *     `view` The view within the AdapterView that was clicked.
     *     `position` The position of the view in the adapter.
     *     `id` The row id of the item that was clicked.
     *
     * In the lambda we initialize our [Item] variable `val item` by retrieving the data associated
     * with the position `position` in the list. We initialize our [Intent] variable `val intent`
     * with an intent intended to execute the hard-coded class name [DetailActivity::class.java],
     * then add an extra to `intent` with the name [DetailActivity.EXTRA_PARAM_ID] ("detail:_id")
     * which holds the `id` property of `item`. We initialize our [ActivityOptionsCompat] variable
     * `val activityOptions` with an instance designed to transition between Activities using cross
     * Activity scene animations with the shared elements to transfer to the called Activity being
     * the view with ID [R.id.imageview_item] of the clicked [View] used as the [View] against which
     * to invoke the transition with the unique name [DetailActivity.VIEW_NAME_HEADER_IMAGE] and the
     * view with ID [R.id.textview_name] of the clicked [View] used as the [View] against which to
     * invoke the transition with the unique name [DetailActivity.VIEW_NAME_HEADER_TITLE]. We then
     * start the activity that `intent` is intended to start ([DetailActivity]) with `activityOptions`
     * converted to a [Bundle] and passed to the activity as its option [Bundle].
     */
    private val mOnItemClickListener =
        OnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, _: Long ->

            /**
             * Called when an item in the [android.widget.GridView] is clicked. Here will launch
             * the [DetailActivity], using the Scene Transition animation functionality.
             */
            /**
             * Called when an item in the [android.widget.GridView] is clicked. Here will launch
             * the [DetailActivity], using the Scene Transition animation functionality.
             */
            val item = adapterView.getItemAtPosition(position) as Item

            // Construct an Intent as normal
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.id)

            // BEGIN_INCLUDE(start_activity)
            /*
         * Now create an {@link android.app.ActivityOptions} instance using the
         * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])} factory
         * method.
         */
            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity,
                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                Pair(view.findViewById(R.id.imageview_item), DetailActivity.VIEW_NAME_HEADER_IMAGE),
                Pair(view.findViewById(R.id.textview_name), DetailActivity.VIEW_NAME_HEADER_TITLE)
            )

            // Now we can start the Activity, providing the activity options as a bundle
            (this@MainActivity as Context).startActivity(intent, activityOptions.toBundle())
            //ActivityCompat.startActivity(this@MainActivity, intent, activityOptions.toBundle())
            // END_INCLUDE(start_activity)
        }

    /**
     * [android.widget.BaseAdapter] which displays items.
     */
    private inner class GridAdapter : BaseAdapter() {
        /**
         * How many items are in the data set represented by this Adapter. We just return the size
         * of the `ITEMS` static array of [Item] objects in the [Item] class.
         *
         * @return Count of items.
         */
        override fun getCount(): Int {
            return Item.ITEMS.size
        }

        /**
         * Get the data item associated with the specified position in the data set. We just return
         * the [Item] stored in position [position] in the `ITEMS` static array of [Item] objects in
         * the [Item] class.
         *
         * @param position Position of the item whose data we want within the adapter's data set.
         * @return The data at the specified position.
         */
        override fun getItem(position: Int): Item {
            return Item.ITEMS[position]
        }

        /**
         * Get the row id associated with the specified position in the list. We just return the `id`
         * property of the  [Item] stored in position [position] in the `ITEMS` static array of [Item]
         * objects in the [Item] class (converted to [Long]).
         *
         * @param position The position of the item within the adapter's dataset whose row id we want.
         * @return The id of the item at the specified position.
         */
        override fun getItemId(position: Int): Long {
            return getItem(position).id.toLong()
        }

        /**
         * Get a View that displays the data at the specified position in the data set. We initialize
         * our nullable [View] variable `var viewLocal` to our nullable parameter [view], and if
         * `viewLocal` is `null` we set it to the [View] which the [LayoutInflater] instance that we
         * retrieve from our Context creates when it inflates the layout file [R.layout.grid_item]
         * using our [ViewGroup] parameter [viewGroup] for the layout params without attaching to
         * it. We initialize our [Item] variable `val item` by using our [getItem] method to retrieve
         * the [Item] at positon [position] in our dataset, and initialize our [ImageView] variable
         * `val image` by finding the view with ID [R.id.imageview_item] in `viewLocal`. We then use
         * global default `Picasso` instance to start an image request using the path given by the
         * `thumbnailUrl` property URL and asynchronously fulfills the request into the [ImageView]
         * `image`. We initialize our [TextView] variable `val name` by finding the [TextView] in
         * `viewLocal` with ID [R.id.textview_name] and set its text to the `name` property of `item`.
         * Finally we return `viewLocal` to the caller.
         *
         * @param position The position of the item within the adapter's dataset of the item whose
         * [View] we want.
         * @param view The old [View] to reuse, if possible.
         * @param viewGroup The parent that this view will eventually be attached to.
         * @return A [View] corresponding to the data at the specified position.
         */
        override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
            var viewLocal = view
            if (viewLocal == null) {
                viewLocal = layoutInflater.inflate(R.layout.grid_item, viewGroup, false)
            }
            val item = getItem(position)

            // Load the thumbnail image
            val image = viewLocal?.findViewById<ImageView>(R.id.imageview_item)
            Picasso.with(image?.context).load(item.thumbnailUrl).into(image)

            // Set the TextView's contents
            val name = viewLocal?.findViewById<TextView>(R.id.textview_name)
            name?.text = item.name
            return viewLocal!!
        }
    }
}