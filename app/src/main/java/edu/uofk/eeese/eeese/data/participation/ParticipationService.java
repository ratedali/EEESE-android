/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.participation;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.List;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import edu.uofk.eeese.eeese.data.participation.ApplicationNotCompletedException.ApplicationError;

@ApplicationScope
public class ParticipationService {
    private static final String REQUEST_PATH = "formResponse";

    private static final String FULLNAME_FIELD = "entry.669981793";
    private static final String PHONENUMBER_FIELD = "entry.1420241112";
    private static final String EMAIL_FIELD = "entry.857967662";
    private static final String UNIVERSITY_FIELD = "entry.2048203842";
    private static final String BATCH_FIELD = "entry.749412383";
    private static final String[] PROJECT_FIELDS = {
            "entry.234522273",
            "entry.347236303",
            "entry.2011104471",
            "entry.363583290",
            "entry.639843878"
    };
    private static final String[] PROJECTEXP_FIELDS = {
            "entry.1453521246",
            "entry.530958489",
            "entry.1857139979",
            "entry.1627354120",
            "entry.511472892"
    };

    private HttpUrl mFormUrl;
    private OkHttpClient mHttpClient;

    @Inject
    public ParticipationService(String formUrl, OkHttpClient httpClient) {
        mFormUrl = new HttpUrl.Builder()
                .host(formUrl)
                .addPathSegment(REQUEST_PATH)
                .build();
        mHttpClient = httpClient;
    }

    Completable apply(final ApplyBody application) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Request request = new Request.Builder()
                        .url(mFormUrl)
                        .post(getFormBodyOf(application))
                        .build();

                Response response = mHttpClient.newCall(request)
                        .execute();

                if (response.isSuccessful()) {
                    e.onComplete();
                } else {
                    @ApplicationError
                    int errorCode = ApplicationNotCompletedException.UNKNOWN;

                    if (response.code() >= 500 && response.code() < 600) {
                        errorCode = ApplicationNotCompletedException.SERVER_ERROR;
                    }
                    e.onError(new ApplicationNotCompletedException("Network error", errorCode));
                }
            }
        });
    }

    private FormBody getFormBodyOf(ApplyBody application) {
        FormBody.Builder formBuilder =
                new FormBody.Builder()
                        .add(FULLNAME_FIELD, application.fullName)
                        .add(PHONENUMBER_FIELD, application.phoneNumber)
                        .add(EMAIL_FIELD, application.emailAddress)
                        .add(UNIVERSITY_FIELD, application.university)
                        .add(BATCH_FIELD, application.batch);
        for (int i = 0; i < PROJECT_FIELDS.length; ++i) {
            Pair<Project, String> projectExpPair = application.projects.get(i);
            String applicationName = getApplicationName(projectExpPair.first);
            String experience = projectExpPair.second;
            formBuilder.add(PROJECT_FIELDS[i], applicationName);
            formBuilder.add(PROJECTEXP_FIELDS[i], experience);
        }
        return formBuilder.build();
    }

    //  until I figure out which component should be responsible for
    // the application name of the projects, I will just use this
    private String getApplicationName(Project project) {
        //TODO: 2/4/17 Figure out which component should be responsible for the application names
        return project.getName();
    }

    public static final class ApplyBody {
        @NonNull
        public final String fullName;
        @NonNull
        public final String phoneNumber;
        @NonNull
        public final String emailAddress;
        @NonNull
        public final String university;
        @NonNull
        public final String batch;
        @NonNull
        public final List<Pair<Project, String>> projects;

        ApplyBody(@NonNull String fullName,
                  @NonNull String phoneNumber,
                  @NonNull String emailAddress,
                  @NonNull String university,
                  @NonNull String batch,
                  @NonNull List<Pair<Project, String>> projcts) {
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.emailAddress = emailAddress;
            this.university = university;
            this.batch = batch;
            this.projects = projcts;
        }

        public static final class Builder {
            private String fullName;
            private String phoneNumber;
            private String emailAddress;
            private String university;
            private String batch;
            private List<Pair<Project, String>> projects;

            public Builder() {

            }

            public Builder fullName(@NonNull String fullName) {
                this.fullName = fullName;
                return this;
            }

            public Builder phoneNumber(@NonNull String phoneNumber) {
                this.phoneNumber = phoneNumber;
                return this;
            }

            public Builder emailAddress(@NonNull String emailAddress) {
                this.emailAddress = emailAddress;
                return this;
            }

            public Builder university(String university) {
                this.university = university;
                return this;
            }

            public Builder batch(String batch) {
                this.batch = batch;
                return this;
            }

            public Builder projects(List<Pair<Project, String>> projects) {
                this.projects = projects;
                return this;
            }

            public ApplyBody build() {
                if (fullName == null
                        || phoneNumber == null
                        || emailAddress == null
                        || university == null
                        || batch == null
                        || projects == null) {
                    throw new IllegalStateException("Some of the parameters are not set");
                }
                return new ApplyBody(fullName, phoneNumber, emailAddress,
                        university, batch, projects);
            }
        }
    }
}
