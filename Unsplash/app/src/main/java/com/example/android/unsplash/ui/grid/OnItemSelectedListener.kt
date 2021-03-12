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
        object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })

    abstract fun onItemSelected(holder: RecyclerView.ViewHolder, position: Int)
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e)) {
            val touchedView = rv.findChildViewUnder(e.x, e.y)
            onItemSelected(
                rv.findContainingViewHolder(touchedView!!)!!,
                rv.getChildAdapterPosition(touchedView)
            )
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        throw UnsupportedOperationException("Not implemented")
    }

}