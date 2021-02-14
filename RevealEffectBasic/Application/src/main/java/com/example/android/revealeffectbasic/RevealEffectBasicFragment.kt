/*
 * Copyright 2014 The Android Open Source Project
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
package com.example.android.revealeffectbasic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.example.android.common.logger.Log.d
import kotlin.math.hypot

/**
 * This sample shows a view that is revealed when a button is clicked.
 */
class RevealEffectBasicFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reveal_effect_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val shape = view.findViewById<View>(R.id.circle)
        val button = view.findViewById<View>(R.id.button)
        // Set a listener to reveal the view when clicked.
        button.setOnClickListener { // Create a reveal {@link Animator} that starts clipping the view from
            // the top left corner until the whole view is covered.
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                shape,
                0,
                0, 0f,
                hypot(shape.width.toDouble(), shape.height.toDouble()).toFloat())
            circularReveal.interpolator = AccelerateDecelerateInterpolator()

            // Finally start the animation
            circularReveal.start()
            d(TAG, "Starting Reveal animation")
        }
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val TAG = "RevealEffectBasicFragment"
    }
}