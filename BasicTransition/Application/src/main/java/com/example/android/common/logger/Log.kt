/*
 * Copyright (C) 2013 The Android Open Source Project
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
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.android.common.logger

import android.util.Log

/**
 * Helper class for a list (or tree) of LoggerNodes.
 *
 *
 * When this is set as the head of the list,
 * an instance of it can function as a drop-in replacement for [android.util.Log].
 * Most of the methods in this class server only to map a method call in Log to its equivalent
 * in LogNode.
 */
object Log {
    /**
     * Grabbing the native values from Android's native logging facilities, to make for easy
     * migration and interop.
     */
    const val NONE: Int = -1

    /**
     * Highest level of verbosity, use Log.v()
     */
    const val VERBOSE: Int = Log.VERBOSE

    /**
     * Second highest level of verbosity, use Log.d()
     */
    const val DEBUG: Int = Log.DEBUG

    /**
     * Third highest level of verbosity, use Log.i()
     */
    const val INFO: Int = Log.INFO

    /**
     * Fourth highest level of verbosity, use Log.w()
     */
    const val WARN: Int = Log.WARN

    /**
     * Lowest highest level of verbosity, use Log.e()
     */
    const val ERROR: Int = Log.ERROR

    /**
     * Priority constant for the println method, use Log.wtf(). The doc say: Log.wtf() might call
     * onTerribleFailure() and can cause termination of your application.
     */
    const val ASSERT: Int = Log.ASSERT

    /**
     * Stores the beginning of the LogNode topology.
     */
    var logNode: LogNode? = null

    /**
     * Instructs the LogNode to print the log data provided. Other LogNodes can
     * be chained to the end of the LogNode as desired.
     *
     * @param priority Log level of the data being logged. Verbose, Error, etc.
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun println(priority: Int, tag: String?, msg: String?, tr: Throwable? = null) {
        if (logNode != null) {
            (logNode ?: return).println(priority, tag, msg, tr)
        }
    }

    /**
     * Prints a message at VERBOSE priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun v(tag: String?, msg: String?, tr: Throwable? = null) {
        println(VERBOSE, tag, msg, tr)
    }

    /**
     * Prints a message at DEBUG priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun d(tag: String?, msg: String?, tr: Throwable? = null) {
        println(DEBUG, tag, msg, tr)
    }

    /**
     * Prints a message at INFO priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun i(tag: String?, msg: String?, tr: Throwable? = null) {
        println(INFO, tag, msg, tr)
    }

    /**
     * Prints a message at WARN priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun w(tag: String?, msg: String?, tr: Throwable? = null) {
        println(WARN, tag, msg, tr)
    }

    /**
     * Prints a message at WARN priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    fun w(tag: String?, tr: Throwable?) {
        w(tag, null, tr)
    }

    /**
     * Prints a message at ERROR priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun e(tag: String?, msg: String?, tr: Throwable? = null) {
        println(ERROR, tag, msg, tr)
    }

    /**
     * Prints a message at ASSERT priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    @JvmOverloads
    fun wtf(tag: String?, msg: String?, tr: Throwable? = null) {
        println(ASSERT, tag, msg, tr)
    }

    /**
     * Prints a message at ASSERT priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     * to extract and print useful information.
     */
    fun wtf(tag: String?, tr: Throwable?) {
        wtf(tag, null, tr)
    }
}