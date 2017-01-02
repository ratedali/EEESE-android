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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.Injection;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.R2;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.util.ViewUtils;

public class ProjectsActivity extends AppCompatActivity implements ProjectsContract.View {

    // Navigation views
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R2.id.nav_view)
    public NavigationView mNavView;
    @BindView(R2.id.toolbar)
    public Toolbar toolbar;

    // Content Views
    @BindView(R2.id.content_view)
    public CoordinatorLayout mContentView;
    @BindView(R2.id.projects_list)
    public RecyclerView mProjectsList;
    @BindView(R2.id.error_view)
    public View mErrorView;

    private ProjectsAdapter mAdapter;
    private ProjectsContract.Presenter mPresenter;

    private Snackbar indicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.projects));
        }

        mAdapter = new ProjectsAdapter(this);
        mProjectsList.setLayoutManager(new LinearLayoutManager(this));
        mProjectsList.setAdapter(mAdapter);
        ProjectsContract.Presenter presenter = Injection.provideProjectsPresenter(this, this);
        setPresenter(presenter);

        ViewUtils.setupDrawerListener(mNavView, mDrawerLayout, this);
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
    public void setPresenter(@NonNull ProjectsContract.Presenter presenter) {
        mPresenter = presenter;
        presenter.subscribe();
    }

    @Override
    public void showProjects(@NonNull List<Project> projects) {
        mAdapter.changeProjects(projects);
        mProjectsList.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void showProjectDetails(@NonNull String projectId) {
        // TODO: 12/31/16 show project details ui
    }

    @Override
    public void setLoadingIndicator(boolean visibility) {
        // TODO: 12/31/16 change to a loading indicator
        if (visibility) {
            if (indicator == null) {
                indicator = Snackbar.make(mContentView, R.string.loading_projects, Snackbar.LENGTH_INDEFINITE);
            }
            indicator.show();
        } else {
            indicator.dismiss();
        }

    }

    @Override
    public void showNoProjects() {
        mProjectsList.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoConnectionError() {
        Snackbar.make(mContentView, R.string.connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.loadProjects(true);
                    }
                })
                .show();
    }
}
