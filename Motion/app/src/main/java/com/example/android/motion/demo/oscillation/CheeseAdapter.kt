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

package com.example.android.motion.demo.oscillation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

/**
 * The [ListAdapter] used to supply views to display in the [RecyclerView] of [OscillationActivity].
 */
internal class CheeseAdapter : ListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    /**
     * A [RecyclerView.OnScrollListener] to be set to the RecyclerView. This tilts the visible
     * items while the list is scrolled.
     *
     * @see RecyclerView.addOnScrollListener
     */
    val onScrollListener = object : RecyclerView.OnScrollListener() {
        /**
         * Callback method to be invoked when the [RecyclerView] has been scrolled. This will be
         * called after the scroll has completed. This callback will also be called if visible item
         * range changes after a layout calculation. In that case, [dx] and [dy] will be 0. Using
         * our `forEachVisibleHolder` extension function we loop through all of the [CheeseViewHolder]
         * which are visible in our [RecyclerView] parameter [recyclerView] and after setting the
         * start velocity of their `rotation` [SpringAnimation] field to a value calculated by the
         * horizontal scroll offset we then start the animation which rotates the view with a bouncy
         * spring configuration, resulting in an oscillation effect.
         *
         * @param recyclerView The [RecyclerView] which scrolled.
         * @param dx The amount of horizontal scroll.
         * @param dy The amount of vertical scroll.
         */
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            recyclerView.forEachVisibleHolder { holder: CheeseViewHolder ->
                holder.rotation
                    // Update the velocity.
                    // The velocity is calculated by the horizontal scroll offset.
                    .setStartVelocity(holder.currentVelocity - dx * SCROLL_ROTATION_MAGNITUDE)
                    // Start the animation. This does nothing if the animation is already running.
                    .start()
            }
        }
    }

    /**
     * A [RecyclerView.EdgeEffectFactory] to be set to the RecyclerView. This adds bounce effect
     * when the list is over-scrolled.
     *
     * @see RecyclerView.setEdgeEffectFactory
     */
    val edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
        /**
         * Creates a new [EdgeEffect] to be used on [RecyclerView] parameter [recyclerView] for the
         * direction [direction].
         *
         * @param recyclerView the [RecyclerView] which our [EdgeEffect] will be used for.
         * @param direction the direction that the [EdgeEffect] is for, one of `DIRECTION_LEFT`,
         * `DIRECTION_TOP`, `DIRECTION_RIGHT`, or `DIRECTION_BOTTOM`.
         */
        override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
            return object : EdgeEffect(recyclerView.context) {

                /**
                 * A view should call this when content is pulled away from an edge by the user.
                 * This will update the state of the current visual effect and its associated
                 * animation. The host view should always call `invalidate` after this and draw
                 * the results accordingly. First we call our super's implementation of `onPull`,
                 * then we call our [handlePull] method with our [Float] parameter [deltaDistance].
                 *
                 * @param deltaDistance Change in distance since the last call. Values may be 0
                 * (no change) to 1.f (full length of the view) or negative values to express change
                 * back toward the edge reached to initiate the effect.
                 */
                override fun onPull(deltaDistance: Float) {
                    super.onPull(deltaDistance)
                    handlePull(deltaDistance)
                }

                /**
                 * A view should call this when content is pulled away from an edge by the user.
                 * This will update the state of the current visual effect and its associated
                 * animation. The host view should always call `invalidate` after this and draw
                 * the results accordingly. First we call our super's implementation of `onPull`,
                 * then we call our [handlePull] method with our [Float] parameter [deltaDistance].
                 *
                 * @param deltaDistance Change in distance since the last call. Values may be 0
                 * (no change) to 1.f (full length of the view) or negative values to express change
                 * back toward the edge reached to initiate the effect.
                 * @param displacement The displacement from the starting side of the effect of the
                 * point initiating the pull. In the case of touch this is the finger position.
                 * Values may be from 0-1.
                 */
                override fun onPull(deltaDistance: Float, displacement: Float) {
                    super.onPull(deltaDistance, displacement)
                    handlePull(deltaDistance)
                }

                /**
                 * This is called by both of our overrides of [onPull] which are called when content
                 * is pulled away from an edge by the user. This occurs while the list is scrolled
                 * with a finger so we stop the animation of all of the item views in [recyclerView]
                 * and update these view's properties without animation.
                 *
                 * First we initialize our variable `val sign` to -1 if [direction] is `DIRECTION_RIGHT`,
                 * initialize our variable `val rotationDelta` to `sign` times [deltaDistance] times
                 * [OVERSCROLL_ROTATION_MAGNITUDE], and intialize our variable `val translationXDelta`
                 * to `sign` times the `width` of [recyclerView] times [deltaDistance] times
                 * [OVERSCROLL_TRANSLATION_MAGNITUDE].
                 *
                 * Finally we loop through all of the visible [CheeseViewHolder] in [recyclerView]
                 * cancelling its [SpringAnimation] field `rotation`, cancelling its [SpringAnimation]
                 * field `translationX`, adding `rotationDelta` to the `rotation` property of the
                 * `itemView`, and adding `translationXDelta` to the `translationX` property of the
                 * `itemView`.
                 *
                 * @param deltaDistance Change in distance since the last call. Values may be 0
                 * (no change) to 1.f (full length of the view) or negative values to express change
                 * back toward the edge reached to initiate the effect.
                 */
                private fun handlePull(deltaDistance: Float) {
                    // This is called on every touch event while the list is scrolled with a finger.
                    // We simply update the view properties without animation.
                    val sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    val rotationDelta = sign * deltaDistance * OVERSCROLL_ROTATION_MAGNITUDE
                    val translationXDelta =
                        sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.forEachVisibleHolder { holder: CheeseViewHolder ->
                        holder.rotation.cancel()
                        holder.translationX.cancel()
                        holder.itemView.rotation += rotationDelta
                        holder.itemView.translationX += translationXDelta
                    }
                }

                /**
                 * Called when the object is released after being pulled. This will begin the
                 * "decay" phase of the effect. After calling this method the host view should
                 * call `invalidate` and draw the results accordingly. This is when we should
                 * start the animations to bring the view property values back to their resting
                 * states. We do this by looping through all of the visible [CheeseViewHolder]
                 * in [recyclerView] starting its [SpringAnimation] field `rotation`, and starting
                 * its [SpringAnimation] field `translationX`.
                 */
                override fun onRelease() {
                    super.onRelease()
                    // The finger is lifted. This is when we should start the animations to bring
                    // the view property values back to their resting states.
                    recyclerView.forEachVisibleHolder { holder: CheeseViewHolder ->
                        holder.rotation.start()
                        holder.translationX.start()
                    }
                }

                /**
                 * Called when the effect absorbs an impact at the given velocity. Used when a fling
                 * reaches the scroll boundary. First we call our super's implementation of `onAbsorb`.
                 * Next we initialize our variable `val sign` to -1 if [direction] is `DIRECTION_RIGHT`,
                 * and initialize our variable `val translationVelocity` to `sign` times `velocity`
                 * times [FLING_TRANSLATION_MAGNITUDE]. Then we loop through all of the visible
                 * [CheeseViewHolder] in `recyclerView` setting the start velocity of its
                 * [SpringAnimation] field `translationX` to `translationVelocity` and starting that
                 * animation.
                 *
                 * @param velocity Velocity at impact in pixels per second.
                 */
                override fun onAbsorb(velocity: Int) {
                    super.onAbsorb(velocity)
                    val sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    // The list has reached the edge on fling.
                    val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                    recyclerView.forEachVisibleHolder { holder: CheeseViewHolder ->
                        holder.translationX
                            .setStartVelocity(translationVelocity)
                            .start()
                    }
                }
            }
        }
    }

    /**
     * Called when [RecyclerView] needs a new [CheeseViewHolder] to represent an item. This new
     * [CheeseViewHolder] should be constructed with a new `View` that can represent a [Cheese]
     * object. We return a new instance of [CheeseViewHolder] which we configure so that the
     * rotation pivot point of its `itemView` is at the center of the top edge. We use the
     * [View.doOnLayout] extension function of `itemView` to have a lambda performed when this view
     * is laid out which sets the `pivotX` property of the view to half of the width, and then
     * set the `pivotY` property of `itemView` to 0f (delaying until layout is not needed for this
     * of course).
     *
     * @param parent The [ViewGroup] into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent).apply {
            // The rotation pivot should be at the center of the top edge.
            itemView.doOnLayout { v -> v.pivotX = v.width / 2f }
            itemView.pivotY = 0f
        }
    }

    /**
     * Called by [RecyclerView] to display the data at the specified position. This method should
     * update the contents of the [CheeseViewHolder] to reflect the item at the given position.
     * First we initialize our [Cheese] variable `val cheese` to the [Cheese] at position `position`
     * in our dataset [List]. Then we being a load with [Glide] that will load the drawable whose
     * resource ID is specified by the `image` field of `cheese` into the [ImageView] field `image`
     * of [holder], and set the text of the [TextView] field `name` of [holder] to the `name` field
     * of `cheese`.
     *
     * @param holder The [CheeseViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese: Cheese = getItem(position)
        Glide.with(holder.image).load(cheese.image).into(holder.image)
        holder.name.text = cheese.name
    }
}

/**
 * The [RecyclerView.ViewHolder] which holds all the information needed to display and animate a
 * [Cheese] object in its [ViewGroup] parameter [parent] (which is a [RecyclerView] using
 * `StaggeredGridLayoutManager` as its layout manager in our case). Our constructor uses the
 * [LayoutInflater] from the context of our [ViewGroup] parameter [parent] to inflate our layout
 * file [R.layout.cheese_board_item] using [parent] for its layout params without attaching to it
 * and passes that [View] to our super's constructor for it to use as our item view.
 *
 * @param parent the [ViewGroup] that our item view will be attached to.
 */
internal class CheeseViewHolder(
    val parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.cheese_board_item, parent, false)
) {
    /**
     * The [ImageView] which is used to display the `image` field of our [Cheese].
     */
    val image: ImageView = itemView.findViewById(R.id.image)

    /**
     * The [TextView] which is used to display the `name` field of our [Cheese].
     */
    val name: TextView = itemView.findViewById(R.id.name)

    /**
     * The current velocity of our [SpringAnimation] field [rotation] which is updated on every
     * animation frame by the `OnAnimationUpdateListener` added to it.
     */
    var currentVelocity = 0f

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation rotates the view with a bouncy
     * spring configuration, resulting in the oscillation effect. The animation is started in
     * [CheeseAdapter.onScrollListener]. We initialize it to a new instance which will animate the
     * [SpringAnimation.ROTATION] property of our [itemView] and which is configured to use a
     * [SpringForce] as the force that drives the animation whose final position is 0f, whose Spring
     * damping ratio is [SpringForce.DAMPING_RATIO_HIGH_BOUNCY] (Damping ratio for a very bouncy
     * spring) and whose stiffness is [SpringForce.STIFFNESS_LOW] (spring with low stiffness which
     * applies less force when the spring is not at the final position). We also add an
     * `OnAnimationUpdateListener` which updates our [currentVelocity] with the current velocity of
     * our animation every animation frame.
     */
    val rotation: SpringAnimation = SpringAnimation(itemView, SpringAnimation.ROTATION)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )
        .addUpdateListener { _, _, velocity ->
            currentVelocity = velocity
        }

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation is used to bring the item back
     * after the over-scroll effect. We initialize it to a new instance which will animate the
     * [SpringAnimation.TRANSLATION_X] property of our [itemView] and which is configured to use a
     * [SpringForce] as the force that drives the animation whose final position is 0f, whose Spring
     * damping ratio is [SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY] (Damping ratio for a medium bouncy
     * spring), and whose stiffness is [SpringForce.STIFFNESS_LOW] (spring with low stiffness which
     * applies less force when the spring is not at the final position).
     */
    val translationX: SpringAnimation = SpringAnimation(itemView, SpringAnimation.TRANSLATION_X)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )
}

/**
 * Runs [action] on every visible [RecyclerView.ViewHolder] in this [RecyclerView].
 */
private inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

// The constants below are used to calculate the animation magnitude from values taken from UI
// events. Their values are determined empirically and can be modified to change the animation
// flavor.

/** The magnitude of rotation while the list is scrolled. */
private const val SCROLL_ROTATION_MAGNITUDE = 0.25f

/** The magnitude of rotation while the list is over-scrolled. */
private const val OVERSCROLL_ROTATION_MAGNITUDE = -10

/** The magnitude of translation distance while the list is over-scrolled. */
private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

/** The magnitude of translation distance when the list reaches the edge on fling. */
private const val FLING_TRANSLATION_MAGNITUDE = 0.5f
