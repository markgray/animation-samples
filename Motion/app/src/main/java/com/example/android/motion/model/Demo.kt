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

package com.example.android.motion.model

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import com.example.android.motion.ui.demolist.DemoListAdapter

/**
 * The class used to hold information about each of the demos available to the user.
 *
 * @param packageName name of the `Activity` that the [Demo] is in, it is used by our [toIntent]
 * method to create an [Intent] that will launch  the activity. [toIntent] is called by the lambda
 * passed to the constructor of [DemoListAdapter] which is called when the item holding our [Demo]
 * is clicked. It is specified in our `Activity` element by the "android:name" attribute and is the
 * `Activity` Class in our app that needs to be started to run the demonstration.
 * @param name Public name, the "android:name" attribute in the [ActivityInfo] our info is in.
 * @param label the current textual label associated with the [ActivityInfo] our info is in (which
 * is specified for our `Activity` element by the "android:label" attribute in AndroidManifest.xml).
 * @param description is the [String] (if any) specified by a [Demo.META_DATA_DESCRIPTION] `meta-data`
 * element for our `Activity` element in AndroidManifest.xml
 * @param apis is a list of strings naming the different API that are used (if any). The
 * [Demo.META_DATA_APIS] `meta-data` element for our `Activity` element in AndroidManifest.xml will
 * have a resource ID for a string array to be read from our `Resources` which will fill this, or
 * if it lacks one an [emptyList] will be used.
 */
data class Demo(
    val packageName: String,
    val name: String,
    val label: String,
    val description: String?,
    val apis: List<String>
) {

    companion object {
        /**
         * The "android:name" of the `category` element of all the `intent-filter` elements of the
         * `activity` elements for the demonstration activities our app provides in its file
         * AndroidManifest.xml
         */
        const val CATEGORY = "com.example.android.motion.intent.category.DEMO"

        /**
         * The "android:name" of the `meta-data` element in the `activity` element that contains
         * a description string resource as its "android:value".
         */
        const val META_DATA_DESCRIPTION = "com.example.android.motion.demo.DESCRIPTION"
        /**
         * The "android:name" of the `meta-data` element in the `activity` element that contains
         * a string array resource ID as its "android:value", with those strings describing the
         * different API that the demonstration activity uses.
         */
        const val META_DATA_APIS = "com.example.android.motion.demo.APIS"
    }

    /**
     * Creates an [Intent] which will launch the activity associated with this [Demo] instance.
     */
    fun toIntent() = Intent(Intent.ACTION_MAIN)
        .addCategory(CATEGORY)
        .setComponent(ComponentName(packageName, name))
}
