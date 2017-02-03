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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transitionseverywhere.TransitionManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.EEESEapp;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;

/**
 * A fragment that displays a list of projects with a specific ProjectCategory
 * {@link OnProjectSelectedListener} interface to handle project clicks
 */
public class ProjectsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ProjectsContract.View {

    // Content
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.projects_list)
    public RecyclerView mProjectsList;
    @BindInt(R.integer.number_of_columns)
    public int numOfColumns;
    @BindView(R.id.error_view)
    public View mErrorView;

    //  Control
    private ProjectsAdapter mAdapter;
    private View mSelectedProjectView;
    @Project.ProjectCategory
    private int mCategory;
    private ProjectsAdapter.OnProjectSelectedListener projectSelectedListener =
            new ProjectsAdapter.OnProjectSelectedListener() {
                @Override
                public void showProjectDetails(Project project, View projectCard) {
                    mSelectedProjectView = projectCard;
                    mPresenter.openProjectDetails(project);
                }
            };
    private static final String CATEGORY_KEY = "edu.uofk.eeese.eeese.ProjectsFragment.CATEGORY";

    private OnProjectSelectedListener mListener;

    @Inject
    public ProjectsContract.Presenter mPresenter;

    public ProjectsFragment() {
    }

    public static ProjectsFragment getInstance(@Project.ProjectCategory int category) {
        ProjectsFragment fragment = new ProjectsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(CATEGORY_KEY, category);
        fragment.setArguments(arguments);
        return fragment;
    }

    /*
     * As always, the int read from the arguments bundle will always be
     * a legal value, because it is always set to a legal value in the first place
     */
    @SuppressWarnings("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCategory = arguments.getInt(CATEGORY_KEY);
        }
        ((EEESEapp) getActivity().getApplicationContext()).getAppComponent()
                .projectsComponent(new ProjectsModule(this, mCategory))
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_projects, container, false);

        ButterKnife.bind(this, rootView);

        mSwipeRefresh.setOnRefreshListener(this);

        mAdapter = new ProjectsAdapter(getContext(), projectSelectedListener);
        mProjectsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mProjectsList.setAdapter(mAdapter);
        mPresenter.loadProjects(false);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProjectSelectedListener) {
            mListener = (OnProjectSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProjectSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void showProjects(@NonNull List<Project> projects) {
        mAdapter = new ProjectsAdapter(getContext(), projects, projectSelectedListener);
        mProjectsList.swapAdapter(mAdapter, true);
        if (mProjectsList.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mSwipeRefresh);
        }
        mProjectsList.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void showProjectDetails(@NonNull String projectId) {
        mListener.onProjectSelected(projectId, mSelectedProjectView);
    }

    @Override
    public void showNoProjects() {
        if (mErrorView.getVisibility() != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mSwipeRefresh);
        }
        mErrorView.setVisibility(View.VISIBLE);
        mProjectsList.setVisibility(View.GONE);

    }

    @Override
    public void setLoadingIndicator(boolean visibility) {
        mSwipeRefresh.setRefreshing(visibility);
    }

    @Override
    public void showNoConnectionError() {
        Snackbar.make(mSwipeRefresh, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(@NonNull ProjectsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onRefresh() {
        mPresenter.loadProjects(true);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnProjectSelectedListener {
        // TODO: Update argument type and name
        void onProjectSelected(String projectId, View projectView);
    }
}
