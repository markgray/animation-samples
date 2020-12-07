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
 * This sample demonstrates the use of animation interpolators and path animations for Material
 * Design. It shows how an [android.animation.ObjectAnimator] is used to animate two properties
 * of a [View] (scale X and Y) along a path using the user selected animation interpolator.
 */
class InterpolatorFragment : Fragment() {
    /**
     * [View] that is animated.
     */
    private lateinit var mView: View

    /**
     * [Spinner] for selection of interpolator.
     */
    private lateinit var mInterpolatorSpinner: Spinner

    /**
     * [SeekBar] for selection of duration of animation.
     */
    private lateinit var mDurationSeekbar: SeekBar

    /**
     * [TextView] that shows animation selected in SeekBar.
     */
    private lateinit var mDurationLabel: TextView

    /**
     * The array of Interpolators available to be used for animation, the one used is selected by
     * the [Spinner] field [mInterpolatorSpinner].
     */
    lateinit var interpolators: Array<Interpolator>
        private set

    /**
     * Path for in (shrinking) animation, from 100% scale to 20%.
     */
    lateinit var pathIn: Path
        private set

    /**
     * Path for out (growing) animation, from 20% to 100%.
     */
    lateinit var pathOut: Path
        private set

    /**
     * Set to `true` if View is animated out (is shrunk).
     */
    private var mIsOut = false

    /**
     * Names of the available interpolators.
     */
    private lateinit var mInterpolatorNames: Array<String>

    /**
     * Called to do initial creation of a fragment. This is called after [onAttach] and before
     * [onCreateView]. Note that this can be called while the fragment's activity is still in the
     * process of being created. As such, you can not rely on things like the activity's content
     * view hierarchy being initialized at this point.  If you want to do work once the activity
     * itself is created, see [onActivityCreated]. Any restored child fragments will be created
     * before the base [Fragment.onCreate] method returns.
     *
     * First we call our super's implementation of `onCreate` then we call our [initInterpolators]
     * method to have it load our [interpolators] array with [Interpolator] objects loaded from
     * `android.R.interpolator.*` resource IDs. We load our array of strings [mInterpolatorNames]
     * from our resource string array [R.array.interpolator_names], with each string naming the
     * corresponding entry in [interpolators]. Finally we call our method [initPaths] to have it
     * initialize the paths ([pathIn] and [pathOut]) that are used by the [ObjectAnimator] to scale
     * the view.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInterpolators()
        mInterpolatorNames = resources.getStringArray(R.array.interpolator_names)
        initPaths()
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to _only_ inflate the layout in this
     * method and move logic that operates on the returned [View] to [onViewCreated].
     *
     * We just return the [View] that our [LayoutInflater] parameter [inflater] inflates from our
     * layout file [R.layout.interpolator_fragment] with our [ViewGroup] parameter [container]
     * supplying the LayoutParams of the view.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself, but this
     * can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or `null`. In our case we always
     * return a [View] inflated from our layout file [R.layout.interpolator_fragment].
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.interpolator_fragment, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once they
     * know their view hierarchy has been completely created.  The fragment's view hierarchy is not
     * however attached to its parent at this point.
     *
     * First we call our method [initAnimateButton] with our [View] parameter [view] to have it set
     * up the 'animate' button so that when it is clicked the [View] field [mView] is animated with
     * the options currently selected (the Interpolator, duration and animation path). Then we
     * initialize our [TextView] field [mDurationLabel] by finding the [View] in [view] with ID
     * [R.id.durationLabel] (displays the duration selected by the [SeekBar]), and initialize our
     * [Spinner] field [mInterpolatorSpinner] by finding the [View] in [view] with ID
     * [R.id.interpolatorSpinner] (allows the user to select which kind of [Interpolator] is used
     * to animate our [View] field [mView]). We initialize our [ArrayAdapter] variable
     * `val spinnerAdapter` to an instance constructed to display our string array [mInterpolatorNames]
     * using the system layout file [android.R.layout.simple_spinner_dropdown_item] for each of the
     * strings in [mInterpolatorNames]. We then set the adapter of [mInterpolatorSpinner] to
     * `spinnerAdapter`. We call our method [initSeekbar] with [view] to have it set up the [SeekBar]
     * in [view] with ID [R.id.durationSeek] (our field [mDurationSeekbar]) to use an
     * [OnSeekBarChangeListener] which updates the text in [mDurationLabel] when the user uses the
     * [SeekBar] to change the duration of the animation. Finally we initialize our [View] field
     * [mView] (the [View] that is animated) by finding the [View] in [view] with ID [R.id.square],
     * and then call our super's implementation of `onViewCreated`.
     *
     * @param view The [View] returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAnimateButton(view)

        // Get the label to display the selected duration
        mDurationLabel = view.findViewById(R.id.durationLabel)

        // Set up the Spinner with the names of interpolators.
        mInterpolatorSpinner = view.findViewById(R.id.interpolatorSpinner)
        val spinnerAdapter = ArrayAdapter(
            activity!!,
            android.R.layout.simple_spinner_dropdown_item,
            mInterpolatorNames
        )
        mInterpolatorSpinner.adapter = spinnerAdapter
        initSeekbar(view)

        // Get the view that will be animated
        mView = view.findViewById(R.id.square)
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Set up the 'animate' button, so that when it is clicked the view is animated with the options
     * selected: the Interpolator (selected by the [Spinner] field [mInterpolatorSpinner]), duration
     * (selected by the [SeekBar] field [mDurationSeekbar]) and animation path (toggles between
     * the [Path] field [pathOut] and the [Path] field [pathIn] depending on the current value of
     * the [Boolean] field [mIsOut]). First we initialize our [View] variable `val button` by finding
     * the [View] in our [View] parameter [view] with ID [R.id.animateButton] then we set the
     * [View.OnClickListener] of `button` to an anonymous class whose `onClick` override animates
     * our [View] field [mView] according to the current setting of [mInterpolatorSpinner],
     * [mDurationSeekbar], and our in/out toggle field [mIsOut] then toggles the value of [mIsOut]
     * to get ready for the next click of `button`.
     *
     * @param view The view holding the button.
     */
    @Suppress("ObjectLiteralToLambda")
    private fun initAnimateButton(view: View) {
        val button = view.findViewById<View>(R.id.animateButton)
        button.setOnClickListener(object : View.OnClickListener {
            /**
             * Called when the button with ID [R.id.animateButton] is clicked. First we initialize
             * our [Int] variable `val selectedItemPosition` to the position of the currently selected
             * item within the adapter's data set of our [Spinner] field [mInterpolatorSpinner]. Then
             * we initialize our [Interpolator] variable `val interpolator` to the entry in our array
             * [interpolators] with index `selectedItemPosition`.
             *
             * @param view the [View] that was clicked.
             */
            @SuppressLint("DefaultLocale")
            override fun onClick(view: View) {
                // Interpolator selected in the spinner
                val selectedItemPosition = mInterpolatorSpinner.selectedItemPosition
                val interpolator = interpolators[selectedItemPosition]
                // Duration selected in SeekBar
                val duration = mDurationSeekbar.progress.toLong()
                // Animation path is based on whether animating in or out
                val path: Path = if (mIsOut) pathIn else pathOut

                // Log animation details
                Log.i(TAG, String.format("Starting animation: [%d ms, %s, %s]",
                    duration, mInterpolatorSpinner.selectedItem as String,
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
        mDurationSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                mDurationLabel.text = resources.getString(R.string.animation_duration, i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Set initial progress to trigger SeekBarChangeListener and update UI
        mDurationSeekbar.progress = INITIAL_DURATION_MS
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
        pathIn.moveTo(0.2f, 0.2f)
        pathIn.lineTo(1f, 1f)

        // Path for 'out' animation: shrinking from 100% to 20%
        pathOut = Path()
        pathOut.moveTo(1f, 1f)
        pathOut.lineTo(0.2f, 0.2f)
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