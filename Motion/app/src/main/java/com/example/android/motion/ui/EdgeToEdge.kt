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

package com.example.android.motion.ui

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import com.example.android.motion.R
import com.google.android.material.appbar.AppBarLayout

/**
 * A utility for edge-to-edge display. It provides several features needed to make the app
 * displayed edge-to-edge on Android Q with gestural navigation. We use a `when` statement
 * to choose between different implementations of our [EdgeToEdgeImpl] interface based on
 * the SDK version of the software currently running on the hardware device we are running on:
 *  - when the SDK is less than 21 we use [EdgeToEdgeBase] which overrides nothing in the
 *  [EdgeToEdgeImpl] interface, and as a result the [EdgeToEdge] methods are no-ops.
 *  - when the SDK is less than 30 but greater than or equal to 21 we use [EdgeToEdgeApi21] which
 *  overrides all three methods of [EdgeToEdgeImpl] with api's introduced with SDK 21, but
 *  deprecated in SDK 30.
 *  - when the SDK is greater than or equal to 30 we use [EdgeToEdgeApi30] which overrides all
 *  three methods of [EdgeToEdgeImpl] with api's introduced with SDK 30 (or will eventually)
 */
@SuppressLint("ObsoleteSdkInt")
object EdgeToEdge: EdgeToEdgeImpl by when {
    Build.VERSION.SDK_INT >= 30 -> EdgeToEdgeApi30()
    Build.VERSION.SDK_INT >= 21 -> EdgeToEdgeApi21()
    else -> EdgeToEdgeBase()
}

private interface EdgeToEdgeImpl {

    /**
     * Configures a root view of an Activity for edge-to-edge display.
     *
     * @param root A root view of an Activity.
     */
    fun setUpRoot(root: ViewGroup) {}

    /**
     * Configures an app bar and a toolbar for edge-to-edge display.
     *
     * @param appBar An [AppBarLayout].
     * @param toolbar A [Toolbar] in the [appBar].
     */
    fun setUpAppBar(appBar: AppBarLayout, toolbar: Toolbar) {}

    /**
     * Configures a scrolling content for edge-to-edge display.
     *
     * @param scrollingContent A scrolling [ViewGroup]. This is typically a `RecyclerView` or a
     * `ScrollView`. It should be as wide as the screen, and should touch the bottom edge of
     * the screen.
     */
    fun setUpScrollingContent(scrollingContent: ViewGroup) {}
}

/**
 * This is the super class of [EdgeToEdge] that is used for SDK less than 21. Its super is just the
 * naked [EdgeToEdgeImpl] interface with all of the methods left as no-ops.
 */
private class EdgeToEdgeBase : EdgeToEdgeImpl

/**
 * This is the super class of [EdgeToEdge] that is used for SDK greater than or equal to 21 but less
 * than 30. It overrides all three methods of its [EdgeToEdgeImpl] super using api's introduced with
 * SDK 21.
 */
@RequiresApi(21)
private class EdgeToEdgeApi21 : EdgeToEdgeImpl {

    /**
     * Configures a root view of an Activity for edge-to-edge display. We use the method
     * [View.setSystemUiVisibility] (aka kotlin property `systemUiVisibility`) of our [ViewGroup]
     * parameter [root] with the flag [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] (requests that
     * the system navigation be temporarily hidden) or'ed with the flag [View.SYSTEM_UI_FLAG_LAYOUT_STABLE]
     * (we would like a stable view of the content insets, so do not relayout our view when the user
     * touches the screen to un-hide the system navigation).
     *
     * @param root A root view of an Activity.
     */
    override fun setUpRoot(root: ViewGroup) {
        @Suppress("DEPRECATION")
        root.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    /**
     * Configures an app bar and a toolbar for edge-to-edge display. First we initialize our variable
     * `val toolbarPadding` to the pixel value of the dimension stored as [R.dimen.spacing_medium] in
     * the resources associated with our [Toolbar] parameter [toolbar] (16dp). The we set the
     * [View.OnApplyWindowInsetsListener] of [AppBarLayout] parameter [appBar] to have a lambda take
     * over the policy for applying window insets to this view. This lambda will update the padding
     * of [appBar] to have its `top` padding be the top system window inset in pixels, and update
     * the `left` padding of [toolbar] to add `toolbarPadding` to the left system window inset in
     * pixels, and the `right` padding to be the right system window inset in pixels.
     *
     * @param appBar An [AppBarLayout].
     * @param toolbar A [Toolbar] in the [appBar].
     */
    override fun setUpAppBar(appBar: AppBarLayout, toolbar: Toolbar) {
        val toolbarPadding = toolbar.resources.getDimensionPixelSize(R.dimen.spacing_medium)
        appBar.setOnApplyWindowInsetsListener { _, windowInsets ->
            @Suppress("DEPRECATION")
            appBar.updatePadding(top = windowInsets.systemWindowInsetTop)
            @Suppress("DEPRECATION")
            toolbar.updatePadding(
                left = toolbarPadding + windowInsets.systemWindowInsetLeft,
                right = windowInsets.systemWindowInsetRight
            )
            windowInsets
        }
    }

    /**
     * Configures a scrolling content for edge-to-edge display. We initialize our [Int] variables
     * `val originalPaddingLeft` to the left padding of our [ViewGroup] parameter [scrollingContent],
     * `val originalPaddingRight` to its right padding, and `val originalPaddingBottom` to its bottom
     * padding (all in pixels). Then we set the [View.OnApplyWindowInsetsListener] of our parameter
     * [scrollingContent] to a lambda which updates the view's padding by adding the left system
     * window inset to `originalPaddingLeft` to set its `left` padding, adding the right system
     * window inset to `originalPaddingRight` to set its `right` padding, and adding the bottom
     * system window inset to `originalPaddingBottom` to set its `bottom` padding. The lambda then
     * returns its [WindowInsets] argument `windowInsets` to its caller.
     *
     * @param scrollingContent A scrolling [ViewGroup]. This is typically a `RecyclerView` or a
     * `ScrollView`. It should be as wide as the screen, and should touch the bottom edge of
     * the screen.
     */
    override fun setUpScrollingContent(scrollingContent: ViewGroup) {
        val originalPaddingLeft = scrollingContent.paddingLeft
        val originalPaddingRight = scrollingContent.paddingRight
        val originalPaddingBottom = scrollingContent.paddingBottom
        scrollingContent.setOnApplyWindowInsetsListener { _, windowInsets ->
            @Suppress("DEPRECATION")
            scrollingContent.updatePadding(
                left = originalPaddingLeft + windowInsets.systemWindowInsetLeft,
                right = originalPaddingRight + windowInsets.systemWindowInsetRight,
                bottom = originalPaddingBottom + windowInsets.systemWindowInsetBottom
            )
            windowInsets
        }
    }
}

/**
 * This is the super class of [EdgeToEdge] that is used for SDK greater than or equal to 30. It will
 * eventually override all three methods of its [EdgeToEdgeImpl] super using api's introduced with
 * SDK 30.
 * TODO: Update to latest methods and constants for API 30
 */
@RequiresApi(30)
private class EdgeToEdgeApi30 : EdgeToEdgeImpl {
    /**
     * Configures a root view of an Activity for edge-to-edge display. We initialize our
     * [WindowInsetsController] variable `val controller` to the single [WindowInsetsController] of
     * the window [ViewGroup] parameter [root] is attached to. We then call the [WindowInsetsController.hide]
     * method of `controller` to have it hide any system bars for navigation. (Note: this is the
     * only one of the three methods of [EdgeToEdgeImpl] that is actually implemented using SDK 30
     * api's.
     *
     * @param root A root view of an Activity.
     */
    override fun setUpRoot(root: ViewGroup) {
        val controller: WindowInsetsController? = root.windowInsetsController
        controller?.hide(WindowInsets.Type.navigationBars())
    }

    /**
     * Configures an app bar and a toolbar for edge-to-edge display. First we initialize our variable
     * `val toolbarPadding` to the pixel value of the dimension stored as [R.dimen.spacing_medium] in
     * the resources associated with our [Toolbar] parameter [toolbar] (16dp). The we set the
     * [View.OnApplyWindowInsetsListener] of [AppBarLayout] parameter [appBar] to have a lambda take
     * over the policy for applying window insets to this view. This lambda will update the padding
     * of [appBar] to have its `top` padding be the top system window inset in pixels, and update
     * the `left` padding of [toolbar] to add `toolbarPadding` to the left system window inset in
     * pixels, and the `right` padding to be the right system window inset in pixels.
     * TODO: Solve DEPRECATION warnings (yawn).
     *
     * @param appBar An [AppBarLayout].
     * @param toolbar A [Toolbar] in the [appBar].
     */
    override fun setUpAppBar(appBar: AppBarLayout, toolbar: Toolbar) {
        val toolbarPadding = toolbar.resources.getDimensionPixelSize(R.dimen.spacing_medium)
        appBar.setOnApplyWindowInsetsListener { _, windowInsets ->
            @Suppress("DEPRECATION")
            appBar.updatePadding(top = windowInsets.systemWindowInsetTop)
            @Suppress("DEPRECATION")
            toolbar.updatePadding(
                left = toolbarPadding + windowInsets.systemWindowInsetLeft,
                right = windowInsets.systemWindowInsetRight
            )
            windowInsets
        }
    }

    /**
     * Configures a scrolling content for edge-to-edge display. We initialize our [Int] variables
     * `val originalPaddingLeft` to the left padding of our [ViewGroup] parameter [scrollingContent],
     * `val originalPaddingRight` to its right padding, and `val originalPaddingBottom` to its bottom
     * padding (all in pixels). Then we set the [View.OnApplyWindowInsetsListener] of our parameter
     * [scrollingContent] to a lambda which updates the view's padding by adding the left system
     * window inset to `originalPaddingLeft` to set its `left` padding, adding the right system
     * window inset to `originalPaddingRight` to set its `right` padding, and adding the bottom
     * system window inset to `originalPaddingBottom` to set its `bottom` padding. The lambda then
     * returns its [WindowInsets] argument `windowInsets` to its caller.
     * TODO: Solve DEPRECATION warnings (yawn).
     *
     * @param scrollingContent A scrolling [ViewGroup]. This is typically a `RecyclerView` or a
     * `ScrollView`. It should be as wide as the screen, and should touch the bottom edge of
     * the screen.
     */
    override fun setUpScrollingContent(scrollingContent: ViewGroup) {
        val originalPaddingLeft = scrollingContent.paddingLeft
        val originalPaddingRight = scrollingContent.paddingRight
        val originalPaddingBottom = scrollingContent.paddingBottom
        scrollingContent.setOnApplyWindowInsetsListener { _, windowInsets ->
            @Suppress("DEPRECATION")
            scrollingContent.updatePadding(
                left = originalPaddingLeft + windowInsets.systemWindowInsetLeft,
                right = originalPaddingRight + windowInsets.systemWindowInsetRight,
                bottom = originalPaddingBottom + windowInsets.systemWindowInsetBottom
            )
            windowInsets
        }
    }
}

