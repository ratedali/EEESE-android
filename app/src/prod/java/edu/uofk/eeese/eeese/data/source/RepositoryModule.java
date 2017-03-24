/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.source;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.sync.SyncManager;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;

@Module
public class RepositoryModule {

    @Provides
    @ApplicationScope
    Repository<Event> provideEventsRepository(Context context, SyncManager syncManager) {
        return new EventsRepository(context, syncManager);
    }

    @Provides
    @ApplicationScope
    Repository<Project> provideProjectsRepository(Context context, SyncManager syncManager) {
        return new ProjectsRepository(context, syncManager);
    }
}
