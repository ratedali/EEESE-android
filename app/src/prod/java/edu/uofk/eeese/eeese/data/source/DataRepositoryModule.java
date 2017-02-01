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
import android.database.sqlite.SQLiteOpenHelper;

import dagger.Module;
import dagger.Provides;
import edu.uofk.eeese.eeese.di.categories.Cache;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.categories.Remote;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;

@Module
public class DataRepositoryModule {
    @Provides
    @ApplicationScope
    @Local
    BaseDataRepository provideLocalSource(Context context,
                                          SQLiteOpenHelper dbHelper,
                                          BaseSchedulerProvider schedulerProvider) {
        return new LocalDataRepository(context, dbHelper, schedulerProvider);
    }

    @Provides
    @ApplicationScope
    @Remote
    BaseDataRepository provideRemoteSource(Context context,
                                           BaseSchedulerProvider schedulerProvider) {
        return new RemoteDataRepository(context, schedulerProvider);
    }

    @Provides
    @ApplicationScope
    @Cache
    BaseDataRepository provideCacheSource(@Local BaseDataRepository local,
                                          @Remote BaseDataRepository remote) {
        return new DataRepository(local, remote);
    }
}
