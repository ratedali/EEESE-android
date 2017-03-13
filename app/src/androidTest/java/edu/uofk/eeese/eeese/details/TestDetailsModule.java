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

import android.support.test.espresso.idling.CountingIdlingResource;

import dagger.Module;
import dagger.Provides;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.di.categories.Cache;
import edu.uofk.eeese.eeese.di.scopes.ActivityScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;

@Module
public class TestDetailsModule {
    private String mProjectId;
    private DetailsContract.View mView;

    public TestDetailsModule(String projectId, DetailsContract.View view) {
        mProjectId = projectId;
        mView = view;
    }

    @Provides
    DetailsContract.View provideView() {
        return mView;
    }

    @Provides
    @ActivityScope
    DetailsContract.Presenter providePresenter(@Cache BaseDataRepository source,
                                               BaseSchedulerProvider schedulerProvider,
                                               DetailsContract.View view,
                                               CountingIdlingResource idlingResource) {
        return new TestingDetailsPresenter(source, view, schedulerProvider, mProjectId,
                idlingResource);
    }
}
