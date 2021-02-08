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

import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionSet

/**
 * Creates a transition like [androidx.transition.AutoTransition], but customized to be more
 * true to Material Design.
 *
 * Fade through involves one element fading out completely before a new one fades in. These
 * transitions can be applied to text, icons, and other elements that don't perfectly overlap.
 * This technique lets the background show through during a transition, and it can provide
 * continuity between screens when paired with a shared transformation.
 * Used in `FadeThroughActivity`.
 *
 * See
 * [Expressing continuity](https://material.io/design/motion/understanding-motion.html#expressing-continuity)
 * for the detail of fade through.
 */
fun fadeThrough(): Transition {
    return transitionTogether {
        interpolator = FAST_OUT_SLOW_IN
        this += ChangeBounds()
        this += transitionSequential {
            addTransition(Fade(Fade.OUT))
            addTransition(Fade(Fade.IN))
        }
    }
}

/**
 * Returns a new instance of [TransitionSet] whose [TransitionSet.setOrdering] (aka kotlin `ordering`
 * property) is [TransitionSet.ORDERING_TOGETHER] (flag used to indicate that the child transitions
 * of this set should all start at the same time) which has our [body] lambda parameter "applied" to
 * it. Used in `CheeseArticleFragment`, `CheeseDetailFragment`, `CheeseGridFragment`, and in this
 * file in our [fadeThrough] factory method.
 *
 * @param body a lambda whose receiver is a [TransitionSet]
 */
inline fun transitionTogether(crossinline body: TransitionSet.() -> Unit): TransitionSet {
    return TransitionSet().apply {
        ordering = TransitionSet.ORDERING_TOGETHER
        body()
    }
}

/**
 * Returns a new instance of [SequentialTransitionSet] which has our [body] lambda parameter applied
 * to it. Used in `LoadingActivity`, and in this file in our [fadeThrough] factory method.
 *
 * @param body a lambda whose receiver is a [SequentialTransitionSet]
 */
inline fun transitionSequential(
    crossinline body: SequentialTransitionSet.() -> Unit
): SequentialTransitionSet {
    return SequentialTransitionSet().apply(body)
}

/**
 * Calls the [action] function parameter on each of the [Transition] objects in its [TransitionSet]
 * receiver. We loop on `i` from 0 until [TransitionSet.getTransitionCount] (aka `transitionCount`
 * property in kotlin) and if the [Transition] at position `i` in the [TransitionSet] is not `null`
 * call our [action] parameter on that [Transition] (if it is `null` we throw the exception
 * [IndexOutOfBoundsException]). Used in `SequentialTransitionSet`.
 *
 * @param action function which takes a [Transition] as its argument and returns [Unit] (aka void).
 */
inline fun TransitionSet.forEach(action: (transition: Transition) -> Unit) {
    for (i in 0 until transitionCount) {
        action(getTransitionAt(i) ?: throw IndexOutOfBoundsException())
    }
}

/**
 * Calls the [action] function parameter with the [Int] index of each of the [Transition] objects in
 * its [TransitionSet] receiver, also passing the [Transition] at that index. We loop on `i` from 0
 * until [TransitionSet.getTransitionCount] (aka `transitionCount` property in kotlin) and if the
 * [Transition] at position `i` in the [TransitionSet] is not `null` call our [action] parameter
 * with the index `i` and the [Transition] at index `i` (if it is `null` we throw the exception
 * [IndexOutOfBoundsException]). Used in `SequentialTransitionSet`.
 *
 * @param action function which takes an [Int] index and a [Transition] as its arguments and returns
 * [Unit] (aka void).
 */
inline fun TransitionSet.forEachIndexed(action: (index: Int, transition: Transition) -> Unit) {
    for (i in 0 until transitionCount) {
        action(i, getTransitionAt(i) ?: throw IndexOutOfBoundsException())
    }
}

/**
 * Our factory method to produce a [MutableIterator] for a mutable [TransitionSet] collection.
 * Provides the ability to remove elements while iterating.
 * TODO: This does not seem to be used but use could be hidden in koltin syntax -- verify this
 */
operator fun TransitionSet.iterator() = object : MutableIterator<Transition> {

    /**
     * Current index into our [TransitionSet].
     */
    private var index = 0

    /**
     * Returns `true` if there are still [Transition] objects in our [TransitionSet] to iterate over
     */
    override fun hasNext() = index < transitionCount

    /**
     * Returns next [Transition] object in our [TransitionSet] and increments the [index] pointer
     */
    override fun next() =
        getTransitionAt(index++) ?: throw IndexOutOfBoundsException()

    /**
     * Removes next [Transition] object in our [TransitionSet] and decrements the [index] pointer
     */
    override fun remove() {
        removeTransition(getTransitionAt(--index) ?: throw IndexOutOfBoundsException())
    }
}

/**
 * Adds its [Transition] parameter [transition] to the [TransitionSet] receiver. Used in this file
 * in our [fadeThrough] factory method, as well as in `LoadingActivity`, `CheeseArticleFragment`,
 * `CheeseDetailFragment`, and `CheeseGridFragment`.
 */
operator fun TransitionSet.plusAssign(transition: Transition) {
    addTransition(transition)
}

/**
 *  Returns the child [Transition] at the position [i] in the [TransitionSet] or throws the exception
 *  [IndexOutOfBoundsException] if there is none. Only used in the `SequentialTransitionSetTest` test
 *  class.
 */
operator fun TransitionSet.get(i: Int): Transition {
    return getTransitionAt(i) ?: throw IndexOutOfBoundsException()
}
