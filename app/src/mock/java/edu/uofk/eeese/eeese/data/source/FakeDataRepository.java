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


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@ApplicationScope
class FakeDataRepository implements BaseDataRepository {

    @NonNull
    private Context mContext;

    private List<Project> mProjects;
    private boolean thrownForProjects;

    private List<Event> mEvents;

    @DrawableRes
    private static final int[] mGalleryRes = {
            R.drawable.gallery_1,
            R.drawable.gallery_2,
            R.drawable.gallery_3,
            R.drawable.gallery_4,
            R.drawable.gallery_5,
            R.drawable.gallery_6,
            R.drawable.gallery_7
    };

    @Inject
    FakeDataRepository(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {

        mContext = context;

        int[] categories = new int[]{
                Project.POWER,
                Project.TELECOM,
                Project.ELECTRONICS_CONTROL,
                Project.SOFTWARE
        };
        mProjects = new LinkedList<>();
        for (int category : categories) {
            for (int i = 1; i <= 5; ++i)
                mProjects.add(
                        new Project.Builder(category + "-" + i, "Project " + i, "Head " + i,
                                category)
                                .withDesc("Project description")
                                .withPrerequisites(
                                        Arrays.asList("Prereq1", "Prereq2", "Prereq3", "Prereq5")
                                ).build());
        }
        thrownForProjects = false;

        mEvents = new LinkedList<>();
        for (int i = 0; i < 10; ++i) {
            Event.Builder builder = new Event.Builder(String.valueOf(i), "Event " + i)
                    .description("Event Description");
            if (i < 3) {
                builder.endDate(new DateTime());
            }
            if (i < 5) {
                builder.startDate(new DateTime());
            }
            if (i < 7) {
                builder.location("1123123", "21131231");
            }
            if (i < 9) {
                builder.imageUri(new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                        .authority(mContext
                                .getResources()
                                .getResourcePackageName(R.drawable.gallery_4))
                        .appendPath(mContext
                                .getResources()
                                .getResourceTypeName(R.drawable.gallery_4))
                        .appendPath(mContext
                                .getResources()
                                .getResourceEntryName(R.drawable.gallery_4))
                        .build());
            }
            mEvents.add(builder.build());
        }
    }

    @Override
    public Completable insertProject(Project project) {
        mProjects.add(project);
        return Completable.complete();
    }

    @Override
    public Completable insertProjects(List<Project> projects) {
        mProjects.addAll(projects);
        return Completable.complete();
    }

    @Override
    public Completable setProjects(List<Project> projects) {
        mProjects = projects;
        return Completable.complete();
    }

    @Override
    public Completable setProjects(List<Project> projects, @Project.ProjectCategory int category) {
        mProjects = Observable.fromIterable(mProjects)
                .filter(project -> project.getCategory() != category)
                .concatWith(Observable.fromIterable(projects))
                .toList()
                .blockingGet();
        return Completable.complete();
    }

    @Override
    public Completable clearProjects() {
        mProjects.clear();
        return Completable.complete();
    }

    @Override
    public Completable clearProjects(@Project.ProjectCategory int category) {
        mProjects = Observable.fromIterable(mProjects)
                .filter(project -> project.getCategory() != category)
                .toList()
                .blockingGet();
        return Completable.complete();
    }

    @NonNull
    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        if (!thrownForProjects) {
            thrownForProjects = true;
            return Single.error(new Exception());
        } else {
            return Single.just(mProjects);
        }
    }

    @NonNull
    @Override
    public Single<List<Project>> getProjectsWithCategory(boolean forceUpdate,
                                                         @Project.ProjectCategory
                                                         final int category) {
        return Observable.fromIterable(mProjects)
                .filter(project -> project.getCategory() == category).toList();
    }

    @Override
    public Single<Project> getProject(String projectId, boolean forceUpdate) {
        return Single.just(mProjects.get(Integer.parseInt(projectId) - 1));
    }

    @Override
    public Completable insertEvent(Event event) {
        mEvents.add(event);
        return Completable.complete();
    }

    @Override
    public Completable insertEvents(List<Event> events) {
        mEvents.addAll(events);
        return Completable.complete();
    }

    @Override
    public Completable setEvents(List<Event> events) {
        mEvents.clear();
        mEvents.addAll(events);
        return Completable.complete();
    }

    @Override
    public Completable clearEvents() {
        mEvents.clear();
        return Completable.complete();
    }

    @Override
    public Single<Event> getEvent(String eventId, boolean forceUpdate) {
        return Single.just(mEvents)
                .flattenAsObservable(events -> events)
                .filter(event -> event.getId() == eventId)
                .firstOrError();
    }

    @Override
    public Single<List<Event>> getEvents(boolean forceUpdate) {
        return Single.just(mEvents);
    }

    @Override
    public Single<Bitmap> getGalleryImageBitmap(final int width, final int height) {
        @DrawableRes final int galleryRes = mGalleryRes[new Random().nextInt(mGalleryRes.length)];
        return Single.fromCallable(() -> {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), galleryRes, options);
            return options;
        }).map(options -> {
            options.inSampleSize = 1;
            if (options.outHeight > height || options.outWidth > width) {
                int halfHeight = options.outHeight / 2;
                int halfWidth = options.outWidth / 2;
                while ((halfHeight / options.inSampleSize) > height &&
                        (halfWidth / options.inSampleSize) > width) {
                    options.inSampleSize *= 2;
                }
            }
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(mContext.getResources(), galleryRes, options);
        });
    }
}
