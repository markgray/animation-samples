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

package com.example.android.motion.demo.reorder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.R
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge
import com.example.android.motion.widget.SpaceDecoration

/**
 * List > Reorder
 *
 * Motion makes it clear when items are selected and what will happen when they’re released. The
 * UI consists of a [RecyclerView] with a `StaggeredGridLayoutManager` as the layout manager which
 * allows you to drag and drop its [Cheese] displaying item views into a new position in the layout
 * with an animation occuring which moves the original item view in the new position to the position
 * the dragged item view last occupied. (Try moving a [Cheese] a long way and you will see what I
 * mean by that.)
 */
class ReorderActivity : AppCompatActivity() {

    /**
     * Our [ReorderViewModel] view model, it holds a [List] of all of the [Cheese] objects in our
     * [Cheese.ALL] list of cheeses in its [ReorderViewModel.cheeses] field (this is the public
     * read only access to its private [MutableLiveData] wrapped [MutableList] `_cheeses`) and a
     * method [ReorderViewModel.move] which allows you to move a [Cheese] object in `_cheeses` to
     * a different position.
     */
    private val viewModel: ReorderViewModel by viewModels()

    /**
     * The elevation that an item view is elevated when it begins its drag (needs to be long clicked
     * first). It is set to [R.dimen.pick_up_elevation] (8dp) in our [onCreate] override.
     */
    private var pickUpElevation: Float = 0f

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.reorder_activity] which consists of
     * a `CoordinatorLayout` holding an `AppBarLayout` (which holds a [Toolbar] widget) and a
     * [RecyclerView] which uses a `StaggeredGridLayoutManager` as its layout manager.
     *
     * We initialize our [Toolbar] variable `val toolbar` by finding the view with ID [R.id.toolbar]
     * and our [RecyclerView] variable `val list` by finding the view with ID [R.id.list]. We use the
     * [setSupportActionBar] method to set `toolbar` to act as the ActionBar for our activity's
     * window, then call our [EdgeToEdge.setUpRoot] to configure the view with ID [R.id.root] (the
     * root `CoordinatorLayout` of our layout file) for edge to edge display, call our [EdgeToEdge.setUpAppBar]
     * method to configure our app bar with ID [R.id.app_bar] and our [Toolbar] toolbar for edge to
     * edge display, and call our [EdgeToEdge.setUpScrollingContent] method to configure our
     * [RecyclerView] `list` for edge to edge display. We then initialize our field [pickUpElevation]
     * to the dimension stored in our resources under the ID [R.dimen.pick_up_elevation] (8dp) and
     * add an [RecyclerView.ItemDecoration] to `list` consisting of a [SpaceDecoration] whose value
     * is stored under the ID [R.dimen.spacing_small] in our resources (8dp).
     *
     * Next we initialize our [ItemTouchHelper] variable `val itemTouchHelper` to a new instance that
     * will work with our [ItemTouchHelper.Callback] field [touchHelperCallback] (this handles view
     * drag inside a [RecyclerView]), and then use the `attachToRecyclerView` method of `itemTouchHelper`
     * to attach it to our [RecyclerView] `list`.
     *
     * Next we initialize our [CheeseGridAdapter] variable `val adapter` with an instance constructed
     * to use a lambda for its [CheeseGridAdapter.onItemLongClick] field which calls the `startDrag`
     * method of `itemTouchHelper` to start dragging the `itemView` of the [RecyclerView.ViewHolder]
     * that the user chooses when it is long-pressed ([CheeseGridAdapter.onItemLongClick] is set to
     * be the `OnLongClickListener` of all of the `itemView` displayed by the [RecyclerView]). We
     * set the `adapter` of `list` to the `adapter` constructed above.
     *
     * Finally we add an observer to the [ReorderViewModel.cheeses] field of [viewModel] whose lambda
     * will submit the changed list of [Cheese] objects to `adapter` to be diffed and displayed (every
     * time the items are reordered on the screen, the lambda will receive a new list, and the adapter
     * takes a diff between the old and the new lists, and animates any moving items by the
     * [RecyclerView.ItemAnimator] of `list` which is `DefaultItemAnimator` in our case).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reorder_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val list: RecyclerView = findViewById(R.id.list)
        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(list)
        pickUpElevation = resources.getDimensionPixelSize(R.dimen.pick_up_elevation).toFloat()
        list.addItemDecoration(
            SpaceDecoration(resources.getDimensionPixelSize(R.dimen.spacing_small))
        )

        // The ItemTouchHelper handles view drag inside the RecyclerView.
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list)

        val adapter = CheeseGridAdapter(onItemLongClick = { holder ->
            // Start dragging the item when it is long-pressed.
            itemTouchHelper.startDrag(holder)
        })
        list.adapter = adapter

        viewModel.cheeses.observe(this) { cheeses ->
            // Every time the items are reordered on the screen, we receive a new list here.
            // The adapter takes a diff between the old and the new lists, and animates any moving
            // items by ItemAnimator.
            adapter.submitList(cheeses)
        }
    }

    /**
     * The [ItemTouchHelper.Callback] used for the [ItemTouchHelper] `val itemTouchHelper` that our
     * [onCreate] override attaches to the [RecyclerView] that displays our [Cheese] objects. It
     * lets us control which touch behaviors are enabled for each ViewHolder and also receives
     * callbacks when the user performs these actions.
     */
    private val touchHelperCallback = object : ItemTouchHelper.Callback() {

        /**
         * Should return a composite flag which defines the enabled move directions in each state
         * (idle, swiping, dragging). This flag is composed of 3 sets of 8 bits, where first 8 bits
         * are for IDLE state, next 8 bits are for SWIPE state and third 8 bits are for DRAG state.
         * [ItemTouchHelper]. We just return the movement flags returned by the `makeMovementFlags`
         * method of [ItemTouchHelper] asked to allow [ItemTouchHelper.UP], [ItemTouchHelper.DOWN],
         * [ItemTouchHelper.LEFT], and [ItemTouchHelper.RIGHT] but disallow swiping away.
         *
         * @param recyclerView The [RecyclerView] to which [ItemTouchHelper] is attached.
         * @param viewHolder   The [RecyclerView.ViewHolder] for which the movement information is
         * necessary.
         * @return flags specifying which movements are allowed on this [RecyclerView.ViewHolder].
         */
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(
                // We allow items to be dragged in any direction.
                ItemTouchHelper.UP
                        or ItemTouchHelper.DOWN
                        or ItemTouchHelper.LEFT
                        or ItemTouchHelper.RIGHT,
                // But not swiped away.
                0
            )
        }

        /**
         * Called when [ItemTouchHelper] wants to move the dragged item from its old position to
         * the new position. If this method returns `true`, [ItemTouchHelper] assumes the the
         * [RecyclerView.ViewHolder] parameter [viewHolder] has been moved to the adapter position
         * of the [RecyclerView.ViewHolder] parameter [target].
         *
         * @param recyclerView The [RecyclerView] to which [ItemTouchHelper] is attached to.
         * @param viewHolder   The [RecyclerView.ViewHolder] which is being dragged by the user.
         * @param target       The [RecyclerView.ViewHolder] over which the currently active item is
         * being dragged.
         * @return `true` if the [viewHolder] has been moved to the adapter position of [target].
         */
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Reorder the items in the ViewModel. The ViewModel will then notify the UI through the
            // LiveData.
            viewModel.move(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Do nothing
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            val view = viewHolder?.itemView ?: return
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    ViewCompat.animate(view).setDuration(150L).translationZ(pickUpElevation)
                }
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            ViewCompat.animate(viewHolder.itemView).setDuration(150L).translationZ(0f)
        }

        override fun isLongPressDragEnabled(): Boolean {
            // We handle the long press on our side for better touch feedback.
            return false
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return false
        }
    }
}
