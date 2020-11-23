/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.gridtopager.fragment

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.gridtopager.MainActivity
import com.google.samples.gridtopager.R
import com.google.samples.gridtopager.adapter.GridAdapter

/**
 * A fragment for displaying a grid of images.
 */
class GridFragment : Fragment() {
    /**
     * The [RecyclerView] produced by inflating our layout file [R.layout.fragment_grid]
     */
    private lateinit var recyclerView: RecyclerView

    /**
     * Called to have the fragment instantiate its user interface view. We initialize our
     * [RecyclerView] field [recyclerView] by using our [LayoutInflater] parameter [inflater]
     * to inflate our layout file [R.layout.fragment_grid] with our [ViewGroup] parameter
     * [container] supplying the `LayoutParams`. We then set the adapter of [recyclerView] to
     * a new instance of [GridAdapter]. We call our method [prepareTransitions] to prepare the
     * shared element transition to the pager fragment, as well as the other transitions that
     * affect the flow. Then we call the [Fragment] method [postponeEnterTransition] to postpone
     * the entering Fragment transition until [startPostponedEnterTransition] or `FragmentManager`
     * method `executePendingTransactions` has been called. Finally we return [recyclerView] to our
     * caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI will be attached
     * to. The fragment should not add the view itself, but this can be used to generate the
     * `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.     *
     * @return Return the [View] for the fragment's UI.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        recyclerView = inflater.inflate(
                R.layout.fragment_grid,
                container,
                false
        ) as RecyclerView
        recyclerView.adapter = GridAdapter(this)
        prepareTransitions()
        postponeEnterTransition()
        return recyclerView
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once they
     * know their view hierarchy has been completely created. The fragment's view hierarchy is not
     * however attached to its parent at this point. First we call our super's implementation of
     * `onViewCreated`, then we call our method [scrollToPosition] to scroll the recycler view to
     * show the last viewed item in the grid. This is important when navigating back to the grid.
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollToPosition()
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back to the grid. We add an [OnLayoutChangeListener] to our [RecyclerView] field
     * [recyclerView] whose `onLayoutChange` override first removes itself as an [OnLayoutChangeListener]
     * from [recyclerView]. It then initializes its [RecyclerView.LayoutManager] variable
     * `val layoutManager` to the [RecyclerView.LayoutManager] currently responsible for layout
     * policy for [recyclerView]. It then uses the `findViewByPosition` method of `layoutManager`
     * to find the [View] at position [MainActivity.currentPosition] in its adapter. Then if the
     * view for the current position is `null` (not currently part of layout manager children), or
     * it's not completely visible it uses the `post` method of [recyclerView] to cause a [Runnable]
     * lambda to be added to its message queue which calls the `scrollToPosition` method of
     * `layoutManager` to have it scroll to adapter position [MainActivity.currentPosition].
     */
    private fun scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(object : OnLayoutChangeListener {
            /**
             * Called when the layout bounds of a view changes due to layout processing. First we
             * remove ourself as an [OnLayoutChangeListener] from [recyclerView]. Then we initialize
             * our [RecyclerView.LayoutManager] variable `val layoutManager` to the `LayoutManager`
             * currently responsible for layout policy for [recyclerView]. Then we use the
             * `findViewByPosition` method of `layoutManager` to find the [View] at position
             * [MainActivity.currentPosition] in its adapter. If the view for the current position
             * is null (not currently part of the layout manager's children), or it's not completely
             * visible we use the `post` method of [recyclerView] to cause a [Runnable] lambda to
             * be added to its message queue which calls the `scrollToPosition` method of
             * `layoutManager` to have it scroll to adapter position [MainActivity.currentPosition].
             *
             * @param v The view whose bounds have changed.
             * @param left The new value of the view's left property.
             * @param top The new value of the view's top property.
             * @param right The new value of the view's right property.
             * @param bottom The new value of the view's bottom property.
             * @param oldLeft The previous value of the view's left property.
             * @param oldTop The previous value of the view's top property.
             * @param oldRight The previous value of the view's right property.
             * @param oldBottom The previous value of the view's bottom property.
             */
            override fun onLayoutChange(
                    v: View,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
            ) {
                recyclerView.removeOnLayoutChangeListener(this)
                val layoutManager: RecyclerView.LayoutManager? = recyclerView.layoutManager
                val viewAtPosition = layoutManager!!.findViewByPosition(MainActivity.currentPosition)
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                                .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post {
                        layoutManager.scrollToPosition(MainActivity.currentPosition)
                    }
                }
            }
        })
    }

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other transitions
     * that affect the flow. First we set the Transition that will be used to move Views out of the
     * scene when the fragment is removed, hidden, or detached when not popping the back stack to
     * the [Transition] that the [TransitionInflater] from the the `Context` this fragment is currently
     * associated with inflates from the resource file [R.transition.grid_exit_transition] (it is a
     * `fade` transition using the fast out slow in interpolator with a duration of 375 and a start
     * delay of 25 whose target ID is the `CardView` with ID [R.id.card_view] in the layout file
     * layout/image_card.xml which is the layout file that `GridAdapter` uses for its item views).
     * Then we set the exit transition callback that is called when this [Fragment] is attached or
     * detached when popping the back stack to an anonymous [SharedElementCallback] whose
     * `onMapSharedElements` override sets its [RecyclerView.ViewHolder] variable 
     */
    private fun prepareTransitions() {
        exitTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.grid_exit_transition)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                            names: List<String>,
                            sharedElements: MutableMap<String,
                                    View>
                    ) {
                        // Locate the ViewHolder for the clicked position.
                        val selectedViewHolder: RecyclerView.ViewHolder
                            = recyclerView.findViewHolderForAdapterPosition(MainActivity.currentPosition)
                            ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.card_image)
                    }
                })
    }
}