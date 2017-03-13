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

import android.support.annotation.NonNull;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.di.scopes.ActivityScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@ActivityScope
public class DetailsPresenter implements DetailsContract.Presenter {

    @NonNull
    private DetailsContract.View mView;
    @NonNull
    private BaseDataRepository mSource;
    @NonNull
    private BaseSchedulerProvider mScheduler;
    @NonNull
    private String mProjectId;

    private CompositeDisposable mSubscriptions;

    @Inject
    public DetailsPresenter(@NonNull BaseDataRepository source,
                            @NonNull DetailsContract.View view,
                            @NonNull BaseSchedulerProvider schedulerProvider,
                            @NonNull String projectId) {
        mSubscriptions = new CompositeDisposable();
        mSource = source;

        mView = view;
        mView.setPresenter(this);

        mScheduler = schedulerProvider;

        mProjectId = projectId;
    }

    @Override
    public void loadDetails(boolean force) {
        Disposable subscription = mSource.getProject(mProjectId, force)
                .observeOn(mScheduler.ui())
                .subscribe(
                        // onSuccess
                        mView::showProjectInfo,
                        // onError
                        throwable -> mView.showInvalidProject()
                );
        mSubscriptions.add(subscription);
    }

    @Override
    public void applyForProject() {
        //TODO apply for the project
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
