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
import android.support.test.espresso.idling.CountingIdlingResource;
import android.util.Log;

import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;

public class TestingDetailsPresenter extends DetailsPresenter {

    private static final String TAG = TestingDetailsPresenter.class.getName();

    @NonNull
    private CountingIdlingResource mIdlingResource;

    public TestingDetailsPresenter(@NonNull BaseDataRepository source,
                                   @NonNull DetailsContract.View view,
                                   @NonNull BaseSchedulerProvider schedulerProvider,
                                   @NonNull String projectId,
                                   @NonNull CountingIdlingResource idlingResource) {
        super(source, view, schedulerProvider, projectId);
        mIdlingResource = idlingResource;
    }

    @Override
    public void loadDetails(boolean force) {
        Log.d(TAG, "loadDetails: incrementing idlingResource");
        mIdlingResource.increment();
        try {
            super.loadDetails(force);
        } finally {
            Log.d(TAG, "loadDetails: Decrementing IdlingResource");
            mIdlingResource.decrement();
            Log.d(TAG, "Idle now? " + mIdlingResource.isIdleNow());
        }
    }

    @Override
    public void applyForProject() {
        Log.d(TAG, "applyForProject: incrementing idlingResource");
        mIdlingResource.increment();
        try {
            super.applyForProject();
        } finally {
            Log.d(TAG, "applyForProject: Decrementing IdlingResource");
            mIdlingResource.decrement();
            Log.d(TAG, "Idle now? " + mIdlingResource.isIdleNow());
        }
    }
}
