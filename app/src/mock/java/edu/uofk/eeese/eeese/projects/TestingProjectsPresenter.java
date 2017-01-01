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
import android.support.test.espresso.idling.CountingIdlingResource;
import android.util.Log;

import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.home.TestingHomePresenter;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;

public final class TestingProjectsPresenter extends ProjectsPresenter {
    private static final String TAG = TestingHomePresenter.class.getName();
    @NonNull
    private CountingIdlingResource mIdlingResource;

    public TestingProjectsPresenter(
            @NonNull DataRepository source,
            @NonNull ProjectsContract.View view,
            @NonNull BaseSchedulerProvider schedulerProvider,
            @NonNull CountingIdlingResource idlingResource) {

        super(source, view, schedulerProvider);
        mIdlingResource = idlingResource;
    }

    @Override
    public void loadProjects(boolean force) {
        Log.d(TAG, "Incrementing IdlingResource");
        mIdlingResource.increment();
        try {
            super.loadProjects(force);
        } finally {
            Log.d(TAG, "Decrementing IdlingResource");
            mIdlingResource.decrement();
            Log.d(TAG, "Idle now? " + mIdlingResource.isIdleNow());
        }
    }
}
