/*
 * Copyright 2016 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.home;

import android.support.annotation.NonNull;

import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class HomePresenter implements HomeContract.Presenter {

    private final String TAG = HomePresenter.class.getCanonicalName();

    private DataRepository mSource;
    private HomeContract.View mView;
    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeDisposable mSubscriptions;

    public HomePresenter(@NonNull DataRepository source,
                         @NonNull HomeContract.View view,
                         @NonNull BaseSchedulerProvider schedulerProvider) {
        mSubscriptions = new CompositeDisposable();
        mSource = source;

        mView = view;
        mView.setPresenter(this);

        mSchedulerProvider = schedulerProvider;

    }

    @Override
    public void loadBasicInfo(boolean force) {
        Disposable subscription =
                mSource.getBasicInfo()
                        .subscribeOn(mSchedulerProvider.computation())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(
                                // OnNext
                                new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        mView.showInfo(s);
                                    }
                                },
                                // OnError
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.showLoadingError();
                                    }
                                }
                        );
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {
        loadBasicInfo(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
