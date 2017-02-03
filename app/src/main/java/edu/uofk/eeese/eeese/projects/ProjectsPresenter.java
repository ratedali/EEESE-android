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

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ProjectsPresenter implements ProjectsContract.Presenter {

    @NonNull
    private BaseDataRepository mSource;
    @NonNull
    private ProjectsContract.View mView;
    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;
    private
    @Project.ProjectCategory
    int mCategory;

    private CompositeDisposable mSubscriptions;

    @Inject
    public ProjectsPresenter(@NonNull BaseDataRepository source,
                             @NonNull ProjectsContract.View view,
                             @NonNull BaseSchedulerProvider schedulerProvider,
                             @Project.ProjectCategory int category) {
        mSource = source;
        mView = view;
        mSchedulerProvider = schedulerProvider;
        mCategory = category;
        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void loadProjects(final boolean force) {
        mView.setLoadingIndicator(true);
        Disposable subscription =
                mSource.getProjectsWithCategory(force, mCategory)
                        .subscribeOn(mSchedulerProvider.io())
                        // sort by name
                        .map(new Function<List<Project>, List<Project>>() {
                            @Override
                            public List<Project> apply(List<Project> projects) throws Exception {
                                Collections.sort(projects, new Comparator<Project>() {
                                    @Override
                                    public int compare(Project lhs, Project rhs) {
                                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                                    }
                                });
                                return projects;
                            }
                        })
                        .subscribeOn(mSchedulerProvider.computation())
                        .observeOn(mSchedulerProvider.ui())
                        .doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                mView.setLoadingIndicator(false);
                            }
                        })
                        .subscribe(
                                // OnSuccess
                                new Consumer<List<Project>>() {
                                    @Override
                                    public void accept(List<Project> projects) throws Exception {
                                        if (projects.size() > 0) {
                                            mView.showProjects(projects);
                                        } else {
                                            mView.showNoProjects();
                                        }
                                    }
                                },
                                // OnError
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.showNoConnectionError();
                                        mView.showNoProjects();
                                    }
                                }
                        );
        mSubscriptions.add(subscription);
    }

    @Override
    public void openProjectDetails(@NonNull Project project) {
        mView.showProjectDetails(project.getId());
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
