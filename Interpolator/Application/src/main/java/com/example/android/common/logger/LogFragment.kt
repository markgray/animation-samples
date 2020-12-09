/*
* Copyright 2013 The Android Open Source Project
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
/*
 * Copyright 2013 The Android Open Source Project
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
package com.example.android.common.logger

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment

/**
 * Simple fragment which contains a [LogView] and uses it to output log data it receives
 * through the [LogNode] interface.
 */
class LogFragment : Fragment() {
    /**
     * The [LogView] in our UI which is used to display log data, it is a custom `AppCompatTextView`
     * which implements the [LogNode] interface.
     */
    lateinit var logView: LogView
        private set

    /**
     * The [ScrollView] in our UI which holds our [LogView] field [logView]
     */
    private lateinit var mScrollView: ScrollView

    /**
     * Builds our UI layout using code, and returns the resulting [View] to the caller. First we
     * initialize our [ScrollView] field [mScrollView] with a new instance, create an instance of
     * [ViewGroup.LayoutParams] whose width and height are both `MATCH_PARENT` to initialize our
     * variable `val scrollParams`, and set the layout params of [mScrollView] to it. We initialize
     * our [LogView] field [logView] with a new instance, initialize our variable `val logParams` to
     * a copy of `scrollParams`, set its height to `WRAP_CONTENT`, and then set the layout params of
     * [logView] to `logParams`. We enable click events for [logView], enable it to receive focus,
     * and set its typeface to [Typeface.MONOSPACE]. We use the logical density of the display to
     * calculate how many pixels there are in 16 dips to initialize our variable `val paddingPixels`
     * then set the padding on all sides of [logView] to `paddingPixels`, and also set the size of
     * the padding between the compound drawables and the text to `paddingPixels`. We set the
     * `gravity` of [logView] to [Gravity.BOTTOM] and set the text appearance to the system style
     * resource `TextAppearance_Holo_Medium` (using the one argument version of `setTextAppearance`
     * for devices `M` and newer, and the deprecated two argument version for older devices). We
     * then add [logView] to [mScrollView] and return [mScrollView] to the caller.
     *
     * @return a [View] which consists of a [ScrollView] holding our [LogView] field [logView].
     */
    private fun inflateViews(): View {
        mScrollView = ScrollView(activity)
        val scrollParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mScrollView.layoutParams = scrollParams
        logView = LogView(activity)
        val logParams = ViewGroup.LayoutParams(scrollParams)
        logParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        logView.layoutParams = logParams
        logView.isClickable = true
        logView.isFocusable = true
        logView.typeface = Typeface.MONOSPACE

        // Want to set padding as 16 dips, setPadding takes pixels.  Hooray math!
        val paddingDips = 16
        val scale = resources.displayMetrics.density.toDouble()
        val paddingPixels = (paddingDips * scale + .5).toInt()
        logView.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels)
        logView.compoundDrawablePadding = paddingPixels
        logView.gravity = Gravity.BOTTOM
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            logView.setTextAppearance(android.R.style.TextAppearance_Holo_Medium)
        } else {
            @Suppress("DEPRECATION")
            logView.setTextAppearance(activity, android.R.style.TextAppearance_Holo_Medium)
        }
        mScrollView.addView(logView)
        return mScrollView
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to **only** inflate the layout in this
     * method and move logic that operates on the returned [View] to [onViewCreated]. We initialize
     * our [View] variable `val result` to the [View] returned by our [inflateViews] method, and then
     * add a [TextWatcher] to [logView] whose `afterTextChanged` override scrolls [mScrollView] to
     * the bottom of the view whenever the text in [logView] changes. Finally we return `result` to
     * the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result = inflateViews()
        logView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        })
        return result
    }
}