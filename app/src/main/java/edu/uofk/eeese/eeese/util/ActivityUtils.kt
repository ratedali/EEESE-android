/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.support.annotation.TransitionRes
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Pair
import android.view.MenuItem
import android.view.View
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.about.AboutActivity
import edu.uofk.eeese.eeese.events.EventsActivity
import edu.uofk.eeese.eeese.projects.ProjectsActivity
import edu.uofk.eeese.eeese.util.FrameworkUtils.atLeastLollipop
import edu.uofk.eeese.eeese.util.FrameworkUtils.belowLollipop

@SuppressLint("NewApi")
object ActivityUtils {

    fun getTargetActivity(item: MenuItem): Class<out Activity>? {
        var targetActivity: Class<out Activity>? = null
        when (item.itemId) {
            R.id.nav_about -> targetActivity = AboutActivity::class.java
            R.id.nav_projects -> targetActivity = ProjectsActivity::class.java
            R.id.nav_events -> targetActivity = EventsActivity::class.java
        }
        return targetActivity
    }

    fun startActivityWithTransition(
            sourceActivity: Activity,
            intent: Intent,
            vararg shared: Pair<View, String>) {
        if (atLeastLollipop) {
            sourceActivity
                    .startActivity(
                            intent,
                            ActivityOptions
                                    .makeSceneTransitionAnimation(sourceActivity, *shared)
                                    .toBundle())
        } else {
            sourceActivity.startActivity(intent)
        }
    }

    fun setTransitionName(view: View, transitionName: String) {
        if (belowLollipop) {
            return
        }
        view.transitionName = transitionName
    }

    /**
     * Inflates the given transition resource and sets it as the enter transition for the activity

     * @param activity     the transition target activity
     * @param transitionId the resource ID of the enter transition xml file
     */
    fun setEnterTransition(activity: Activity, @TransitionRes transitionId: Int) {
        if (belowLollipop) {
            return
        }
        val transition = TransitionInflater.from(activity).inflateTransition(transitionId)
        setEnterTransition(activity, transition)
    }

    /**
     * Sets the given transition as the enter transition for the activity

     * @param activity   the transition target activity
     * @param transition the enter transition
     */
    fun setEnterTransition(activity: Activity, transition: Transition) {
        if (belowLollipop) {
            return
        }
        activity.window.enterTransition = transition
    }

    /**
     * Inflates the given transition resource and sets it as the exit transition for the activity

     * @param activity     the transition target activity
     * *
     * @param transitionId the resource ID of the exit transition xml file
     */
    fun setExitTransition(activity: Activity, @TransitionRes transitionId: Int) {
        if (belowLollipop) {
            return
        }
        val transition = TransitionInflater.from(activity).inflateTransition(transitionId)
        setExitTransition(activity, transition)
    }

    /**
     * Sets the given transition as the exit transition for the activity

     * @param activity   the transition target activity
     * @param transition the exit transition
     */
    fun setExitTransition(activity: Activity, transition: Transition) {
        if (belowLollipop) {
            return
        }
        activity.window.exitTransition = transition
    }

    /**
     * Inflates the given transition resource and sets it as the shared element enter transition for
     * the activity

     * @param activity     the transition target activity
     * @param transitionId the resource ID of the shared element enter transition xml file
     */
    fun setSharedElementEnterTransition(activity: Activity, @TransitionRes transitionId: Int) {
        if (belowLollipop) {
            return
        }
        val transition = TransitionInflater.from(activity).inflateTransition(transitionId)
        setSharedElementEnterTransition(activity, transition)
    }

    /**
     * Sets the given transition as the shared element enter transition for the activity

     * @param activity   the transition target activity
     * *
     * @param transition the shared element enter transition
     */
    fun setSharedElementEnterTransition(activity: Activity, transition: Transition) {
        if (belowLollipop) {
            return
        }
        activity.window.sharedElementEnterTransition = transition
    }

    /**
     * Inflates the given transition resource and sets it as the shared element exit transition for
     * the activity

     * @param activity     the transition target activity
     * @param transitionId the resource ID of the shared element exit transition xml file
     */
    fun setSharedElementExitTransition(activity: Activity, @TransitionRes transitionId: Int) {
        if (belowLollipop) {
            return
        }
        val transition = TransitionInflater.from(activity).inflateTransition(transitionId)
        setSharedElementExitTransition(activity, transition)
    }

    /**
     * Sets the given transition as the shared element exit transition for the activity

     * @param activity   the transition target activity
     * @param transition the shared element exit transition
     */
    fun setSharedElementExitTransition(activity: Activity, transition: Transition) {
        if (belowLollipop) {
            return
        }
        activity.window.sharedElementExitTransition = transition
    }
}
