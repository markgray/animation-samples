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
    private val format: String?
    private val width: Int
    private val height: Int
    private val filename: String?
    @JvmField
    val id: Long
    @JvmField
    val author: String?
    private val authorUrl: String?
    private val postUrl: String?

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
        this.authorUrl = author_url
        this.postUrl = post_url
    }

    protected constructor(parcel: Parcel) {
        format = parcel.readString()
        width = parcel.readInt()
        height = parcel.readInt()
        filename = parcel.readString()
        id = parcel.readLong()
        author = parcel.readString()
        authorUrl = parcel.readString()
        postUrl = parcel.readString()
    }

    fun getPhotoUrl(requestWidth: Int): String {
        return String.format(Locale.getDefault(), PHOTO_URL_BASE, requestWidth, id)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(format)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeString(filename)
        dest.writeLong(id)
        dest.writeString(author)
        dest.writeString(authorUrl)
        dest.writeString(postUrl)
    }

    companion object {
        private const val PHOTO_URL_BASE = "https://unsplash.it/%d?image=%d"
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo> {
            override fun createFromParcel(parcel: Parcel): Photo {
                return Photo(parcel)
            }

            override fun newArray(size: Int): Array<Photo?> {
                return arrayOfNulls(size)
            }
        }
    }
}