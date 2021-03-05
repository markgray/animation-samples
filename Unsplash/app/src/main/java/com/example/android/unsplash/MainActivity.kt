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

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.transition.Transition
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.unsplash.data.UnsplashService
import com.example.android.unsplash.data.model.Photo
import com.example.android.unsplash.databinding.PhotoItemBinding
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback
import com.example.android.unsplash.ui.TransitionCallback
import com.example.android.unsplash.ui.grid.GridMarginDecoration
import com.example.android.unsplash.ui.grid.OnItemSelectedListener
import com.example.android.unsplash.ui.grid.PhotoAdapter
import com.example.android.unsplash.ui.grid.PhotoViewHolder
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.ArrayList

/**
 * The starting activity of this "Unsplash" demo. The demo consists of a [RecyclerView] with a
 * `GridLayoutManager` app:layoutManager that displays [Photo]'s downloaded from https://unsplash.it
 * in a grid, and when one of them is clicked [DetailActivity] is launched to show the [Photo] in
 * a larger format with a smooth shared transition occurring between the two images.
 */
class MainActivity : AppCompatActivity() {
    /**
     * The [Transition.TransitionListener] that we use for our activity's [Window] that receives
     * notifications from transitions. Notifications indicate transition lifecycle events. We use
     * it to reset shared element exit transition callbacks.
     */
    private val sharedExitListener: Transition.TransitionListener = object : TransitionCallback() {
        /**
         * Notification about the end of the transition. Canceled transitions will always notify
         * listeners of both the cancellation and end events. That is, `onTransitionEnd(Transition)`
         * is always called, regardless of whether the transition was canceled or played through to
         * completion. We call the method [setExitSharedElementCallback] with `null` to have it
         * remove the listener we added to be called to handle shared elements when we launched
         * [DetailActivity] once the transition has completed (or been canceled).
         *
         * @param transition The [Transition] which reached its end.
         */
        override fun onTransitionEnd(transition: Transition) {
            setExitSharedElementCallback(null as SharedElementCallback?)
        }
    }

    /**
     * The [RecyclerView] in our layout file with ID [R.id.image_grid] which displays our [Photo]'s
     */
    private lateinit var grid: RecyclerView

    /**
     * The indeterminate [ProgressBar] in our layout file with ID [android.R.id.empty] which is
     * displayed while we wait for [Photo]'s to be downloaded.
     */
    private lateinit var empty: ProgressBar

    /**
     * The [ArrayList] of [Photo] objects that we use as the dataset for the [PhotoAdapter] which
     * fills our [RecyclerView] field [grid]. It is also passed to [DetailActivity] when one of the
     * items in [grid] is clicked (along with the postion of the [Photo] clicked in the dataset).
     */
    private var relevantPhotos: ArrayList<Photo?>? = null

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.activity_main]. Our layout file
     * consists of a `FrameLayout` which holds an indeterminate [ProgressBar] and a [RecyclerView]
     * whose app:layoutManager is a [GridLayoutManager]. We then call the [postponeEnterTransition]
     * method to have it delay starting the entering and shared element transitions until all data
     * is loaded. We add our [Transition.TransitionListener] field [sharedExitListener] to the
     * exit [Transition] of our activity's [Window] (it will reset shared element exit transition
     * callbacks when that [Transition] completes). We initialize our [RecyclerView] field [grid] by
     * finding the [View] in our UI with ID [R.id.image_grid] and our [ProgressBar] field [empty] by
     * finding the [View] in our UI with ID [android.R.id.empty]. We call our method [setupRecyclerView]
     * to have it configure the [GridLayoutManager] of our [RecyclerView] field [grid] as we want it
     * (using span sizes of 3, 2, or 1 depending on the modulo 6 of the position in the grid to get
     * an "artsy" look). Then if our [Bundle] parameter [savedInstanceState] is not `null` we set
     * our [ArrayList] of [Photo] objects field [relevantPhotos] to the value stored under the key
     * [IntentUtil.RELEVANT_PHOTOS] in [savedInstanceState]. Finally we call our [displayData] method
     * to have it "populate" our grid with [Photo] objects (downloading them from the Internet first
     * if need be).
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState].
     * Our override of [onSaveInstanceState] saves our [ArrayList] of [Photo] objects dataset field
     * [relevantPhotos] in the [Bundle] passed it under the key [IntentUtil.RELEVANT_PHOTOS].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postponeEnterTransition()
        // Listener to reset shared element exit transition callbacks.
        window.sharedElementExitTransition.addListener(sharedExitListener)
        grid = findViewById<View>(R.id.image_grid) as RecyclerView
        empty = findViewById<View>(android.R.id.empty) as ProgressBar
        setupRecyclerView()
        if (savedInstanceState != null) {
            relevantPhotos = savedInstanceState.getParcelableArrayList(IntentUtil.RELEVANT_PHOTOS)
        }
        displayData()
    }

    /**
     * Displays our [ArrayList] of [Photo] objects dataset field [relevantPhotos] in our grid
     * (downloading them from the Internet first if need be). If our [ArrayList] dataset of [Photo]
     * objects in [relevantPhotos] is not `null` we just call our method [populateGrid] to have it
     * construct and configure a [PhotoAdapter] for our [RecyclerView] field [grid] to display the
     * contents of [relevantPhotos]. Otherwise we need to download [relevantPhotos] from the
     * Internet. To do this we initialize our [UnsplashService] variable `val unsplashApi` by using
     * the [RestAdapter.Builder] method to build a [UnsplashService] instance whose API endpoint URL
     * is [UnsplashService.ENDPOINT] ("https://unsplash.it"). We call the [UnsplashService.getFeed]
     * method of `unsplashApi` with an anonymous [retrofit.Callback] whose override of `success`
     * will set [relevantPhotos] to an [ArrayList] of [Photo] objects consisting of the last
     * [PHOTO_COUNT] entries in its [List] of [Photo] parameter `photos` and then call our method
     * [populateGrid] to have it construct and configure a [PhotoAdapter] for our [RecyclerView]
     * field [grid] to display the contents of [relevantPhotos]. The `failure` override of the
     * [Callback] will just log its [RetrofitError] parameter.
     */
    private fun displayData() {
        if (relevantPhotos != null) {
            populateGrid()
        } else {
            val unsplashApi: UnsplashService = RestAdapter.Builder()
                .setEndpoint(UnsplashService.ENDPOINT)
                .build()
                .create(UnsplashService::class.java)
            unsplashApi.getFeed(object : Callback<List<Photo?>> {
                /**
                 * Successful HTTP response. We set our [ArrayList] of [Photo] field [relevantPhotos]
                 * to the last [PHOTO_COUNT] (12) [Photo] objects in our parameter [photos], then
                 * call our [populateGrid] method to have it use [relevantPhotos] to populate its
                 * grid with the associated images.
                 *
                 * @param photos the [List] of [Photo] objects downloaded downloaded and parsed by
                 * [retrofit].
                 * @param response HTTP response returned from server.
                 */
                override fun success(photos: List<Photo?>, response: Response) {
                    // the first items not interesting to us, get the last <n>
                    relevantPhotos = ArrayList(photos.subList(photos.size - PHOTO_COUNT, photos.size))
                    populateGrid()
                }

                /**
                 * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
                 * exception. We just log the error.
                 *
                 * @param error the [RetrofitError] returned from [retrofit]
                 */
                override fun failure(error: RetrofitError) {
                    Log.e(TAG, "Error retrieving Unsplash feed:", error)
                }
            })
        }
    }

    /**
     * Called to construct and configure a [PhotoAdapter] for our [RecyclerView] field [grid] to
     * display the contents of the [ArrayList] of [Photo] objects field [relevantPhotos]. We set
     * the adapter of our [RecyclerView] field [grid] to a new instance of [PhotoAdapter] constructed
     * to use our [ArrayList] of [Photo] field [relevantPhotos]. Then we add to [grid] an anonymous
     * [OnItemSelectedListener] whose `onItemSelected` override returns having done nothing if its
     * [RecyclerView.ViewHolder] parameter `holder` is not a [PhotoViewHolder], but if it is a
     * [PhotoViewHolder] it sets its [PhotoItemBinding] variable 'val binding` to the [PhotoItemBinding]
     * property of `holder`, initializes its [Intent] variable `val intent` to the [Intent] created by
     * our [getDetailActivityStartIntent] method intended to launch [DetailActivity] with all the
     * information it needs to create its UI and sets its [ActivityOptions] variable `val activityOptions`
     * to the [ActivityOptions] instance that our [getActivityOptions] method constructs for `binding`.
     * After all this it launches `intent` for a result with the request code [IntentUtil.REQUEST_CODE]
     * and a "bundled" up `activityOptions` as additional options for how the Activity should be started.
     * Finally having added our [OnItemSelectedListener] to [grid] we set the visibility of our indeterminate
     * [ProgressBar] field [empty] to [View.GONE].
     */
    private fun populateGrid() {
        grid.adapter = PhotoAdapter(this, relevantPhotos!!)
        grid.addOnItemTouchListener(object : OnItemSelectedListener(this@MainActivity) {
            /**
             * Our custom [RecyclerView.OnItemTouchListener] calls this method from its
             * [RecyclerView.onInterceptTouchEvent] override when its `GestureDetector` detects
             * a screen touch to a particular item view of the [RecyclerView] with the `ViewHolder`
             * that contains the view that was touched and the adapter position that the child view
             * corresponds to. If our [RecyclerView.ViewHolder] parameter [holder] is not an instance
             * of [PhotoViewHolder] we return having done nothing. Otherwise we set our [PhotoItemBinding]
             * variable 'val binding` to the [PhotoItemBinding] property of [holder], initialize our
             * [Intent] variable `val intent` to the [Intent] created by our [getDetailActivityStartIntent]
             * method intended to launch [DetailActivity] with all the information it needs to create
             * its UI and set our [ActivityOptions] variable `val activityOptions` to the [ActivityOptions]
             * instance that our [getActivityOptions] method constructs for `binding`. Finally we launch
             * `intent` for a result with the request code [IntentUtil.REQUEST_CODE] and a "bundled"
             * up `activityOptions` as additional options for how the Activity should be started.
             *
             * @param holder the [RecyclerView.ViewHolder] that contains the view that was touched
             * @param position the adapter position that the touched view corresponds to
             */
            override fun onItemSelected(holder: RecyclerView.ViewHolder, position: Int) {
                if (holder !is PhotoViewHolder) {
                    return
                }
                val binding: PhotoItemBinding = holder.binding
                val intent: Intent = getDetailActivityStartIntent(
                    this@MainActivity,
                    relevantPhotos,
                    position,
                    binding
                )
                val activityOptions: ActivityOptions = getActivityOptions(binding)
                // TODO: use registerForActivityResult(ActivityResultContract, ActivityResultCallback
                // TODO: passing in a StartActivityForResult object for the ActivityResultContract.
                @Suppress("DEPRECATION")
                this@MainActivity.startActivityForResult(
                    intent,
                    IntentUtil.REQUEST_CODE,
                    activityOptions.toBundle()
                )
            }
        })
        empty.visibility = View.GONE
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in [onCreate] or [onRestoreInstanceState] (the [Bundle] populated by this
     * method will be passed to both). We store our [ArrayList] of [Photo] objects field [relevantPhotos]
     * as an [ArrayList] of [Parcelable] objects under the key [IntentUtil.RELEVANT_PHOTOS] in our
     * [Bundle] parameter [outState], then call our super's implementation of `onSaveInstanceState`.
     *
     * @param outState [Bundle] in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(IntentUtil.RELEVANT_PHOTOS, relevantPhotos)
        super.onSaveInstanceState(outState)
    }

    /**
     * Called when an activity you launched with an activity transition exposes this [Activity]
     * through a returning activity transition, giving you the [resultCode] and any additional
     * data from it. This method will only be called if the activity set a result code other
     * than [Activity.RESULT_CANCELED] and it supports activity transitions with
     * [Window.FEATURE_ACTIVITY_TRANSITIONS].
     *
     * The purpose of this function is to let the called [Activity] send a hint about its state so
     * that this underlying [Activity] can prepare to be exposed. A call to this method does not
     * guarantee that the called [Activity] has or will be exiting soon. It only indicates that it
     * will expose this [Activity]'s [Window] and it has some data to pass to prepare it.
     *
     * First we call the method [postponeEnterTransition] to postpone the entering activity transition.
     * Then we add an anonymous [ViewTreeObserver.OnPreDrawListener] to the [ViewTreeObserver] for
     * our [RecyclerView] field [grid] whose `onPreDraw` override will first remove itself as a
     * [ViewTreeObserver.OnPreDrawListener] then call the [startPostponedEnterTransition] method to
     * begin the transitions that were postponed by our call to [postponeEnterTransition], and finally
     * it will return `true` to proceed with the current drawing pass.
     *
     * If our [Intent] parameter [data] is `null` we return now, otherwise we continue by retrieving
     * the [Int] stored in our [Intent] parameter [data] as an extra under the key
     * [IntentUtil.SELECTED_ITEM_POSITION] in order to initialize our variable `val selectedItem`.
     * We then call the `scrollToPosition` of grid to have it scroll the position `selectedItem`.
     * We initialize our [PhotoViewHolder] variable `val holder` by finding the `ViewHolder` in
     * [grid] for the item in the position `selectedItem` of its data set and if this is `null` we
     * log this fact and return, otherwise we initialize our [DetailSharedElementEnterCallback]
     * variable `val callback` to a new instance constructed to the [Intent] that started this
     * activity to retrieve values it needs to configure the [TextView] displaying the author of the
     * selected item. We set the binding of `callback` to the [PhotoItemBinding] of `holder` and then
     * call the [setExitSharedElementCallback] method to have it use `callback` to handle shared
     * elements on the launching [Activity].
     *
     * @param resultCode The integer result code returned by the child activity
     * through its setResult().
     * @param data An [Intent], which can return result data to the caller
     * (various data can be attached to [Intent] "extras").
     */
    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        postponeEnterTransition()
        // Start the postponed transition when the recycler view is ready to be drawn.
        grid.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                grid.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })
        if (data == null) {
            return
        }
        val selectedItem = data.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0)
        grid.scrollToPosition(selectedItem)
        val holder = grid.findViewHolderForAdapterPosition(selectedItem) as PhotoViewHolder?
        if (holder == null) {
            Log.w(TAG, "onActivityReenter: Holder is null, remapping cancelled.")
            return
        }
        val callback = DetailSharedElementEnterCallback(intent)
        callback.setBinding(holder.binding)
        setExitSharedElementCallback(callback)
    }

    /**
     * Configures our [RecyclerView] field [grid] and its [GridLayoutManager] to display its dataset
     * in the way we want it. We initialize our [GridLayoutManager] variable `val gridLayoutManager`
     * by retrieving the [RecyclerView.LayoutManager] of our [RecyclerView] field [grid], and then
     * we set its [SpanSizeLookup] to an anonymous instance whose `getSpanSize` override will return
     * 3 different number of spans that each item occupies depending on the value of the `position`
     * of the item modulo 6: 3 spans for a value of 5, 2 spans for a value of 3, and 1 span for all
     * other values.
     *
     * Then we add a new instance of [GridMarginDecoration] constructed to produce a space of
     * [R.dimen.grid_item_spacing] (2dp) as an [RecyclerView.ItemDecoration] to [grid], and set
     * the fixed size property of [grid] to `true` (this allows [RecyclerView] to perform several
     * optimizations when it knows in advance that the [RecyclerView]'s size is not affected by the
     * adapter contents).
     */
    private fun setupRecyclerView() {
        val gridLayoutManager = grid.layoutManager as GridLayoutManager?
        gridLayoutManager!!.spanSizeLookup = object : SpanSizeLookup() {
            /**
             * Returns the number of span occupied by the item at [position]. We return 3 different
             * number of spans that each item occupies depending on the value of the `position` of
             * the item modulo 6: 3 spans for a value of 5, 2 spans for a value of 3, and 1 span for
             * all other values.
             *
             * @param position The adapter position of the item
             * @return The number of spans occupied by the item at the provided position
             */
            override fun getSpanSize(position: Int): Int {
                /* emulating https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6Okdz75tqQsck9lUkgxNVZza1U/style_imagery_integration_scale1.png */
                return when (position % 6) {
                    5 -> 3
                    3 -> 2
                    else -> 1
                }
            }
        }
        grid.addItemDecoration(GridMarginDecoration(resources.getDimensionPixelSize(R.dimen.grid_item_spacing)))
        grid.setHasFixedSize(true)
    }

    /**
     * Constructs an [ActivityOptions] instance that can be used to transition to [DetailActivity]
     * using cross [Activity] scene animations that use shared elements created from the views in the
     * [PhotoItemBinding] view binding of the selected item view and their related views in
     * [DetailActivity]. The views in the layout files layout/photo_item.xml and layout/detail_view.xml
     * contain an android:transitionName attribute with a view binding expression which computes a
     * transition name for the view by appending the [Photo.id] property of the [Photo] that the
     * layout files are displaying to the strings "transition_author" and "transition_photo". Using
     * this fact we initialize our [Pair] of [View] and [String] `val authorPair` to the [View] in
     * [binding] for the [TextView] displaying the `author` property of the [Photo] object paired
     * with the `transitionName` of that [TextView] (computed in its android:transitionName attribute
     * by appending the `id` propery of the [Photo] to the [String] "transition_author" in its binding
     * expression) and we initialize our [Pair] of [View] and [String] `val photoPair` to the [View]
     * in [binding] for the [ImageView] displaying the `photo` property of the [Photo] object paired
     * with the `transitionName` of that [ImageView] (computed in its android:transitionName attribute
     * by appending the `id` propery of the [Photo] to the [String] "transition_photo" in its binding
     * expression).
     *
     * Next we initialize our [View] variable `val decorView` to the top-level window decor view
     * (containing the standard window frame/decorations and the client's content inside of that),
     * initialize our [View] variable `val statusBackground` by finding the [View] in `decorView`
     * with id [android.R.id.statusBarBackground] (the status bar background [View]), and initialize
     * our [View] variable `val navBackground` by finding the [View] in `decorView` with id
     * [android.R.id.navigationBarBackground] (the navigation Bar Background [View]). We create a
     * [Pair] to initialize our variable `val statusPair` from the `statusBackground` view and its
     * `transitionName` property ("android:status:background"). Then we initialize our [ActivityOptions]
     * variable `val options` to an [ActivityOptions] to transition between Activities using cross
     * Activity scene animations composed of the shared elements `authorPair`,  `photoPair`, and
     * `statusPair` if `navBackground` is `null`, or if it is not `null` we initialize `options` to
     * an [ActivityOptions] to transition between Activities using cross Activity scene animations
     * composed of the shared elements `authorPair`,  `photoPair`, and `statusPair` as well as a
     * `val navPair` [Pair] created from `navBackground` and its `transitionName` property
     * ("android:navigation:background"). Finally we return `options` to our caller.
     *
     * @param binding the [PhotoItemBinding] view binding of the selected item view.
     * @return an [ActivityOptions] that will be used to transition to [DetailActivity] using cross
     * [Activity] scene animations between related views.
     */
    private fun getActivityOptions(binding: PhotoItemBinding): ActivityOptions {
        val authorPair: Pair<View, String> = Pair.create(binding.author, binding.author.transitionName)
        val photoPair: Pair<View, String> = Pair.create(binding.photo, binding.photo.transitionName)
        val decorView: View = window.decorView
        val statusBackground: View = decorView.findViewById(android.R.id.statusBarBackground)
        val navBackground: View? = decorView.findViewById(android.R.id.navigationBarBackground)
        // statusBackground.transitionName is "android:status:background"
        val statusPair: Pair<View, String> = Pair.create(
            statusBackground,
            statusBackground.transitionName
        )
        @Suppress("UnnecessaryVariable")
        val options: ActivityOptions = if (navBackground == null) {
            ActivityOptions.makeSceneTransitionAnimation(
                this,
                authorPair,
                photoPair,
                statusPair
            )
        } else {
            val navPair: Pair<View, String> = Pair.create(navBackground, navBackground.transitionName)
            // navBackground.transitionName is "android:navigation:background"
            ActivityOptions.makeSceneTransitionAnimation(
                this,
                authorPair,
                photoPair,
                statusPair,
                navPair
            )
        }
        return options
    }

    companion object {
        /**
         * The number of [Photo] objects we download and display.
         */
        private const val PHOTO_COUNT = 12

        /**
         * TAG used for logging.
         */
        private const val TAG = "MainActivity"

        /**
         * Called to construct an [Intent] that will launch [DetailActivity] with the extras it needs
         * to display the selected [Photo]. First we construct an [Intent] that will launch the
         * [DetailActivity] activity to initialize our variable `val intent`. We set the action of
         * `intent` to [Intent.ACTION_VIEW] ("android.intent.action.VIEW" -- Display the data to the
         * user), we store our [ArrayList] of [Photo] objects parameter [photos] as an array list
         * extra under the key [IntentUtil.PHOTO] in `intent`, store our [Int] parameter [position]
         * (the position of the selected item in our [RecyclerView]) as an extra under the key
         * [IntentUtil.SELECTED_ITEM_POSITION] in `intent`, store the `textSize` property of the
         * [TextView] `author` in our [PhotoItemBinding] view binding parameter [binding] under the
         * key [IntentUtil.FONT_SIZE] in `intent`, store a [Rect] containing the `paddingLeft`,
         * `paddingTop`, `paddingRight` and `paddingBottom` properties of the [TextView] `author`
         * in our [PhotoItemBinding] view binding parameter [binding] under the key [IntentUtil.PADDING]
         * in `intent`, and store the `currentTextColor` property of the [TextView] `author` in our
         * [PhotoItemBinding] view binding parameter [binding] under the key [IntentUtil.TEXT_COLOR]
         * in `intent`. Finally we return `intent` to the caller.
         *
         * @param host our [Activity], "this@MainActivity" in the [OnItemSelectedListener] added to
         * [grid] in our [populateGrid] method.
         * @param photos the [ArrayList] of [Photo] object we use as our dataset, [relevantPhotos]
         * in the [OnItemSelectedListener] added to [grid] in our [populateGrid] method.
         * @param position the position of the selected item in our [RecyclerView].
         * @param binding the [PhotoItemBinding] view binding of the selected item in our [RecyclerView]
         * which is retrieved from the `binding` property of the [RecyclerView.ViewHolder] associated
         * with the selected item.
         */
        private fun getDetailActivityStartIntent(
            host: Activity,
            photos: ArrayList<Photo?>?,
            position: Int,
            binding: PhotoItemBinding
        ): Intent {
            val intent = Intent(host, DetailActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putParcelableArrayListExtra(IntentUtil.PHOTO, photos)
            intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, position)
            intent.putExtra(IntentUtil.FONT_SIZE, binding.author.textSize)
            intent.putExtra(IntentUtil.PADDING,
                Rect(
                    binding.author.paddingLeft,
                    binding.author.paddingTop,
                    binding.author.paddingRight,
                    binding.author.paddingBottom
                )
            )
            intent.putExtra(IntentUtil.TEXT_COLOR, binding.author.currentTextColor)
            return intent
        }
    }
}