/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.TransitionRes;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.home.HomeActivity;
import edu.uofk.eeese.eeese.projects.ProjectsActivity;

public final class ActivityUtils {

    public static boolean atLeastApi(int code) {
        return Build.VERSION.SDK_INT >= code;
    }

    @Nullable
    public static Class<? extends Activity> getTargetActivity(@NonNull MenuItem item) {
        Class<? extends Activity> targetActivity = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                targetActivity = HomeActivity.class;
                break;
            case R.id.nav_projects:
                targetActivity = ProjectsActivity.class;
                break;
            case R.id.nav_info:
                // TODO: 1/4/17 Add InfoActivity
                break;
        }
        return targetActivity;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SafeVarargs
    public static void startActivityWithTransition(
            @NonNull Activity activity,
            Intent intent,
            Pair<View, String>... shared) {
        if (atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            activity.startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(activity, shared)
                            .toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * Inflates the given transition resource and sets it as the enter transition for the activity
     *
     * @param activity     the transition target activity
     * @param transitionId the resource id of the enter transition xml file
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setEnterTransition(@NonNull Activity activity, @TransitionRes int transitionId) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        Transition transition = TransitionInflater.from(activity).inflateTransition(transitionId);
        setEnterTransition(activity, transition);
    }

    /**
     * Sets the given transition as the enter transition for the activity
     *
     * @param activity   the transition target activity
     * @param transition the enter transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setEnterTransition(@NonNull Activity activity, Transition transition) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        activity.getWindow().setEnterTransition(transition);
    }

    /**
     * Inflates the given transition resource and sets it as the exit transition for the activity
     *
     * @param activity     the transition target activity
     * @param transitionId the resource id of the exit transition xml file
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setExitTransition(@NonNull Activity activity, @TransitionRes int transitionId) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        Transition transition = TransitionInflater.from(activity).inflateTransition(transitionId);
        setExitTransition(activity, transition);
    }

    /**
     * Sets the given transition as the exit transition for the activity
     *
     * @param activity   the transition target activity
     * @param transition the exit transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setExitTransition(@NonNull Activity activity, Transition transition) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        activity.getWindow().setExitTransition(transition);
    }

    /**
     * Inflates the given transition resource and sets it as the shared element enter transition for
     * the activity
     *
     * @param activity     the transition target activity
     * @param transitionId the resource id of the shared element enter transition xml file
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setSharedElementEnterTransition(@NonNull Activity activity, @TransitionRes int transitionId) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        Transition transition = TransitionInflater.from(activity).inflateTransition(transitionId);
        setSharedElementEnterTransition(activity, transition);
    }

    /**
     * Sets the given transition as the shared element enter transition for the activity
     *
     * @param activity   the transition target activity
     * @param transition the shared element enter transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setSharedElementEnterTransition(@NonNull Activity activity, Transition transition) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        activity.getWindow().setSharedElementEnterTransition(transition);
    }

    /**
     * Inflates the given transition resource and sets it as the shared element exit transition for
     * the activity
     *
     * @param activity     the transition target activity
     * @param transitionId the resource id of the shared element exit transition xml file
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setSharedElementExitTransition(@NonNull Activity activity, @TransitionRes int transitionId) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        Transition transition = TransitionInflater.from(activity).inflateTransition(transitionId);
        setSharedElementExitTransition(activity, transition);
    }

    /**
     * Sets the given transition as the shared element exit transition for the activity
     *
     * @param activity   the transition target activity
     * @param transition the shared element exit transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setSharedElementExitTransition(@NonNull Activity activity, Transition transition) {
        if (!atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        activity.getWindow().setSharedElementExitTransition(transition);
    }
}
