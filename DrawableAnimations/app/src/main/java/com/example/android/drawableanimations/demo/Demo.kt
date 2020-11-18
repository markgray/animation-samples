/*
 * Copyright 2020 Google LLC
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

package com.example.android.drawableanimations.demo

import androidx.fragment.app.Fragment

/**
 * This is the datum type used as the dataset of the `DemoListAdapter` for the `RecyclerView` in the
 * `HomeFragment` fragment UI which allows the user to select between the demo fragments loaded.
 *
 * @param title The title to be displayed for this choice in the `RecyclerView` of the `HomeFragment`
 * fragment UI, as well as the title associated with the activity once the demo fragment is loaded.
 * @param createFragment a function which takes no arguments and returns a [Fragment], in our case
 * the constructors for the fragments `AnimatedFragment` and `SeekableFragment` are used in the
 * [Demo] instances that load these fragments.
 */
data class Demo(
    val title: String,
    val createFragment: () -> Fragment
)
