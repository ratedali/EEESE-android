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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
    @BindView(R.id.prereq_list)
    public RecyclerView mPrereqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mProjectId = intent.getStringExtra(PROJECT_ID_KEY);

        ((EEESEapp) getApplication()).getAppComponent()
                .detailsComponent(new DetailsModule(mProjectId, this))
                .inject(this);

        ActivityUtils.setEnterTransition(this, R.transition.details);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_projectcard);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_projectcard);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if ((mActionBar = getSupportActionBar()) != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPrereqList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mPresenter.loadDetails(false);
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
        mProjectDesc.setText(project.getDesc());
        mPrereqList.swapAdapter(new PrereqAdapter(project.getPrerequisites()), false);
    }

    @Override
    public void showInvalidProject() {

    }

    @Override
    public void setPresenter(@NonNull DetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Adapter for the prerequisites list, simply puts the prerequisite text in a TextView
     */
    static class PrereqAdapter extends RecyclerView.Adapter<PrereqAdapter.ViewHolder> {

        @NonNull
        private List<String> mPrerequisites;

        public PrereqAdapter(@NonNull List<String> prerequisites) {
            mPrerequisites = prerequisites;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.prereq_list_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mPrerequisites.get(position));
        }

        @Override
        public int getItemCount() {
            return mPrerequisites.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.prereq_textview)
            public TextView text;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
