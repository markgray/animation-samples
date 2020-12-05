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
package com.example.android.interpolator

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.android.common.logger.Log

/**
 * This sample demonstrates the use of animation interpolators and path animations for
 * Material Design.
 * It shows how an [android.animation.ObjectAnimator] is used to animate two properties of a
 * view (scale X and Y) along a path.
 */
class InterpolatorFragment : Fragment() {
    /**
     * View that is animated.
     */
    private var mView: View? = null

    /**
     * Spinner for selection of interpolator.
     */
    private var mInterpolatorSpinner: Spinner? = null

    /**
     * SeekBar for selection of duration of animation.
     */
    private var mDurationSeekbar: SeekBar? = null

    /**
     * TextView that shows animation selected in SeekBar.
     */
    private var mDurationLabel: TextView? = null
    /**
     * Return the array of loaded Interpolators available in this Fragment.
     *
     * @return Interpolators
     */
    /**
     * Interpolators used for animation.
     */
    lateinit var interpolators: Array<Interpolator>
        private set
    /**
     * @return The animation path for the 'in' (shrinking) animation.
     */
    /**
     * Path for in (shrinking) animation, from 100% scale to 20%.
     */
    var pathIn: Path? = null
        private set
    /**
     * @return The animation path for the 'out' (growing) animation.
     */
    /**
     * Path for out (growing) animation, from 20% to 100%.
     */
    var pathOut: Path? = null
        private set

    /**
     * Set to true if View is animated out (is shrunk).
     */
    private var mIsOut = false

    /**
     * Names of the available interpolators.
     */
    private lateinit var mInterpolatorNames: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInterpolators()
        mInterpolatorNames = resources.getStringArray(R.array.interpolator_names)
        initPaths()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interpolator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAnimateButton(view)

        // Get the label to display the selected duration
        mDurationLabel = view.findViewById(R.id.durationLabel)

        // Set up the Spinner with the names of interpolators.
        mInterpolatorSpinner = view.findViewById(R.id.interpolatorSpinner)
        val spinnerAdapter = ArrayAdapter(activity!!,
            android.R.layout.simple_spinner_dropdown_item, mInterpolatorNames)
        mInterpolatorSpinner!!.adapter = spinnerAdapter
        initSeekbar(view)


        // Get the view that will be animated
        mView = view.findViewById(R.id.square)
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Set up the 'animate' button, when it is clicked the view is animated with the options
     * selected: the Interpolator, duration and animation path
     *
     * @param view The view holding the button.
     */
    @Suppress("ObjectLiteralToLambda")
    private fun initAnimateButton(view: View) {
        val button = view.findViewById<View>(R.id.animateButton)
        button.setOnClickListener(object : View.OnClickListener {
            @SuppressLint("DefaultLocale")
            override fun onClick(view: View) {
                // Interpolator selected in the spinner
                val selectedItemPosition = mInterpolatorSpinner!!.selectedItemPosition
                val interpolator = interpolators[selectedItemPosition]
                // Duration selected in SeekBar
                val duration = mDurationSeekbar!!.progress.toLong()
                // Animation path is based on whether animating in or out
                val path: Path = if (mIsOut) pathIn!! else pathOut!!

                // Log animation details
                Log.i(TAG, String.format("Starting animation: [%d ms, %s, %s]",
                    duration, mInterpolatorSpinner!!.selectedItem as String,
                    if (mIsOut) "Out (growing)" else "In (shrinking)"))

                // Start the animation with the selected options
                startAnimation(interpolator, duration, path)

                // Toggle direction of animation (path)
                mIsOut = !mIsOut
            }
        })
    }

    /**
     * Set up SeekBar that defines the duration of the animation
     *
     * @param view The view holding the button.
     */
    private fun initSeekbar(view: View) {
        mDurationSeekbar = view.findViewById<View>(R.id.durationSeek) as SeekBar

        // Register listener to update the text label when the SeekBar value is updated
        mDurationSeekbar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                mDurationLabel!!.text = resources.getString(R.string.animation_duration, i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Set initial progress to trigger SeekBarChangeListener and update UI
        mDurationSeekbar!!.progress = INITIAL_DURATION_MS
    }

    /**
     * Start an animation on the sample view.
     * The view is animated using an [android.animation.ObjectAnimator] on the
     * [View.SCALE_X] and [View.SCALE_Y] properties, with its animation based on a
     * path.
     * The only two paths defined here ([.mPathIn] and [.mPathOut]) scale the view
     * uniformly.
     *
     * @param interpolator The interpolator to use for the animation.
     * @param duration Duration of the animation in ms.
     * @param path Path of the animation
     * @return The ObjectAnimator used for this animation
     * @see android.animation.ObjectAnimator.ofFloat
     */
    fun startAnimation(interpolator: Interpolator?, duration: Long, path: Path?): ObjectAnimator {
        // This ObjectAnimator uses the path to change the x and y scale of the mView object.
        val animator = ObjectAnimator.ofFloat(mView, View.SCALE_X, View.SCALE_Y, path)

        // Set the duration and interpolator for this animation
        animator.duration = duration
        animator.interpolator = interpolator
        animator.start()
        return animator
    }

    /**
     * Initialize interpolators programmatically by loading them from their XML definitions
     * provided by the framework.
     */
    private fun initInterpolators() {
        interpolators = arrayOf(
            AnimationUtils.loadInterpolator(activity,
                android.R.interpolator.linear),
            AnimationUtils.loadInterpolator(activity,
                android.R.interpolator.fast_out_linear_in),
            AnimationUtils.loadInterpolator(activity,
                android.R.interpolator.fast_out_slow_in),
            AnimationUtils.loadInterpolator(activity,
                android.R.interpolator.linear_out_slow_in)
        )
    }

    /**
     * Initializes the paths that are used by the ObjectAnimator to scale the view.
     */
    private fun initPaths() {
        // Path for 'in' animation: growing from 20% to 100%
        pathIn = Path()
        pathIn!!.moveTo(0.2f, 0.2f)
        pathIn!!.lineTo(1f, 1f)

        // Path for 'out' animation: shrinking from 100% to 20%
        pathOut = Path()
        pathOut!!.moveTo(1f, 1f)
        pathOut!!.lineTo(0.2f, 0.2f)
    }

    companion object {
        /**
         * Default duration of animation in ms.
         */
        private const val INITIAL_DURATION_MS = 750

        /**
         * String used for logging.
         */
        const val TAG = "InterpolatorPlaygroundFragment"
    }
}