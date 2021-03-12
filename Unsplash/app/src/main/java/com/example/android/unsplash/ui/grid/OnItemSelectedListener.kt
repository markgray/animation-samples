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
package com.example.android.unsplash.ui.grid

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.example.android.unsplash.DetailActivity
import com.example.android.unsplash.MainActivity
import com.example.android.unsplash.data.model.Photo

/**
 * This custom [OnItemTouchListener] is added as the [OnItemTouchListener] for the grid [RecyclerView]
 * in the `populateGrid` method of [MainActivity], and its [onItemSelected] method is overridden by
 * a method which will do all that is necessary to start up [DetailActivity] and have it display the
 * [Photo] that the user clicked in that [RecyclerView].
 */
abstract class OnItemSelectedListener(context: Context?) : OnItemTouchListener {
    /**
     * The [GestureDetector] we use to catch a [MotionEvent] that is an up motion event in its
     * `onSingleTapUp` override, in order to return `true` to consume the event. Our override of
     * [onInterceptTouchEvent] will use this object to determine if the [MotionEvent] it intercepted
     * is a touch event, in which case it will determine the `ViewHolder` of the child view of the
     * [RecyclerView] that was touched as well as its adapter position and then pass these as arguments
     * to the override of our [onItemSelected] method.
     */
    private val gestureDetector: GestureDetector = GestureDetector(context,
        /**
         * A [SimpleOnGestureListener] is a [GestureDetector.OnGestureListener] which allows one to
         * to listen for a subset of all the gestures, in our case only the `onSingleTapUp` gesture.
         */
        object : SimpleOnGestureListener() {
            /**
             * Notified when a tap occurs with the up [MotionEvent] that triggered it. We just return
             * `true` to consume the gesture. This will cause the [GestureDetector.onTouchEvent] method
             * of [gestureDetector] to return `true` so our override of [onInterceptTouchEvent] can use
             * if to detect that the user selected a [Photo] that they want to display in
             * [DetailActivity] and then use the [MotionEvent] to locate the [Photo] in the [RecyclerView]
             * and then call the override of [onItemSelected] to launch [DetailActivity] to display it.
             *
             * @param e The up motion event that completed the first tap
             * @return `true` if the event is consumed, else `false`
             */
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })

    /**
     * Instances of [OnItemSelectedListener] must override this to learn which [Photo] object the
     * user has clicked. It will be called with the [RecyclerView.ViewHolder] parameter [holder]
     * containing the view holder which contains the view and the adapter position of the view will
     * be in the parameter [position].
     *
     * @param holder the [RecyclerView.ViewHolder] which contains the view that was clicked.
     * @param position the adapter position corresponding to the view that was clicked.
     */
    abstract fun onItemSelected(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * Silently observe and/or take over touch events sent to the [RecyclerView] before they are
     * handled by either the [RecyclerView] itself or its child views. The `onInterceptTouchEvent`
     * methods of each attached [OnItemTouchListener] will be run in the order in which each listener
     * was added, before any other touch processing by the [RecyclerView] itself or its child views
     * occurs.
     *
     * If the [GestureDetector.onTouchEvent] method of our [GestureDetector] field [gestureDetector]
     * detects that the [MotionEvent] parameter [e] that we intercepted is a touch event we use
     * the [RecyclerView.findChildViewUnder] method of our [RecyclerView] parameter [rv] to find the
     * [View] under the point located at the X coordinate and the Y coordinate of [MotionEvent] parameter
     * [e] to initialize our [View] variable `val touchedView`. We then call the override of our
     * [onItemSelected] method with the [RecyclerView.ViewHolder] that contains `touchedView` that
     * the [RecyclerView.findContainingViewHolder] method of [rv] returns, and the adapter position
     * corresponding to `touchedView` that the [RecyclerView.getChildAdapterPosition] method returns.
     * Whether is was a touch event or not we always return `false` to continue observing future
     * events as before.
     *
     * @param rv the [RecyclerView] whose touch event we are intercepting.
     * @param e [MotionEvent] describing the touch event. All coordinates are in the [RecyclerView]'s
     * coordinate system.
     * @return `true` if this [OnItemTouchListener] wishes to begin intercepting touch events, `false`
     * to continue with the current behavior and continue observing future events in the gesture. We
     * always return `false`.
     */
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e)) {
            val touchedView: View? = rv.findChildViewUnder(e.x, e.y)
            onItemSelected(
                rv.findContainingViewHolder(touchedView!!)!!,
                rv.getChildAdapterPosition(touchedView)
            )
        }
        return false
    }

    /**
     * Process a touch event as part of a gesture that was claimed by returning `true` from a
     * previous call to [onInterceptTouchEvent]. We always throw [UnsupportedOperationException].
     *
     * @param rv the [RecyclerView] which received that touch event.
     * @param e MotionEvent describing the touch event. All coordinates are in
     *          the RecyclerView's coordinate system.
     */
    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        throw UnsupportedOperationException("Not implemented")
    }

    /**
     * Called when a child of [RecyclerView] does not want [RecyclerView] and its ancestors to
     * intercept touch events with [onInterceptTouchEvent]. We throw [UnsupportedOperationException].
     *
     * @param disallowIntercept `true` if the child does not want the parent to intercept touch events.
     */
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        throw UnsupportedOperationException("Not implemented")
    }

}