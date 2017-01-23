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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import com.transitionseverywhere.TransitionManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.EEESEapp;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.details.DetailsActivity;
import edu.uofk.eeese.eeese.util.ActivityUtils;
import edu.uofk.eeese.eeese.util.ViewUtils;

public class ProjectsActivity extends AppCompatActivity implements ProjectsContract.View {

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

    // Content Views
    @BindView(R.id.content_view)
    public CoordinatorLayout mContentView;
    @BindView(R.id.projects_list)
    public RecyclerView mProjectsList;
    @BindView(R.id.error_view)
    public View mErrorView;

    @BindInt(R.integer.number_of_columns)
    public int numOfColumns;

    private boolean mExit;

    private ProjectsAdapter mAdapter;
    private View mSelectedProjectView;
    @BindString(R.string.transitionname_projectcard)
    public String projectCardTransitionName;

    private ProjectsAdapter.OnProjectSelectedListener projectSelectedListener =
            new ProjectsAdapter.OnProjectSelectedListener() {
                @Override
                public void showProjectDetails(Project project, View projectCard) {
                    mSelectedProjectView = projectCard;
                    mPresenter.openProjectDetails(project);
                }
            };

    @Inject
    public ProjectsContract.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        ActivityUtils.setEnterTransition(this, R.transition.projects_enter);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_projectcard);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_projectcard);

        ((EEESEapp) getApplication()).getAppComponent()
                .projectsComponent(new ProjectsModule(this))
                .inject(this);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.projects));
        }

        mAdapter = new ProjectsAdapter(this, projectSelectedListener);
        mProjectsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mProjectsList.setAdapter(mAdapter);

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
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
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
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawer.closeDrawers();
                Class<? extends Activity> targetActivity = ActivityUtils.getTargetActivity(item);
                if (targetActivity != null && !targetActivity.equals(source.getClass())) {
                    Intent intent = new Intent(source, targetActivity);
                    ActivityUtils.startActivityWithTransition(source, intent,
                            new Pair<View, String>(mAppBar, toolbarTransitionName));
                    mExit = true;
                }
                return true;
            }
        });
    }

    @Override
    public void setPresenter(@NonNull ProjectsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showProjects(@NonNull List<Project> projects) {
        mAdapter = new ProjectsAdapter(this, projects, projectSelectedListener);
        mProjectsList.swapAdapter(mAdapter, true);
        if (mProjectsList.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mContentView);
        }
        mProjectsList.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);

    }

    @Override
    public void showProjectDetails(@NonNull String projectId) {
        ActivityUtils.setTransitionName(mSelectedProjectView, projectCardTransitionName);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.PROJECT_ID_KEY, projectId);
        ActivityUtils.startActivityWithTransition(this, intent,
                new Pair<View, String>(mAppBar, toolbarTransitionName),
                new Pair<>(mSelectedProjectView, projectCardTransitionName));
    }

    @Override
    public void setLoadingIndicator(boolean visibility) {
        // TODO: 12/31/16 show a loading mLoadingIndicator
    }

    @Override
    public void showNoProjects() {
        if (mErrorView.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mContentView);
        }
        mErrorView.setVisibility(View.VISIBLE);
        mProjectsList.setVisibility(View.GONE);

    }

    @Override
    public void showNoConnectionError() {
        Snackbar.make(mContentView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
    }

    public void reloadProjects(View view) {
        mPresenter.loadProjects(true);
    }
}
