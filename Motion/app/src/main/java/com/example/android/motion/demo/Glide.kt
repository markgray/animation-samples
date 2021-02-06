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

package com.example.android.motion.demo

import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * This file just contains the `RequestBuilder.doOnEnd` extension function, which is used in the
 * `CheeseDetailFragment` and `CheeseGridAdapter` classes of the `SharedElementActivity` demo.
 */

/**
 * Executes the specified [body] when the request is complete. It is invoked no matter whether the
 * request succeeds or fails. We call the [RequestBuilder.addListener] method of our receiver to
 * have it add an anonymous [RequestListener] whose `onLoadFailed` and `onResourceReady` overrides
 * both execute our [body] parameter and return to the caller the modified [RequestBuilder] that the
 * method returns to us.
 *
 * @param body a lambda to be executed when the [Glide] request that our [RequestBuilder] receiver
 * is building completes whether the request succeeds or fails.
 * @return the same [RequestBuilder] we were invoked on which [RequestBuilder.addListener] returns
 * in order to allow chaining.
 */
fun <T> RequestBuilder<T>.doOnEnd(body: () -> Unit): RequestBuilder<T> {
    return addListener(object : RequestListener<T> {
        /**
         * Called when an exception occurs during a load, immediately before [Target.onLoadFailed].
         * Will only be called if we currently want to display an image for the given model in the
         * given [target]. We just execute the [body] lambda parameter passed to [doOnEnd] and
         * return `false` to allow [Target.onLoadFailed] to be called on [target].
         *
         * @param e The maybe `null` exception containing information about why the request failed.
         * @param model The model we were trying to load when the exception occurred.
         * @param target The [Target] we were trying to load the image into.
         * @param isFirstResource `true` if this exception is for the first resource to load.
         * @return `true` to prevent [Target.onLoadFailed] from being called on [target], typically
         * because the listener wants to update the [target] or the object the [target] wraps itself
         * or `false` to allow [Target.onLoadFailed] to be called on [target].
         */
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }

        /**
         * Called when a load completes successfully, immediately before [Target.onResourceReady]
         * We just execute the [body] lambda parameter passed to [doOnEnd] and return `false` to
         * allow [Target.onResourceReady] to be called on [target].
         *
         * @param resource The resource that was loaded for the target.
         * @param model The specific model that was used to load the image.
         * @param target The [Target] the model was loaded into.
         * @param dataSource The [DataSource] the resource was loaded from.
         * @param isFirstResource `true` if this is the first resource in this load to be loaded
         * into the target. For example when loading a thumbnail and a full-sized image, this will
         * be `true` for the first image to load and `false` for the second.
         * @return `true` to prevent [Target.onResourceReady] from being called on [target],
         * typically because the listener wants to update the [Target] or the object the [Target]
         * wraps itself or `false` to allow [Target.onResourceReady] to be called on [target].
         */
        override fun onResourceReady(
            resource: T,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }
    })
}
