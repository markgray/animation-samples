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
package com.example.android.basictransition

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ViewAnimator
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentTransaction
import com.example.android.common.activities.SampleActivityBase
import com.example.android.common.logger.Log
import com.example.android.common.logger.LogFragment
import com.example.android.common.logger.LogWrapper
import com.example.android.common.logger.MessageOnlyLogFilter

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * `Fragment` which can display a view.
 *
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
class MainActivity : SampleActivityBase() {
    /**
     * Whether the Log Fragment is currently shown
     */
    private var mLogShown = false

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file `R.layout.activity_main`. If our [Bundle]
     * parameter [savedInstanceState] is `null` this is the first time we have been called so we
     * initialize our [FragmentTransaction] variable `val transaction` by using the `FragmentManager`
     * for interacting with fragments associated with this activity to start a series of edit
     * operations on the Fragments associated it. We initialize our [BasicTransitionFragment]
     * variable `val fragment` to a new instance, use `transaction` to replace any existing fragment
     * that was added to the container with ID `R.id.sample_content_fragment` with `fragment`.
     * We then schedule a commit of `transaction`.
     *
     * @param savedInstanceState If this is non-null we are being re-created after a configuration
     * change and the state of our [BasicTransitionFragment] has been saved by the system. If it is
     * `null` this is the first time we have been called so we need to create and add the fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<LinearLayout>(R.id.sample_main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = BasicTransitionFragment()
            transaction.replace(R.id.sample_content_fragment, fragment)
            transaction.commit()
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in to [menu]. We use a `MenuInflater` with our context to inflate our menu layout file
     * `R.menu.main` into our [Menu] parameter [menu] and return `true` to the caller so that the
     * [Menu] will be displayed.
     *
     * @param menu The options [Menu] in which you place your items.
     * @return You must return `true` for the [Menu] to be displayed,
     * if you return `false` it will not be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * Prepare the Screen's standard options menu to be displayed. This is called right before the
     * menu is shown, every time it is shown. You can use this method to efficiently enable/disable
     * items or otherwise dynamically modify the contents. We initialize our [MenuItem] variable
     * `val logToggle` by finding the [MenuItem] with ID `R.id.menu_toggle_log` and set the
     * visibility of `logToggle` to visible if the [View] with ID `R.id.sample_output` is a
     * [ViewAnimator] (as it is for screens with a width less than 720dp, for screens 720dp or wider
     * it is a `LinearLayout`), and to invisible if it is not (no need to toggle the log on wide
     * screens since it is always displayed). We then set the title of `logToggle` to "Hide Log"
     * if our [mLogShown] property is `true` or to "Show Log" if it is `false`. Finally we return
     * the value returned by our super's implementation of `onPrepareOptionsMenu` to our caller.
     *
     * @param menu The options menu as last shown or first initialized by [onCreateOptionsMenu].
     * @return You must return `true` for the menu to be displayed,
     * if you return `false` it will not be shown.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val logToggle = menu.findItem(R.id.menu_toggle_log)
        logToggle.isVisible = findViewById<View>(R.id.sample_output) is ViewAnimator
        logToggle.setTitle(if (mLogShown) R.string.sample_hide_log else R.string.sample_show_log)
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. When the identifier
     * of our [MenuItem] parameter [item] is `R.id.menu_toggle_log` we first toggle the value of our
     * field [mLogShown]. We initialize our [ViewAnimator] variable `val output` by finding the
     * view with ID `R.id.sample_output` and then branch on the new value of [mLogShown]:
     *  - `true`: we set the child view to be displayed of `output` to 1.
     *  - `false`: we set the child view to be displayed of `output` to 0.
     *
     * We then call [invalidateOptionsMenu] to declare that the options menu has changed, so should
     * be recreated, and return `true` to the caller to consume the event here.
     *
     * If the identifier of our [MenuItem] parameter [item] is not one of ours we return the value
     * returned by our super's implementation of [onOptionsItemSelected] to the caller.
     *
     * @param item The [MenuItem] that was selected.
     * @return [Boolean] Return `false` to allow normal menu processing to proceed,
     * `true` to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_toggle_log -> {
                mLogShown = !mLogShown
                val output = findViewById<View>(R.id.sample_output) as ViewAnimator
                if (mLogShown) {
                    output.displayedChild = 1
                } else {
                    output.displayedChild = 0
                }
                invalidateOptionsMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Create a chain of targets that will receive log data, called by the `onStart` override of
     * `SampleActivityBase`.
     */
    override fun initializeLogging() {
        /**
         * Wraps Android's native log framework.
         */
        val logWrapper = LogWrapper()
        /**
         * Using Log, front-end to the logging chain, emulates android.util.log method signatures.
         */
        Log.logNode = logWrapper
        /**
         * Filter strips out everything except the message text.
         */
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter
        /**
         * On screen logging via a fragment with a TextView.
         */
        val logFragment = supportFragmentManager
            .findFragmentById(R.id.log_fragment) as LogFragment?
        msgFilter.next = (logFragment ?: return).logView
        Log.i(TAG, "Ready")
    }

    companion object {
        /**
         * TAG used for logging
         */
        const val TAG: String = "MainActivity"
    }
}
