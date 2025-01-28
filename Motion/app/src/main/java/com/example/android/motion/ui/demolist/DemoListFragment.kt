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

package com.example.android.motion.ui.demolist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.R
import com.example.android.motion.model.Demo
import com.example.android.motion.ui.EdgeToEdge

/**
 * This [Fragment] displays a [RecyclerView] which allows the user to select a demo.
 */
class DemoListFragment : Fragment() {

    /**
     * The view model which holds the [LiveData] wrapped [List] of [Demo] objects in its `demos`
     * field which we use as the dataset for the [DemoListAdapter] which feeds our [RecyclerView].
     */
    private val viewModel: DemoListViewModel by viewModels()

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. It is recommended to **only** inflate the layout in this
     * method and move logic that operates on the returned [View] to [onViewCreated].
     *
     * We return the [View] which our [LayoutInflater] parameter [inflater] inflates from our layout
     * file `R.layout.demo_list_fragment` when it uses our [ViewGroup] parameter [container] for the
     * `LayoutParams` without attaching to it.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent [View] that the fragment's
     * UI will be attached to. The fragment should not add the view itself, but this
     * can be used to generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.demo_list_fragment, container, false)
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the [View]. We initialize our [RecyclerView] variable `val demoList` by
     * finding the [View] in our [View] parameter [view] with ID `R.id.demo_list`, and then call
     * the [EdgeToEdge.setUpScrollingContent] to have it set up the scrolling content of `demoList`
     * for edge-to-edge display (if the SDK version of the software currently running on the device
     * is 21 or greater). We initialize our [DemoListAdapter] variable `val adapter` to an instance
     * whose `onDemoSelected` field is a lambda which calls `startActivity` to start the [Intent]
     * returned by the `toIntent` method of the [Demo] item which was clicked, then set the adapter
     * of `demoList` to `adapter`. We set an observer on the `demos` [LiveData] wrapped [List] of
     * [Demo] objects dataset of our [DemoListViewModel] field [viewModel] whose lambda calls the
     * `submitList` method of `adapter` with that [List] to submit it to be diffed, and displayed.
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val demoList: RecyclerView = view.findViewById(R.id.demo_list)
        EdgeToEdge.setUpScrollingContent(demoList)

        val adapter = DemoListAdapter { demo ->
            requireActivity().startActivity(demo.toIntent())
        }
        demoList.adapter = adapter
        viewModel.demos.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }
}
