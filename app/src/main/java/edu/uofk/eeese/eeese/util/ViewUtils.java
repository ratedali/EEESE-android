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
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.home.HomeActivity;
import edu.uofk.eeese.eeese.projects.ProjectsActivity;

public class ViewUtils {
    public static DrawerLayout openDrawer(final DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
        return drawerLayout;
    }

    public static NavigationView setupDrawerListener(NavigationView navView,
                                                     final DrawerLayout drawerLayout,
                                                     final Activity sourceActivity) {

        int currentItemId;

        final Class<? extends Activity> sourceActivityClass = sourceActivity.getClass();
        if (sourceActivityClass.equals(HomeActivity.class)) {
            currentItemId = R.id.nav_home;
        } else if (sourceActivityClass.equals(ProjectsActivity.class)) {
            currentItemId = R.id.nav_projects;
        } else {
            currentItemId = -1;
        }

        if (currentItemId != -1) {
            navView.setCheckedItem(currentItemId);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);
                drawerLayout.closeDrawers();

                Class<?> targetActivity = null;
                boolean allowBack = false;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        targetActivity = HomeActivity.class;
                        break;
                    case R.id.nav_projects:
                        targetActivity = ProjectsActivity.class;
                        break;
                    case R.id.nav_info:
                    default:
                        allowBack = true;
                }
                if (targetActivity == null || sourceActivityClass.equals(targetActivity)) {
                    return false;
                }

                Intent intent = new Intent(sourceActivity, targetActivity);
                sourceActivity.startActivity(intent);
                if (!allowBack) {
                    sourceActivity.finish();
                }
                return true;
            }
        });

        return navView;
    }

}
