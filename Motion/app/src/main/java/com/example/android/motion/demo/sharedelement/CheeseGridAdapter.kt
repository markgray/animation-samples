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

package com.example.android.motion.demo.sharedelement

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.example.android.motion.R
import com.example.android.motion.demo.doOnEnd
import com.example.android.motion.model.Cheese
import com.google.android.material.card.MaterialCardView

private const val STATE_LAST_SELECTED_ID = "last_selected_id"

/**
 * A [RecyclerView.Adapter] for cheeses. This adapter starts the shared element transition. It also
 * handles return transition.
 *
 * @param onReadyToTransition a lambda to be run after [Glide] finishes loading the image for the
 * transition destination (when the `id` property of the [Cheese] being loaded is the same as our
 * [lastSelectedId] field we are being transitioned back from [CheeseDetailFragment] and there is a
 * postponed transition for that particular [Cheese]). In our case the lambda just calls the method
 * `Fragment.startPostponedEnterTransition` to have it begin the postponed transition.
 */
internal class CheeseGridAdapter(
    private val onReadyToTransition: () -> Unit
) : ListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    /**
     * The [Cheese.id] property of the last [Cheese] that the user has clicked. This is set by the
     * `OnClickListener` of the `itemView` of the [CheeseViewHolder] to the `id` property of the
     * [Cheese] it holds, and is saved by our [saveInstanceState] method under the key
     * [STATE_LAST_SELECTED_ID] in the [Bundle] passed it by the `onSaveInstanceState` override of
     * [CheeseGridFragment] before we navigate to [CheeseDetailFragment] then restored by our
     * [restoreInstanceState] method from the [Bundle] passed it from the `onViewCreated` override
     * of [CheeseGridFragment] when we navigate back from [CheeseDetailFragment]. Saving the `id`
     * allows us to use the [Cheese] as the focal element for the shared element transition to and
     * from [CheeseDetailFragment]. See our [onBindViewHolder] override to see how we do this.
     */
    private var lastSelectedId: Long? = null

    /**
     * `true` if we are expecting a reenter transition from the detail fragment.
     */
    val expectsTransition: Boolean
        get() = lastSelectedId != null

    /**
     * Called when [RecyclerView] needs a new [CheeseViewHolder] of the given type to represent
     * an item. We return a new instance of [CheeseViewHolder] after adding an `OnClickListener`
     * to its [CheeseViewHolder.itemView] which:
     *  - Initializes its [Cheese] variable `val cheese` to the [Cheese] in our dataset which is at
     *  the Adapter position that this ViewHolder is intended for.
     *  - Sets our [lastSelectedId] field to the [Cheese.id] property of `cheese` to record the
     *  selected item so that we can make the item ready before starting the reenter transition.
     *  - Finds a `NavController` associated with the [View] clicked and use it to navigate to the
     *  destination ID [R.id.cheeseDetailFragment] (the [CheeseDetailFragment]) passing it the `id`
     *  property of `cheese` as its [CheeseDetailFragmentArgs] safe args, `null` for its `navOptions`
     *  (special options for this navigation operation), and for its `navigatorExtras` an instance
     *  of [FragmentNavigatorExtras] which maps the views in our [CheeseViewHolder.itemView] to
     *  their transition names in [CheeseDetailFragment].
     *
     * The [FragmentNavigatorExtras] mentioned above maps the views in the [CheeseViewHolder] to the
     * string constants defined in [CheeseDetailFragment] as follows:
     *  - [CheeseViewHolder.card] the [MaterialCardView] with ID [R.id.card] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_BACKGROUND] (the `card`
     *  is expanded into the background, the fragment will use this first element as the epicenter
     *  of all the fragment transitions, including Explode for non-shared elements.
     *  - [CheeseViewHolder.image] the [ImageView] with ID [R.id.image] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_IMAGE] (the `image` is
     *  the focal element in this shared element transition).
     *  - [CheeseViewHolder.name] the [TextView] with ID [R.id.name] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_NAME] (this element is
     *  only on the grid item, but needs to be a shared element so it can be animated with the card)
     *  - [CheeseViewHolder.favorite] the [ImageView] with ID [R.id.favorite] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_FAVORITE] (this element is
     *  only on the grid item, but needs to be a shared element so it can be animated with the card)
     *  - [CheeseViewHolder.bookmark] the [ImageView] with ID [R.id.bookmark] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_BOOKMARK] (this element is
     *  only on the grid item, but needs to be a shared element so it can be animated with the card)
     *  - [CheeseViewHolder.share] the [ImageView] with ID [R.id.share] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_SHARE] (this element is
     *  only on the grid item, but needs to be a shared element so it can be animated with the card)
     *  - [CheeseViewHolder.toolbar] the [MirrorView] with ID [R.id.toolbar] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_TOOLBAR] (this element
     *  is only on the detail fragment)
     *  - [CheeseViewHolder.body] the [MirrorView] with ID [R.id.body] in the `itemView` of
     *  [CheeseViewHolder] is mapped to [CheeseDetailFragment.TRANSITION_NAME_BODY] (this element
     *  is only on the detail fragment)
     *
     * @param parent The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     * @return A new [CheeseViewHolder] that holds a [View] of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent).apply {
            itemView.setOnClickListener { view ->
                val cheese: Cheese = getItem(absoluteAdapterPosition)

                // Record the selected item so that we can make the item ready before starting the
                // reenter transition.
                lastSelectedId = cheese.id

                view.findNavController().navigate(
                    R.id.cheeseDetailFragment,
                    CheeseDetailFragmentArgs(cheese.id).toBundle(),
                    null,
                    FragmentNavigatorExtras(
                        // We expand the card into the background.
                        // The fragment will use this first element as the epicenter of all the
                        // fragment transitions, including Explode for non-shared elements.
                        card to CheeseDetailFragment.TRANSITION_NAME_BACKGROUND,
                        // The image is the focal element in this shared element transition.
                        image to CheeseDetailFragment.TRANSITION_NAME_IMAGE,

                        // These elements are only on the grid item, but they need to be shared
                        // elements so they can be animated with the card. See SharedFade.kt.
                        name to CheeseDetailFragment.TRANSITION_NAME_NAME,
                        favorite to CheeseDetailFragment.TRANSITION_NAME_FAVORITE,
                        bookmark to CheeseDetailFragment.TRANSITION_NAME_BOOKMARK,
                        share to CheeseDetailFragment.TRANSITION_NAME_SHARE,

                        // These elements are only on the detail fragment.
                        toolbar to CheeseDetailFragment.TRANSITION_NAME_TOOLBAR,
                        body to CheeseDetailFragment.TRANSITION_NAME_BODY
                    )
                )
            }
        }
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the [CheeseViewHolder.itemView] in its [CheeseViewHolder] parameter
     * [holder] to reflect the item at the given position. First we initialize our [Cheese] variable
     * `val cheese` to the [Cheese] in our dataset occupying the position [position]. Next we set
     * the TransitionName of each of the Views in [CheeseViewHolder] parameter [holder] to a string
     * created by concatenating the string value of the [Cheese.id] property of `cheese` to the name
     * of the field holding the view separated by a "-" character (this guarantees that each of the
     * shared elements has a unique transition name, not just in this grid item, but in the entire
     * fragment).
     *
     * We next set the text of the [TextView] field [CheeseViewHolder.name] of [holder] to the
     * [Cheese.name] field of `cheese`. To load the [ImageView] field [CheeseViewHolder.image] of
     * [holder] we first initialize our [RequestBuilder] variable `var requestBuilder` to an instance
     * which will begin a load with [Glide] will be tied to the lifecycle of the fragment that
     * contains the `image` field of [holder] and will load the [Drawable] whose resource ID is in
     * the [Cheese.image] field of `cheese` without transforming the image (by cropping). If the
     * [Cheese.id] property of `cheese` is [lastSelectedId] we set the priority of `requestBuilder`
     * to [Priority.IMMEDIATE] (the highest priority) and add a lambda to be executed when the
     * request is complete which executes our [onReadyToTransition] method reference (the lambda
     * which is passed as an argument to our constructor by [CheeseGridFragment] calls the method
     * `startPostponedEnterTransition` to begin postponed transitions) and then sets [lastSelectedId]
     * to `null`. Having configured `requestBuilder` to do what we want we then calls its `into`
     * method to set the `image` field of [holder] to be the [ImageView] that the resource will be
     * loaded into, cancelling any existing loads into the view, and freeing any resources [Glide]
     * may have previously loaded into the view so they may be reused.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese: Cheese = getItem(position)

        // Each of the shared elements has to have a unique transition name, not just in this grid
        // item, but in the entire fragment.
        ViewCompat.setTransitionName(holder.image, "image-${cheese.id}")
        ViewCompat.setTransitionName(holder.name, "name-${cheese.id}")
        ViewCompat.setTransitionName(holder.toolbar, "toolbar-${cheese.id}")
        ViewCompat.setTransitionName(holder.card, "card-${cheese.id}")
        ViewCompat.setTransitionName(holder.favorite, "favorite-${cheese.id}")
        ViewCompat.setTransitionName(holder.bookmark, "bookmark-${cheese.id}")
        ViewCompat.setTransitionName(holder.share, "share-${cheese.id}")
        ViewCompat.setTransitionName(holder.body, "body-${cheese.id}")

        holder.name.text = cheese.name

        // Load the image asynchronously. See CheeseDetailFragment.kt about "dontTransform()"
        var requestBuilder = Glide.with(holder.image).load(cheese.image).dontTransform()
        if (cheese.id == lastSelectedId) {
            requestBuilder = requestBuilder
                .priority(Priority.IMMEDIATE)
                .doOnEnd {
                    // We have loaded the image for the transition destination. It is ready to start
                    // the transition postponed in the fragment.
                    onReadyToTransition()
                    lastSelectedId = null
                }
        }
        requestBuilder.into(holder.image)
    }

    /**
     * Called from the `onSaveInstanceState` override of [CheeseGridFragment] to have us save our
     * [lastSelectedId] field in the [Bundle] that `onSaveInstanceState` is passed when the fragment
     * is killed. That [Bundle] will be passed to our [restoreInstanceState] by the `onViewCreated`
     * override of [CheeseGridFragment] when the fragment is recreated. If our [lastSelectedId] field
     * is not `null` we save [lastSelectedId] in our [Bundle] parameter [outState] under the key
     * [STATE_LAST_SELECTED_ID].
     *
     * @param outState the [Bundle] we should store any information that we will need to restore when
     * we are resumed.
     */
    fun saveInstanceState(outState: Bundle) {
        lastSelectedId?.let { id ->
            outState.putLong(STATE_LAST_SELECTED_ID, id)
        }
    }

    /**
     * Called from the `onViewCreated` override of [CheeseGridFragment] when the fragment is resumed
     * so that we may restore the contents of our [lastSelectedId] field to the value that our method
     * [saveInstanceState] saved in the [Bundle] passed to the `onSaveInstanceState` override of
     * [CheeseGridFragment] when the fragment was killed. If our [lastSelectedId] field is `null`
     * and [state] contains the key [STATE_LAST_SELECTED_ID] we set our [lastSelectedId] field to
     * the [Long] stored in [state] under the key [STATE_LAST_SELECTED_ID].
     *
     * @param state the [Bundle] passed to the `onViewCreated` override of [CheeseGridFragment].
     */
    fun restoreInstanceState(state: Bundle) {
        if (lastSelectedId == null && state.containsKey(STATE_LAST_SELECTED_ID)) {
            lastSelectedId = state.getLong(STATE_LAST_SELECTED_ID)
        }
    }
}

/**
 * The [RecyclerView.ViewHolder] which holds all the information needed to display a [Cheese] object
 * in the [RecyclerView] used in the [CheeseGridFragment] UI. Our constructor just calls our super's
 * constructor with the [View] that the [LayoutInflater] for the context of our [ViewGroup] parameter
 * `parent` inflates from our layout file [R.layout.cheese_grid_item] when it uses `parent` for its
 * layout params without attaching to it.
 *
 * @param parent the [ViewGroup] that our [itemView] will be attached to.
 */
internal class CheeseViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.cheese_grid_item, parent, false)
) {
    /**
     * The root [MaterialCardView] of our [itemView] with ID [R.id.card] holds all the other views.
     * Used as the epicenter of all the fragment transitions, including Explode for non-shared elements.
     * Its shared element name is [CheeseDetailFragment.TRANSITION_NAME_BACKGROUND] and its unique
     * transition name is formed by appending the string value of the [Cheese.id] property of the
     * [Cheese] we hold to the string: "card-"
     */
    val card: MaterialCardView = itemView.findViewById(R.id.card)

    /**
     * The [ImageView] in our [itemView] with ID [R.id.image] which holds the [Drawable] picture of
     * the [Cheese] whose resource ID is found in its [Cheese.image] property. Used as the focal
     * element in the shared element transition to [CheeseDetailFragment]. Its shared element name
     * is [CheeseDetailFragment.TRANSITION_NAME_IMAGE] and its unique transition name is formed by
     * appending the string value of the [Cheese.id] property of the [Cheese] we hold to the string:
     * "image-"
     */
    val image: ImageView = itemView.findViewById(R.id.image)

    /**
     * The [TextView] in our [itemView] with ID [R.id.name] which displays the [Cheese.name] field
     * of the [Cheese] we hold. This element is only on the grid item, but it needs to be a shared
     * element so it can be animated with the card. Its shared element name is
     * [CheeseDetailFragment.TRANSITION_NAME_NAME] and its unique transition name is formed by
     * appending the string value of the [Cheese.id] property of the [Cheese] we hold to the string:
     * "name-"
     */
    val name: TextView = itemView.findViewById(R.id.name)

    /**
     * The [MirrorView] in our [itemView] with ID [R.id.toolbar] which exists only in the detail
     * fragment [CheeseDetailFragment]. Its shared element name is
     * [CheeseDetailFragment.TRANSITION_NAME_TOOLBAR] and its unique transition name is formed by
     * appending the string value of the [Cheese.id] property of the [Cheese] we hold to the string:
     * "toolbar-"
     */
    val toolbar: MirrorView = itemView.findViewById(R.id.toolbar)

    /**
     * The [ImageView] in our [itemView] with ID [R.id.favorite] which holds the [Drawable] with
     * resource ID [R.drawable.ic_favorite] (a heart icon). This element is only on the grid item,
     * but it needs to be a shared element so it can be animated with the card. Its shared element
     * name is [CheeseDetailFragment.TRANSITION_NAME_FAVORITE] and its unique transition name is
     * formed by appending the string value of the [Cheese.id] property of the [Cheese] we hold to
     * the string: "favorite-"
     */
    val favorite: ImageView = itemView.findViewById(R.id.favorite)

    /**
     * The [ImageView] in our [itemView] with ID [R.id.bookmark] which holds the [Drawable] with
     * resource ID [R.drawable.ic_bookmark] (a book icon). This element is only on the grid item,
     * but it needs to be a shared element so it can be animated with the card. Its shared element
     * name is [CheeseDetailFragment.TRANSITION_NAME_SHARE] and its unique transition name is
     * formed by appending the string value of the [Cheese.id] property of the [Cheese] we hold to
     * the string: "bookmark-"
     */
    val bookmark: ImageView = itemView.findViewById(R.id.bookmark)

    /**
     * The [ImageView] in our [itemView] with ID [R.id.share] which holds the [Drawable] with
     * resource ID [R.drawable.ic_share] (a share icon). This element is only on the grid item,
     * but it needs to be a shared element so it can be animated with the card. Its shared element
     * name is [CheeseDetailFragment.TRANSITION_NAME_BOOKMARK] and its unique transition name is
     * formed by appending the string value of the [Cheese.id] property of the [Cheese] we hold to
     * the string: "share-"
     */
    val share: ImageView = itemView.findViewById(R.id.share)

    /**
     * The [MirrorView] in our [itemView] with ID [R.id.body] which exists only in the detail
     * fragment [CheeseDetailFragment]. Its shared element name is
     * [CheeseDetailFragment.TRANSITION_NAME_BODY] and its unique transition name is formed by
     * appending the string value of the [Cheese.id] property of the [Cheese] we hold to the string:
     * "body-"
     */
    val body: MirrorView = itemView.findViewById(R.id.body)
}
