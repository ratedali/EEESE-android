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

import dagger.Module;
import dagger.Provides;
import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.source.Repository;
import edu.uofk.eeese.eeese.di.scopes.ActivityScope;

@Module
public class EventsModule {
    private EventsContract.View view;

    public EventsModule(EventsContract.View view) {
        this.view = view;
    }

    @Provides
    EventsContract.View provideView() {
        return view;
    }

    @Provides
    @ActivityScope
    EventsContract.Presenter providePresenter(EventsContract.View view, Repository<Event> source) {
        return new EventsPresenter(view, source);
    }
}
