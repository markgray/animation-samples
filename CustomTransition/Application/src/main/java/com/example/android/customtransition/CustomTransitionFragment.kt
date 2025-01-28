/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.customtransition

import android.content.Context
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.android.common.logger.Log.i

/**
 * The [Fragment] holding our demo.
 */
class CustomTransitionFragment : Fragment(), View.OnClickListener {
    /**
     * These are the Scenes we use.
     */
    private lateinit var mScenes: Array<Scene>

    /**
     * The current index for [mScenes].
     */
    private var mCurrentScene = 0

    /**
     * This is the custom Transition we use in this sample.
     */
    private var mTransition: Transition? = null

    /**
     * Called to have the fragment instantiate its user interface view. We just return the [View]
     * that our [LayoutInflater] parameter [inflater] returns when it inflates our layout file
     * `R.layout.fragment_custom_transition` using our [ViewGroup] parameter [container] for the
     * LayoutParams without attaching to [container].
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_transition, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created. The fragment's view hierarchy
     * is not however attached to its parent at this point. We initialize our [Context] variable
     * `val context` to the [Context] of the FragmentActivity this fragment is currently associated
     * with, and we initialize our [FrameLayout] variable `val container` to the [FrameLayout] in
     * our [View] parameter [view] with ID `R.id.container`. We find the [View] in [view] with ID
     * `R.id.show_next_scene` (the "Show next scene" `Button`) and set its `OnClickListener` to
     * `this`. If our [Bundle] parameter [savedInstanceState] is not `null` we set our [Int] field
     * [mCurrentScene] to the [Int] stored in [savedInstanceState] under the key [STATE_CURRENT_SCENE].
     * We initialize each of the [Scene] entries in our [Array] field [mScenes] with instances that
     * are created by the [Scene.getSceneForLayout] method from the layout files `R.layout.scene1`,
     * `R.layout.scene2`, and [R.layout.scene3]. We initialize our custom [Transition] field
     * [mTransition] to an instance of [ChangeColor]. Finally we call the [TransitionManager.go]
     * method to have it show the initial [Scene] which is the [Scene] in [mScenes] whose index is
     * [mCurrentScene] modulo the size of [mScenes].
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context: Context? = activity
        val container = view.findViewById<View>(R.id.container) as FrameLayout
        view.findViewById<View>(R.id.show_next_scene).setOnClickListener(this)
        if (savedInstanceState != null) {
            mCurrentScene = savedInstanceState.getInt(STATE_CURRENT_SCENE)
        }
        // We set up the Scenes here.
        mScenes = arrayOf(
            Scene.getSceneForLayout(container, R.layout.scene1, context),
            Scene.getSceneForLayout(container, R.layout.scene2, context),
            Scene.getSceneForLayout(container, R.layout.scene3, context)
        )
        // This is the custom Transition.
        mTransition = ChangeColor()
        // Show the initial Scene.
        TransitionManager.go(mScenes[mCurrentScene % mScenes.size])
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be reconstructed
     * in a new instance if its process is restarted. If a new instance of the fragment later needs
     * to be created, the data you place in the Bundle here will be available in the Bundle given to
     * [onCreate], [onCreateView] and [onActivityCreated]. First we call our super's implementation
     * of [onSaveInstanceState], then we save our [Int] field [mCurrentScene] in [outState] under the
     * key [STATE_CURRENT_SCENE].
     *
     * @param outState [Bundle] in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_SCENE, mCurrentScene)
    }

    /**
     * Called when the `Button` with ID `R.id.show_next_scene` ("Show next scene") is clicked. When
     * the ID of our [View] parameter [v] is `R.id.show_next_scene` we add 1 to our [Int] field
     * [mCurrentScene] and set it to that value modulo the size of our [Array] field [mScenes].
     * We log a message announcing the transition to [mCurrentScene], then use the method
     * [TransitionManager.go] to have it use our custom [Transition] field [mTransition] to change
     * to the [Scene] at index [mCurrentScene] in our [Array] field [mScenes].
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.show_next_scene -> {
                mCurrentScene = (mCurrentScene + 1) % mScenes.size
                i(TAG, "Transitioning to scene #$mCurrentScene")
                // Pass the custom Transition as second argument for TransitionManager.go
                TransitionManager.go(mScenes[mCurrentScene], mTransition)
            }
        }
    }

    companion object {
        /**
         * The key under which we save the value of our [Int] field [mCurrentScene]
         */
        private const val STATE_CURRENT_SCENE = "current_scene"

        /**
         * Tag for the logger
         */
        private const val TAG = "CustomTransitionFragment"
    }
}
