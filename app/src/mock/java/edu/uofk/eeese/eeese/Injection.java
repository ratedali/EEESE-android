/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.data.source.MockDataRepository;
import edu.uofk.eeese.eeese.home.HomeContract;
import edu.uofk.eeese.eeese.home.TestingHomePresenter;
import edu.uofk.eeese.eeese.projects.ProjectsContract;
import edu.uofk.eeese.eeese.projects.TestingProjectsPresenter;
import edu.uofk.eeese.eeese.util.EspressoIdlingResource;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import edu.uofk.eeese.eeese.util.schedulers.ImmediateSchedulerProvider;

public final class Injection {

    public static DataRepository provideDataRepository(Context context) {
        return MockDataRepository.getInstance();
    }

    public static BaseSchedulerProvider provideSchedulerProvider() {
        return ImmediateSchedulerProvider.getInstance();
    }

    public static CountingIdlingResource provideCountingIdlingResource() {
        return EspressoIdlingResource.getInstance();
    }

    public static HomeContract.Presenter provideHomePresenter(@NonNull Context context,
                                                              @NonNull HomeContract.View view) {
        return new TestingHomePresenter(provideDataRepository(context),
                view,
                provideSchedulerProvider(),
                provideCountingIdlingResource());
    }


    public static ProjectsContract.Presenter provideProjectsPresenter(@NonNull Context context,
                                                                      @NonNull ProjectsContract.View view) {
        return new TestingProjectsPresenter(provideDataRepository(context),
                view,
                provideSchedulerProvider(),
                provideCountingIdlingResource());
    }
}
