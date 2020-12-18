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

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.motion.model.Demo

/**
 * The Application context aware `ViewModel` used by [DemoListFragment].
 *
 * @param application our activity's [Application] instance.
 */
class DemoListViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The [MutableLiveData] wrapped [List] of [Demo] objects corresponding to each of the sample
     * activities that the user can select using the `RecyclerView` displayed by [DemoListFragment].
     * It is filled using a [PackageManager] instance to find global package information for all
     * of the activities with an `ACTION_MAIN` action, and a [Demo.CATEGORY] category in its
     * intent-filter. These are to be found in our AndroidManifest.xml file. This is private to
     * avoid exposing a way to set this value to observers. Public read-only access is provided by
     * our property [demos].
     */
    private val _demos = MutableLiveData<List<Demo>>()

    /**
     * Public read-only access to our [MutableLiveData] wrapped [List] of [Demo] objects field
     * [_demos]. An observer is added to it in the `onViewCreated` override of [DemoListFragment]
     * whose lambda calls the `submitList` method of its [DemoListAdapter] to submit [demos] to be
     * diffed, and displayed whenever it changes value.
     */
    val demos: LiveData<List<Demo>> = _demos

    init {
        /**
         * The [PackageManager] instance we use to find global package information.
         */
        val packageManager: PackageManager = getApplication<Application>().packageManager

        /**
         * The list of [ResolveInfo] objects returned when we use `packageManager` to retrieve all
         * activities whose `intent-filter` has an `action` [Intent] of `ACTION_MAIN` and `category`
         * of [Demo.CATEGORY] including the `meta-data` Bundles that are associated with them.
         */
        val resolveInfoList: MutableList<ResolveInfo> = packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Demo.CATEGORY),
            PackageManager.GET_META_DATA
        )

        /**
         * [Resources] instance for the application's package.
         */
        val resources: Resources = application.resources

        /**
         * Here we fill our `MutableLiveData` wrapped `List` of `Demo` objects field `_demos` with
         * new instances of `Demo` constructed using values parsed from each of the `ResolveInfo`
         * objects in our list `resolveInfoList`.
         */
        _demos.value = resolveInfoList.map { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo
            val metaData = activityInfo.metaData
            val apisId = metaData?.getInt(Demo.META_DATA_APIS, 0) ?: 0
            Demo(
                activityInfo.applicationInfo.packageName,
                activityInfo.name,
                activityInfo.loadLabel(packageManager).toString(),
                metaData?.getString(Demo.META_DATA_DESCRIPTION),
                if (apisId == 0) emptyList() else resources.getStringArray(apisId).toList()
            )
        }
    }
}
