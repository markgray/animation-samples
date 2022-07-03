/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.activityscenetransitionbasic

/**
 * Represents an Item in our application. Each item has a name, id, full size image url and
 * thumbnail url.
 *
 * @param name The human readable name of the photo.
 * @param author The name of the photographer.
 * @param mFileName The file name on the remote server.
 */
class Item internal constructor(
    val name: String,
    val author: String,
    private val mFileName: String
) {
    /**
     * The unique ID of the [Item], consists of the sum of the hash code values of the [name] and
     * [mFileName] fields.
     */
    val id: Int
        get() = name.hashCode() + mFileName.hashCode()

    /**
     * The complete URL for the full-size image on the remote server, calculated by appending our
     * [mFileName] field to the [LARGE_BASE_URL] base URL.
     */
    val photoUrl: String
        get() = LARGE_BASE_URL + mFileName

    /**
     * The complete URL for the thumbnail image on the remote server, calculated by appending our
     * [mFileName] field to the [THUMB_BASE_URL] base URL.
     */
    val thumbnailUrl: String
        get() = THUMB_BASE_URL + mFileName

    companion object {
        /**
         * The base URL for all of the full-size images on the remote server.
         */
        private const val LARGE_BASE_URL = "https://storage.googleapis.com/androiddevelopers/sample_data/activity_transition/large/"

        /**
         * The base URL for all of the thumbnail images on the remote server.
         */
        private const val THUMB_BASE_URL = "https://storage.googleapis.com/androiddevelopers/sample_data/activity_transition/thumbs/"

        /**
         * Our dataset.
         */
        var ITEMS: Array<Item> = arrayOf(
            Item("Flying in the Light", "Romain Guy", "flying_in_the_light.jpg"),
            Item("Caterpillar", "Romain Guy", "caterpillar.jpg"),
            Item("Look Me in the Eye", "Romain Guy", "look_me_in_the_eye.jpg"),
            Item("Flamingo", "Romain Guy", "flamingo.jpg"),
            Item("Rainbow", "Romain Guy", "rainbow.jpg"),
            Item("Over there", "Romain Guy", "over_there.jpg"),
            Item("Jelly Fish 2", "Romain Guy", "jelly_fish_2.jpg"),
            Item("Lone Pine Sunset", "Romain Guy", "lone_pine_sunset.jpg")
        )

        /**
         * Retrieves the [Item] in our dataset [ITEMS] which has the same ID as our parameter [id].
         * It does this by looping through all of the items in our dataset [ITEMS] until it finds an
         * [Item] with the same ID as our parameter [id] then returning that [Item]
         *
         * @param id The `id` property of the [Item] we are searching for.
         * @return The [Item] in our dataset with the same ID as our parameter [id] or `null` if it
         * is not found.
         */
        fun getItem(id: Int): Item? {
            for (item in ITEMS) {
                if (item.id == id) {
                    return item
                }
            }
            return null
        }
    }
}