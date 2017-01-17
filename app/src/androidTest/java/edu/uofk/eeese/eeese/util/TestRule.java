/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.util;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import edu.uofk.eeese.eeese.AppComponent;
import edu.uofk.eeese.eeese.EEESEapp;

public class TestRule<T extends Activity> extends IntentsTestRule<T> {
    private AppComponent mComponent;

    public TestRule(Class<T> activityClass,
                    AppComponent component) {
        super(activityClass);
        mComponent = component;
    }

    public TestRule(Class<T> activityClass, boolean initialTouchMode,
                    AppComponent component) {
        super(activityClass, initialTouchMode);
        mComponent = component;
    }

    public TestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity,
                    AppComponent component) {
        super(activityClass, initialTouchMode, launchActivity);
        mComponent = component;
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        ((EEESEapp) InstrumentationRegistry.getTargetContext().getApplicationContext())
                .setAppComponent(mComponent);
    }
}
