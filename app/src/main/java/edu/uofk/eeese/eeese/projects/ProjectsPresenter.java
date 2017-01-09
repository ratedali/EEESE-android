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

import java.util.List;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class ProjectsPresenter implements ProjectsContract.Presenter {

    @NonNull
    private DataRepository mSource;
    @NonNull
    private ProjectsContract.View mView;
    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeDisposable mSubscriptions;

    @Inject
    public ProjectsPresenter(@NonNull DataRepository source,
                             @NonNull ProjectsContract.View view,
                             @NonNull BaseSchedulerProvider schedulerProvider) {
        mSource = source;
        mView = view;
        mSchedulerProvider = schedulerProvider;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void loadProjects(boolean force) {
        mView.setLoadingIndicator(true);
        Disposable subscription =
                mSource.getProjects(force)
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                mView.setLoadingIndicator(false);
                            }
                        })
                        .subscribe(
                                // OnNext
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
                                    }
                                }
                        );
        mSubscriptions.add(subscription);
    }

    @Override
    public void openProjectDetails(@NonNull Project project) {
        // TODO: 12/31/16 select the project id and open project details ui
    }

    @Override
    public void subscribe() {
        loadProjects(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
