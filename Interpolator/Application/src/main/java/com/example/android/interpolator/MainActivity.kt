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
package com.example.android.interpolator

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ViewAnimator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.android.common.activities.SampleActivityBase
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.android.common.logger.Log
import com.example.android.common.logger.LogFragment
import com.example.android.common.logger.LogWrapper
import com.example.android.common.logger.MessageOnlyLogFilter

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * [Fragment] which can display a view.
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
     *
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to edge
     * mode, then we call our super's implementation of `onCreate`, and set our content view to our
     * layout file `R.layout.activity_main` (either layout/activity_main.xml, or
     * layout-w720dp/activity_main.xml for devices 720dp wide or wider).
     *
     * We initialize our [LinearLayout] variable `rootView` by finding the view with ID
     * `R.id.sample_main_layout`, then we use the [ViewCompat.setOnApplyWindowInsetsListener]
     * method to set an [OnApplyWindowInsetsListener] to take over the policy for applying window
     * insets to `rootView`, with the `listener` argument a lambda that accepts the [View] passed
     * the lambda in variable `v` and the [WindowInsetsCompat] passed the lambda in variable
     * `windowInsets`. It initializes its [Insets] variable `systemBars` to the
     * [WindowInsetsCompat.getInsets] of `windowInsets` with [WindowInsetsCompat.Type.systemBars]
     * as the argument. It then gets the insets for the IME (keyboard) using
     * [WindowInsetsCompat.Type.ime]. It then updates the layout parameters of `v` to be a
     * [ViewGroup.MarginLayoutParams] with the left margin set to `systemBars.left`, the right
     * margin set to `systemBars.right`, the top margin set to `systemBars.top`, and the bottom
     * margin set to the maximum of the system bars bottom inset and the IME bottom inset.
     * Finally it returns [WindowInsetsCompat.CONSUMED] to the caller (so that the window insets
     * will not keep passing down to descendant views).
     *
     * If our [Bundle] parameter [savedInstanceState] is `null` this is the first time
     * we are being called so we need to create and add an [InterpolatorFragment] to our UI
     * (if it is not `null` we are being called after a configuration change and the system
     * will take care of our [InterpolatorFragment] for us). To add the [InterpolatorFragment] the
     * first time we initialize our [FragmentTransaction] variable `val transaction` by using the
     * FragmentManager for interacting with fragments associated with this activity to begin a new
     * transaction. We initialize our [InterpolatorFragment] variable `val fragment` with a new
     * instance, call the `replace` method of `transaction` to replace the [Fragment] in the
     * container with resource ID `R.id.sample_content_fragment` with `fragment` and then we
     * commit `transaction`.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in {[onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<LinearLayout>(R.id.sample_main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, windowInsets: WindowInsetsCompat ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = systemBars.left
                rightMargin = systemBars.right
                topMargin = systemBars.top
                bottomMargin = systemBars.bottom.coerceAtLeast(ime.bottom)
            }
            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = InterpolatorFragment()
            transaction.replace(R.id.sample_content_fragment, fragment)
            transaction.commit()
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in to the [Menu] parameter [menu]. This is only called once, the first time the options
     * menu is displayed. To update the menu every time it is displayed, see [onPrepareOptionsMenu].
     * When you add items to the menu, you can implement the Activity's [onOptionsItemSelected]
     * method to handle them there. We use a [MenuInflater] for our context to inflate our menu
     * layout file `R.menu.main` into our [Menu] parameter [menu], then return `true` so that the
     * [Menu] will be displayed.
     *
     * @param menu The options [Menu] in which you place your items.
     * @return You must return `true` for the menu to be displayed; if you return `false` it will
     * not be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * Prepare the Screen's standard options menu to be displayed. This is called right before the
     * menu is shown, every time it is shown. You can use this method to efficiently enable/disable
     * items or otherwise dynamically modify the contents. We initialize our [MenuItem] variable
     * `val logToggle` by finding the item in our [Menu] parameter [menu] with the resource ID
     * `R.id.menu_toggle_log`. We set it to visible if the [View] with ID `R.id.sample_output` in
     * our UI is a [ViewAnimator] (as it is only for the displays whose width is less than 720dp
     * which use layout/activity_main.xml as their content view, it is a `LinearLayout` for displays
     * 720dp or wider which use layout-w720dp/activity_main.xml as their content view) and to
     * invisible if it is not a [ViewAnimator]. If our [mLogShown] field is `true` we set the text
     * of `logToggle` to "Hide Log" and if it is `false` we set the text to "Show Log". Finally we
     * return the value returned by our super's implementation of `onPrepareOptionsMenu` to the
     * caller.
     *
     * @param menu The options [Menu] as last shown or first initialized by [onCreateOptionsMenu].
     * @return You must return `true` for the menu to be displayed; if you return `false` it will
     * not be shown.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val logToggle: MenuItem = menu.findItem(R.id.menu_toggle_log)
        logToggle.isVisible = findViewById<View>(R.id.sample_output) is ViewAnimator
        logToggle.setTitle(if (mLogShown) R.string.sample_hide_log else R.string.sample_show_log)
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. When the item ID of our
     * [MenuItem] parameter [item] is `R.id.menu_toggle_log` ("Show Log" or "Hide Log" item) we toggle
     * the value of our [Boolean] field [mLogShown], then we initialize our [ViewAnimator] variable
     * `val output` by finding the [View] in our UI with ID `R.id.sample_output`. If [mLogShown] is
     * `true` we set the child view in `output` that will be displayed to 1, if it is `false` we set
     * the child view in `output` that will be displayed to 0. We then call [invalidateOptionsMenu]
     * to declare that the options menu has changed, so should be recreated ([onCreateOptionsMenu]
     * will be called the next time it needs to be displayed), and return `true` to consume the
     * event. If on the other hand the item ID of [item] is not one of ours we return the value
     * returned by our super's implementation of `onOptionsItemSelected`.
     *
     * @param item The [MenuItem] that was selected.
     * @return boolean Return `false` to allow normal menu processing to proceed, `true` to consume
     * it here.
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
         * TAG used for logging.
         */
        const val TAG: String = "MainActivity"
    }
}
