/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.unsplash.data

import com.example.android.unsplash.MainActivity
import com.example.android.unsplash.data.model.Photo
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.http.GET

/**
 * Modeling the unsplash.it API.
 */
interface UnsplashService {
    /**
     * The `@GET` annotation will make a "GET" request to the REST path "/list" relative to the base
     * URL. The base URL is set to [ENDPOINT] by a call to the `setEndpoint` method of the
     * [RestAdapter.Builder] used in the `displayData` method of [MainActivity] so the `GET` call
     * will download the JSON file it finds at "https://unsplash.it/list", parse it into a [List]
     * of [Photo] objects which it will feed to the [Callback] parameter [callback] of the [getFeed]
     * method.
     *
     * @param callback the [Callback] which this method will use to return the results of the `GET`
     * request, either a [List] of [Photo] objects parsed from the JSON at "https://unsplash.it/list"
     * to the [Callback.success] override of [callback], or the [RetrofitError] if one occurs to the
     * [Callback.failure] override of [callback].
     */
    @GET("/list")
    fun getFeed(callback: Callback<List<Photo?>>)

    companion object {
        /**
         * The base URL for the data this app downloads, used by a call to the `setEndpoint` method
         * of the [RestAdapter.Builder] used in the `displayData` method of [MainActivity] to create
         * the instance of [UnsplashService] it uses to download its dataset of [Photo] objects.
         */
        const val ENDPOINT: String = "https://picsum.photos"
    }
}