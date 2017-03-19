/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.events;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.di.categories.Cache;
import edu.uofk.eeese.eeese.di.scopes.ActivityScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@ActivityScope
public class EventsPresenter implements EventsContract.Presenter {

    private EventsContract.View mView;
    private BaseDataRepository mSource;
    private BaseSchedulerProvider mSchedulerProvider;
    private CompositeDisposable mSubscriptions;

    @Inject
    public EventsPresenter(@NonNull EventsContract.View view,
                           @NonNull @Cache BaseDataRepository source,
                           @NonNull BaseSchedulerProvider schedulerProvider) {
        mView = view;
        mSource = source;
        mSchedulerProvider = schedulerProvider;

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void loadEvents(boolean forceUpdate) {
        mView.showLoadingIndicator();
        Disposable subscription =
                mSource.getEvents(forceUpdate)
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .doFinally(mView::hideLoadingIndicator)
                        .subscribe(
                                //OnSuccess
                                events -> mView.showEvents(events),
                                //OnError
                                throwable -> {
                                    mView.showNoEvents();
                                    mView.showConnectionError();
                                }
                        );
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
