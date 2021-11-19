/*
 * Copyright (C) 2013 The Android Open Source Project
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
package com.example.android.basictransition

import android.os.Bundle
import android.transition.Scene
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment

/**
 * Our demo [Fragment]. The UI shows radioboxes to select between different Scenes,
 * and uses various ways to transition between them.
 */
class BasicTransitionFragment : Fragment(), RadioGroup.OnCheckedChangeListener {

    // We transition between these Scenes
    /**
     * [Scene] instantiated from a live view hierarchy retrieved from view with ID R.id.container
     */
    private lateinit var mScene1: Scene

    /**
     * [Scene] inflated from the layout resource file [R.layout.scene2]
     */
    private var mScene2: Scene? = null
    /**
     * [Scene] inflated from the layout resource file [R.layout.scene3]
     */
    private lateinit var mScene3: Scene

    /**
     * A custom [TransitionManager]
     */
    private lateinit var mTransitionManagerForScene3: TransitionManager

    /**
     * Transitions take place in this ViewGroup. We retain this for the dynamic transition on scene 4.
     */
    private lateinit var mSceneRoot: ViewGroup

    /**
     * Called to have the fragment instantiate its user interface view. First we initialize our [View]
     * variable `val view` by using our [LayoutInflater] parameter [inflater] in flate our layout file
     * [R.layout.fragment_basic_transition], using our [ViewGroup] parameter [container] for its
     * LayoutParams without attaching to it. We initialize our [RadioGroup] variable `val radioGroup`
     * by finding the [View] in `view` with ID [R.id.select_scene] and set its `OnCheckedChangeListener`
     * to `this`. We initialize our [ViewGroup] field [mSceneRoot] by finding the [View] in `view` with
     * ID [R.id.select_scene]. We then initialize our [Scene] field [mScene1] with an instance which
     * when entered, will remove any children from the [mSceneRoot] container and add the view in
     * [mSceneRoot] with ID [R.id.container] as a new child of [mSceneRoot]. We initialize our [Scene]
     * field [mScene2] with an instance described by the resource file [R.layout.scene2] with
     * [mSceneRoot] as root of the hierarchy in which scene changes and transitions will take place,
     * and our [Scene] field [mScene3] with an instance described by the resource file [R.layout.scene3]
     * with [mSceneRoot] as root of the hierarchy in which scene changes and transitions will take
     * place. We initialize our [TransitionManager] field [mTransitionManagerForScene3] with a custom
     * TransitionManager for Scene 3 inflated from the file [R.transition.scene3_transition_manager]
     * in which ChangeBounds and Fade take place at the same time. Finally we return `view` to our
     * caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI will be attached to.
     * The fragment should not add the view itself, but this can be used to generate the LayoutParams
     * of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
                R.layout.fragment_basic_transition,
                container,
                false
        )!!
        val radioGroup = view.findViewById<View>(R.id.select_scene) as RadioGroup
        radioGroup.setOnCheckedChangeListener(this)
        mSceneRoot = view.findViewById<View>(R.id.scene_root) as ViewGroup

        // BEGIN_INCLUDE(instantiation_from_view)
        // A Scene can be instantiated from a live view hierarchy.
        mScene1 = Scene(mSceneRoot, mSceneRoot.findViewById<View>(R.id.container))
        // END_INCLUDE(instantiation_from_view)

        // BEGIN_INCLUDE(instantiation_from_resource)
        // You can also inflate a generate a Scene from a layout resource file.
        mScene2 = Scene.getSceneForLayout(mSceneRoot, R.layout.scene2, activity)
        // END_INCLUDE(instantiation_from_resource)

        // Another scene from a layout resource file.
        mScene3 = Scene.getSceneForLayout(mSceneRoot, R.layout.scene3, activity)

        // BEGIN_INCLUDE(custom_transition_manager)
        // We create a custom TransitionManager for Scene 3, in which ChangeBounds and Fade
        // take place at the same time.
        mTransitionManagerForScene3 = TransitionInflater.from(activity)
                .inflateTransitionManager(R.transition.scene3_transition_manager, mSceneRoot)
        // END_INCLUDE(custom_transition_manager)
        return view
    }

    /**
     * Called when the checked radio button has changed. When the selection is cleared, [checkedId]
     * is -1. We switch on the value of our parameter [checkedId]:
     *  - [R.id.select_scene_1] we use the [TransitionManager.go] method to start an automatic
     *  transition to [Scene] field [mScene1] using the default transition for TransitionManager.
     *  - [R.id.select_scene_2] we use the [TransitionManager.go] method to start an automatic
     *  transition to [Scene] field [mScene2] using the default transition for TransitionManager.
     *  - [R.id.select_scene_3] we use our custom [TransitionManager] field [mTransitionManagerForScene3]
     *  to change to [Scene] field [mScene3], using the appropriate transition for this particular
     *  scene change.
     *  - [R.id.select_scene_4] we dynamically invoke a transition without a [Scene]. To do this we
     *  first call the [TransitionManager.beginDelayedTransition] using [mSceneRoot] as the root of
     *  the View hierarchy to run the transition on. and then we just change view properties of the
     *  view with ID [R.id.transition_square] to change its size to 100dp by 100dp.
     *
     * @param group the [RadioGroup] in which the checked radio button has changed
     * @param checkedId the unique identifier of the newly checked radio button
     */
    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.select_scene_1 -> {

                // BEGIN_INCLUDE(transition_simple)
                // You can start an automatic transition with TransitionManager.go().
                TransitionManager.go(mScene1)
            }
            R.id.select_scene_2 -> {
                TransitionManager.go(mScene2)
            }
            R.id.select_scene_3 -> {

                // BEGIN_INCLUDE(transition_custom)
                // You can also start a transition with a custom TransitionManager.
                mTransitionManagerForScene3.transitionTo(mScene3)
            }
            R.id.select_scene_4 -> {

                // BEGIN_INCLUDE(transition_dynamic)
                // Alternatively, transition can be invoked dynamically without a Scene.
                // For this, we first call TransitionManager.beginDelayedTransition().
                TransitionManager.beginDelayedTransition(mSceneRoot)
                // Then, we can just change view properties as usual.
                val square = mSceneRoot.findViewById<View>(R.id.transition_square)
                val params = square.layoutParams
                val newSize = resources.getDimensionPixelSize(R.dimen.square_size_expanded)
                params.width = newSize
                params.height = newSize
                square.layoutParams = params
            }
        }
    }

    companion object {
        @Suppress("unused")
        fun newInstance(): BasicTransitionFragment {
            return BasicTransitionFragment()
        }
    }
}