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
package com.example.android.revealeffectbasic

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ViewAnimator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.android.common.activities.SampleActivityBase
import androidx.activity.enableEdgeToEdge
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
     * then we set our content view to our layout file [R.layout.activity_main]. On devices narrower
     * than 720dp our layout file consists of a vertical `LinearLayout` root holding a [ViewAnimator]
     * (a `FrameLayout` container that will perform animations when switching between its views) which
     * holds a `ScrollView` holding a `TextView` describing our demo and a `fragment` which can hold
     * a [LogFragment] displaying our log (these two are toggled by the [R.id.menu_toggle_log] menu
     * item in our [Menu]). Below this [ViewAnimator] is a separator and a `FrameLayout` with ID
     * [R.id.sample_content_fragment] which is used to hold our [RevealEffectBasicFragment]. On
     * devices with a width of 720dp or greater the layout file consists of a horizontal `LinearLayout`
     * root holding a vertical `LinearLayout` which holding a `TextView` describing our demo, followed
     * by a separator and a `fragment` which holds a [LogFragment] displaying our log. To the right
     * of this section in the horizontal `LinearLayout` is a separator and a `FrameLayout` with ID
     * [R.id.sample_content_fragment] which is used to hold our [RevealEffectBasicFragment].
     *
     * Having set our content view we check if [savedInstanceState] is `null` and if it is we have
     * just been launched so we need to create and add a [RevealEffectBasicFragment] to our UI (if
     * it is not `null` the OS will have taken care to restore the old [RevealEffectBasicFragment]).
     * To do this we initialize our [FragmentTransaction] variable `val transaction` by using the
     * [FragmentManager] for interacting with fragments associated with this activity to begin a
     * new [FragmentTransaction], initialize our variable `val fragment` with a new instance of
     * [RevealEffectBasicFragment], use `transaction` to add `fragment` to the container with ID
     * [R.id.sample_content_fragment] and then we `commit` the `transaction`.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * *_Note: Otherwise it is null._* We just use it to determine if we need to create and add
     * a [RevealEffectBasicFragment] fragment ([savedInstanceState] is `null`) or if we are being
     * re-created after a configuration change in which case the OS will have restored our old
     * fragment.
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
            val fragment = RevealEffectBasicFragment()
            transaction.replace(R.id.sample_content_fragment, fragment)
            transaction.commit()
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in to [menu]. This is only called once, the first time the options menu is displayed.
     * To update the menu every time it is displayed, see [onPrepareOptionsMenu]. We use a
     * [MenuInflater] for this context to inflate our menu layout file [R.menu.main] into our [Menu]
     * parameter [menu], then return `true` so that our menu will be displayed.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return `true` for the menu to be displayed, if you return `false` it will
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
     * `val logToggle` by finding the item with ID [R.id.menu_toggle_log], set it to visible if
     * the view in our UI with [R.id.sample_output] is a [ViewAnimator] (narrow layout file), and
     * set its title to [R.string.sample_hide_log] ("Hide Log") if [mLogShown] is `true` or to
     * [R.string.sample_show_log] ("Show Log") if it is `false`. Finally we return the value that
     * is returned by our super's implementation of `onPrepareOptionsMenu` to the caller.
     *
     * @param menu The options menu as last shown or first initialized by [onCreateOptionsMenu].
     * @return You must return `true` for the menu to be displayed, if you return `false` it will
     * not be shown.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val logToggle: MenuItem = menu.findItem(R.id.menu_toggle_log)
        logToggle.isVisible = findViewById<View>(R.id.sample_output) is ViewAnimator
        logToggle.setTitle(if (mLogShown) R.string.sample_hide_log else R.string.sample_show_log)
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. When the `itemId` of
     * our [MenuItem] parameter [item] is [R.id.menu_toggle_log] we toggle the value of [mLogShown],
     * then initialize our [ViewAnimator] variable `val output` by finding the view in our UI with
     * ID [R.id.sample_output]. If [mLogShown] is now `true` we have `output` display child view 1
     * (the `fragment` containing the `LogFragment`), and if it is `false` we have it display child
     * view 0 (the `TextView` describing this demo). We then call [invalidateOptionsMenu] to declare
     * that the options menu has changed, so should be recreated ([onCreateOptionsMenu] will be
     * called the next time it needs to be displayed).
     *
     * @param item The menu item that was selected.
     * @return [Boolean] Return `false` to allow normal menu processing to proceed, `true` to
     * consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_toggle_log -> {
                mLogShown = !mLogShown
                val output: ViewAnimator = findViewById(R.id.sample_output)
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