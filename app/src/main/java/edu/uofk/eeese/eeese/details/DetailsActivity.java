/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.EEESEapp;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.util.ActivityUtils;

public class DetailsActivity extends AppCompatActivity implements DetailsContract.View {

    private String mProjectId;
    public static final String PROJECT_ID_KEY = "edu.uofk.eeese.eeese.PROJECT_ID";

    @Inject
    public DetailsContract.Presenter mPresenter;

    @BindView(R.id.appbar)
    public AppBarLayout appBar;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private ActionBar mActionBar;

    @BindView(R.id.project_name)
    public TextView mProjectName;
    @BindView(R.id.project_head)
    public TextView mProjectHead;
    @BindView(R.id.project_desc)
    public TextView mProjectDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mProjectId = intent.getStringExtra(PROJECT_ID_KEY);

        ((EEESEapp) getApplication()).getAppComponent()
                .detailsComponent(new DetailsModule(mProjectId, this))
                .inject(this);

        ActivityUtils.setEnterTransition(this, R.transition.details_enter);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_projectcard);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_projectcard);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if ((mActionBar = getSupportActionBar()) != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
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
    public void showProjectInfo(@NonNull Project project) {
        mProjectName.setText(project.getName());
        mProjectHead.setText(project.getProjectHead());
        mProjectDesc.setText(project.getDesc()); //TODO check if description is empty
    }

    @Override
    public void showInvalidProject() {

    }

    @Override
    public void setPresenter(@NonNull DetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
