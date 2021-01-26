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

package com.example.android.motion.demo.sharedelement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.motion.R
import com.example.android.motion.model.Cheese
import com.example.android.motion.ui.EdgeToEdge

/**
 * Navigation > Shared element
 *
 * A transition may include a focal element, which is a persistent element significant to the
 * hierarchy that can be tweened. Like animated containers, focal elements enhance continuity
 * by seamlessly transforming their appearance. The starting fragment [CheeseGridFragment] shows
 * all of the [Cheese] objects in the [Cheese.ALL] list of [Cheese] in a 3 wide grid, and when
 * you click on one of them it navigates to the [CheeseDetailFragment] to display the "details"
 * of the [Cheese] using the cheese picture as the shared focal element.
 */
class SharedElementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shared_element_activity)
        EdgeToEdge.setUpRoot(findViewById(R.id.nav_host))
    }
}
