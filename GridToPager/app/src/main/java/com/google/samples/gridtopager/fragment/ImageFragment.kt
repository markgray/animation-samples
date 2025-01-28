/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.gridtopager.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.samples.gridtopager.R

/**
 * A fragment for displaying an image.
 */
class ImageFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. We initialize our [View]
     * variable `val view` by using our [LayoutInflater] parameter [inflater] to inflate our layout
     * file `R.layout.fragment_image` with our [ViewGroup] parameter [container] supplying the
     * `LayoutParams` (the [View] consists of a single [ImageView] with ID `R.id.image`). We
     * initialize our [Bundle] variable `val arguments` to the arguments supplied when the fragment
     * was instantiated. We initialize our variable `val imageRes` to the [Int] stored under the key
     * [KEY_IMAGE_RES] in `arguments`. We set the name of the View to be used to identify Views in
     * Transitions of the [View] in `view` with ID `R.id.image` to be the string value of the image
     * resource value `imageRes` (name of the View to uniquely identify it for Transitions, which is
     * the same name used when binding views in the grid).
     *
     * We then begin a load of `imageRes` with [Glide] that will be tied to the given Fragment's
     * lifecycle and that uses `this` Fragment's default options. We set the [RequestListener] of
     * this load to an anonymous instance whose `onLoadFailed` override calls the method
     * [startPostponedEnterTransition] of the parent Fragment containing this Fragment to get the
     * transition going in case of a failure and returns `false` to to allow `onLoadFailed`` to be
     * called on the target [Drawable], and whose `onResourceReady` override also calls the method
     * [startPostponedEnterTransition] of the parent Fragment containing this Fragment to get the
     * transition going in case of a failure and returns `false` to to allow `onLoadFailed`` to be
     * called on the target [Drawable]. The final method called in the [Glide] is `into` which
     * will set the [ImageView] that the resource will be loaded into to be the [View] in `view`
     * with ID `R.id.image`.
     *
     * Finally we return `view` to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or `null`.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        val arguments: Bundle? = arguments

        @DrawableRes
        val imageRes = (arguments ?: return null).getInt(KEY_IMAGE_RES)

        // Just like we do when binding views at the grid, we set the transition name to be the string
        // value of the image res.
        view.findViewById<View>(R.id.image).transitionName = imageRes.toString()

        // Load the image with Glide to prevent OOM error when the image drawables are very large.
        Glide.with(this)
            .load(imageRes)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                    // startPostponedEnterTransition() should also be called on it to get the transition
                    // going in case of a failure.
                    parentFragment!!.startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                    // startPostponedEnterTransition() should also be called on it to get the transition
                    // going when the image is ready.
                    parentFragment!!.startPostponedEnterTransition()
                    return false
                }
            })
            .into((view.findViewById<View>(R.id.image) as ImageView))
        return view
    }

    companion object {
        /**
         * The key to the value of the image resource ID which is passed us in our argument [Bundle].
         */
        private const val KEY_IMAGE_RES = "com.google.samples.gridtopager.key.imageRes"

        /**
         * Factory method used by the `getItem` method of `ImagePagerAdapter` to create a new instance
         * of [ImageFragment] to fetch and display the `Drawable` with resource ID [drawableRes]. We
         * initialize our [ImageFragment] variable `val fragment` with a new instance and our [Bundle]
         * variable `val argument` with a new instance. We store our parameter [drawableRes] under the
         * key [KEY_IMAGE_RES] in `argument` and set the construction arguments of `fragment` to
         * `argument`. Finally we return `fragment` to the caller.
         *
         * @param drawableRes the resource ID of the `Drawable` which the new [ImageFragment] should
         * display.
         * @return a new instance of [ImageFragment] whose construction arguments [Bundle] contains
         * our parameter [drawableRes] stored under the key [KEY_IMAGE_RES].
         */
        @JvmStatic
        fun newInstance(@DrawableRes drawableRes: Int): ImageFragment {
            val fragment = ImageFragment()
            val argument = Bundle()
            argument.putInt(KEY_IMAGE_RES, drawableRes)
            fragment.arguments = argument
            return fragment
        }
    }
}
