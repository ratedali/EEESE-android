/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@Module
public class HTTPModule {
    @Provides
    @ApplicationScope
    OkHttpClient provideHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(context))
                .connectTimeout(2, TimeUnit.SECONDS)
                // 2 MB cache
                .cache(new Cache(context.getCacheDir(), 2_000_000))
                .build();
    }

    @Provides
    @ApplicationScope
    Picasso providePicassoInstance(Context context, OkHttpClient httpClient) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(httpClient))
                .indicatorsEnabled(true)
                .loggingEnabled(true)
                .build();
    }
}
