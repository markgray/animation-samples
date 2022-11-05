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
package com.example.android.unsplash.data.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Locale

/**
 * Model class representing data returned from unsplash.it
 */
@Suppress("MemberVisibilityCanBePrivate") // I like to use kdoc [] references
open class Photo : Parcelable {
    /*{
        "format": "jpeg",
        "width": 5616,
        "height": 3744,
        "filename": "0000_yC-Yzbqy7PY.jpeg",
        "id": 0,
        "author": "Alejandro Escamilla",
        "author_url": "https://unsplash.com/alejandroescamilla",
        "post_url": "https://unsplash.com/photos/yC-Yzbqy7PY"
    }*/

    /**
     * The image format of the photo, ie. "jpeg", "gif", "png" etc.
     */
    val format: String?

    /**
     * The width of the photo in pixels.
     */
    val width: Int

    /**
     * The height of the photo in pixels.
     */
    val height: Int

    /**
     * The file name on the remote server.
     */
    val filename: String?

    /**
     * The unique ID of the image. The URL for retrieving the image is formed by our [getPhotoUrl]
     * method by formatting the [String] value of the requested width passed to the method and the
     * [String] value of our [id] into the [PHOTO_URL_BASE] format. The [id] is also used to generate
     * unique transition names for the views holding the image
     */
    @JvmField
    val id: Long

    /**
     * The name of the author of the image.
     */
    @JvmField
    val author: String?

    /**
     * The URL for the author's web page.
     */
    @Suppress("PropertyName") // Needs to be same as JSON name or annotated
    val author_url: String?

    /**
     * The URL for a particular image which allows on to download the image in different sizes or to
     * select related images from the same author.
     */
    @Suppress("PropertyName") // Needs to be same as JSON name or annotated
    val post_url: String?

    /**
     * Superfluous constructor - does not seem to be used, retrofit appears to access the code of
     * the class during runtime in order to figure out how to perform the conversion from JSON to
     * [Photo] objects.
     */
    constructor(
        format: String?,
        width: Int,
        height: Int,
        filename: String?,
        id: Long,
        author: String?,
        author_url: String?,
        post_url: String?
    ) {
        this.format = format
        this.width = width
        this.height = height
        this.filename = filename
        this.id = id
        this.author = author
        this.author_url = author_url
        this.post_url = post_url
    }

    /**
     * Constructor used to recreate an instance of [Photo] which has been stored in a [Parcel] by
     * our [writeToParcel] method.
     *
     * @param parcel a [Parcel] containing a parcelized [Photo] object.
     */
    protected constructor(parcel: Parcel) {
        format = parcel.readString()
        width = parcel.readInt()
        height = parcel.readInt()
        filename = parcel.readString()
        id = parcel.readLong()
        author = parcel.readString()
        author_url = parcel.readString()
        post_url = parcel.readString()
    }

    /**
     * Returns an URL that can be used to retrieve the image that this [Photo] object represents.
     * Our [PHOTO_URL_BASE] constant is a format string we use to encode our [requestWidth] parameter
     * followed by a query string for "image=[id]"
     *
     * @param requestWidth the requested width in pixels of the image
     */
    fun getPhotoUrl(requestWidth: Int): String {
        return String.format(Locale.getDefault(), PHOTO_URL_BASE, requestWidth, id)
    }

    /**
     * Describe the kinds of special objects contained in this [Parcelable] instance's marshaled
     * representation. For example, if the object will include a file descriptor in the output of
     * [writeToParcel], the return value of this method must include the `CONTENTS_FILE_DESCRIPTOR`
     * bit. We just return 0 as our contents contain no special objects.
     *
     * @return a bitmask indicating the set of special object types marshaled by this [Parcelable]
     * object instance.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Flatten this object in to a [Parcel]. We just use the appropriate `write*` method of our
     * [Parcel] parameter [dest] to store our fields in [dest].
     *
     * @param dest The [Parcel] in which the object should be written.
     * @param flags Additional flags about how the object should be written. May be 0 or
     * `PARCELABLE_WRITE_RETURN_VALUE`.
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(format)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeString(filename)
        dest.writeLong(id)
        dest.writeString(author)
        dest.writeString(author_url)
        dest.writeString(post_url)
    }

    companion object {
        /**
         * The format string that our [getPhotoUrl] method uses to format an URL that can be used to
         * retrieve the image associated with its [Photo] object.
         */
        private const val PHOTO_URL_BASE = "https://unsplash.it/%d?image=%d"

        /**
         * Interface that must be implemented and provided as a public [CREATOR]
         * field that generates instances of your [Parcelable] class from a [Parcel].
         */
        @Suppress("unused") // Unused but instructional
        @JvmField
        val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo> {
            /**
             * Create a new instance of the [Parcelable] class, instantiating it from the given
             * [Parcel] whose data had previously been written by [Parcelable.writeToParcel]}.
             * We just return a new instance of constructed from our [Parcel] parameter [parcel].
             *
             * @param parcel The [Parcel] to read the object's data from.
             * @return Returns a new instance of the [Parcelable] class.
             */
            override fun createFromParcel(parcel: Parcel): Photo {
                return Photo(parcel)
            }

            /**
             * Create a new array of the [Parcelable] class.
             *
             * @param size Size of the array.
             * @return Returns an array of the [Parcelable] class, with every entry
             * initialized to `null`.
             */
            override fun newArray(size: Int): Array<Photo?> {
                return arrayOfNulls(size)
            }
        }
    }
}