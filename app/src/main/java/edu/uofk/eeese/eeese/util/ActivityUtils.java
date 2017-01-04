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

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.home.HomeActivity;
import edu.uofk.eeese.eeese.projects.ProjectsActivity;

public final class ActivityUtils {

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

    @SafeVarargs
    public static void startActivityWithTransition(
            @NonNull Activity activity,
            Intent intent,
            Pair<View, String>... shared) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(activity, shared)
                            .toBundle());
        } else {
            activity.startActivity(intent);
        }
    }
}
