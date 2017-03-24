/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import dagger.Module;
import dagger.Provides;
import edu.uofk.eeese.eeese.BuildConfig;
import edu.uofk.eeese.eeese.di.categories.BackendApiUrl;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class BackendModule {

    @Provides
    @BackendApiUrl
    String provideBackendApiUri() {
        return BuildConfig.BACKEND_API_URI;
    }

    @Provides
    @ApplicationScope
    Gson provideJSONParser() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class,
                        new ServerContract.Events.EventDateDeserializer())
                .create();
    }

    @Provides
    @ApplicationScope
    Retrofit provideRetrofit(OkHttpClient httpClient,
                             Gson jsonParser,
                             @BackendApiUrl String backendApiUri) {
        return new Retrofit.Builder()
                .baseUrl(backendApiUri)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(jsonParser))
                .build();
    }

    @Provides
    BackendApi provideBackendApi(Retrofit retrofit) {
        return retrofit.create(BackendApi.class);
    }

    @Provides
    ApiWrapper provideBackendApiWrapper(BackendApi api) {
        return new ApiWrapper(api);
    }
}
