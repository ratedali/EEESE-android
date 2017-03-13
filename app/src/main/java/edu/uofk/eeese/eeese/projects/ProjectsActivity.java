/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.projects;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.details.DetailsActivity;
import edu.uofk.eeese.eeese.util.ActivityUtils;
import edu.uofk.eeese.eeese.util.ViewUtils;

public class ProjectsActivity extends AppCompatActivity implements ProjectsFragment.OnProjectSelectedListener {

    private static final String APPLICATION_FORM_URL = "https://bit.ly/Projects-Participation";

    // Navigation views
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    public NavigationView mNavView;
    @BindView(R.id.appbar)
    public AppBarLayout mAppBar;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindString(R.string.transitionname_toolbar)
    public String toolbarTransitionName;
    @BindView(R.id.tablayout)
    public TabLayout mTabLayout;

    // Content Views
    @BindView(R.id.content_view)
    public CoordinatorLayout mContentView;
    @BindView(R.id.viewpager)
    public ViewPager mViewPager;

    // Control
    private boolean mExit;
    @BindString(R.string.transitionname_projectcard)
    public String projectCardTransitionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        ActivityUtils.setEnterTransition(this, R.transition.projects_enter);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_projectcard);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_projectcard);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.projects));
        }

        List<ProjectsFragment> projects = Arrays.asList(
                ProjectsFragment.getInstance(Project.POWER),
                ProjectsFragment.getInstance(Project.TELECOM),
                ProjectsFragment.getInstance(Project.SOFTWARE),
                ProjectsFragment.getInstance(Project.ELECTRONICS_CONTROL));
        List<String> categories = Arrays.asList(
                getString(R.string.power_category),
                getString(R.string.telecom_category),
                getString(R.string.software_category),
                getString(R.string.electronics_control_category)
        );
        mViewPager.setAdapter(new ProjectsPagerAdapter(
                getSupportFragmentManager(),
                projects,
                categories));
        mTabLayout.setupWithViewPager(mViewPager);

        setupDrawer(mNavView, mDrawerLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ViewUtils.openDrawer(mDrawerLayout);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mExit)
            finish();
    }

    private void setupDrawer(@NonNull NavigationView navView,
                             @NonNull final DrawerLayout drawer) {
        navView.setCheckedItem(R.id.nav_projects);
        drawer.closeDrawers();
        final Activity source = this;
        navView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            drawer.closeDrawers();
            Class<? extends Activity> targetActivity = ActivityUtils.getTargetActivity(item);
            if (targetActivity != null && !targetActivity.equals(source.getClass())) {
                Intent intent = new Intent(source, targetActivity);
                ActivityUtils.startActivityWithTransition(source, intent,
                        new Pair<>(mAppBar, toolbarTransitionName));
                mExit = true;
            }
            return true;
        });
    }

    @Override
    public void onProjectSelected(String projectId, View projectView) {
        ActivityUtils.setTransitionName(projectView, projectCardTransitionName);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.PROJECT_ID_KEY, projectId);
        ActivityUtils.startActivityWithTransition(this, intent,
                new Pair<>(mAppBar, toolbarTransitionName),
                new Pair<>(projectView, projectCardTransitionName));
    }

    @OnClick(R.id.fab)
    public void participate(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APPLICATION_FORM_URL));
        startActivity(intent);
    }

    class ProjectsPagerAdapter extends FragmentPagerAdapter {

        @NonNull
        private List<ProjectsFragment> mProjectsFragments;

        @NonNull
        private List<String> mCategories;
        public ProjectsPagerAdapter(FragmentManager fm,
                                    @Nullable List<ProjectsFragment> projectsFragments,
                                    @Nullable List<String> categories) {
            super(fm);
            mProjectsFragments = projectsFragments != null ?
                    projectsFragments : Collections.emptyList();
            mCategories = categories != null ? categories : Collections.emptyList();
            // use empty categories for unspecified ones
            for (int i = 0; i < mProjectsFragments.size() - mCategories.size(); ++i) {
                mCategories.add("");
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mProjectsFragments.get(position);
        }

        @Override
        public int getCount() {
            return mProjectsFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCategories.get(position);
        }
    }
}
