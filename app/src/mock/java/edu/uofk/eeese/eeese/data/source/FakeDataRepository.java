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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

@ApplicationScope
class FakeDataRepository implements DataRepository {

    @NonNull
    private Context mContext;

    private List<Project> mProjects;
    private boolean thrown;

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

        mProjects = Arrays.asList(
                new Project.Builder("1", "Project 1", "Head 1")
                        .withDesc("The First Project").build(),
                new Project.Builder("2", "Project 2", "Head 2")
                        .withDesc("The Second Project").build(),
                new Project.Builder("3", "Project 3", "Head 3")
                        .withDesc("The Third Project").build(),
                new Project.Builder("4", "Project 4", "Head 4")
                        .withDesc("The Fourth Project").build()
        );

        thrown = false;
    }

    @Override
    public Observable<List<Project>> getProjects(boolean forceUpdate) {
        if (!thrown) {
            thrown = true;
            return Observable.error(new Exception());
        } else {
            return Observable.just(mProjects);
        }
    }

    @Override
    public Observable<Project> getProject(String projectId, boolean forceUpdate) {
        return Observable.just(mProjects.get(Integer.parseInt(projectId) - 1));
    }

    @Override
    public Observable<Bitmap> getGalleryImageBitmap(final int width, final int height) {
        @DrawableRes final int galleryRes = mGalleryRes[new Random().nextInt(mGalleryRes.length)];
        return Observable.fromCallable(new Callable<BitmapFactory.Options>() {
            @Override
            public BitmapFactory.Options call() throws Exception {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(mContext.getResources(), galleryRes, options);
                return options;
            }
        }).map(new Function<BitmapFactory.Options, Bitmap>() {
            @Override
            public Bitmap apply(BitmapFactory.Options options) throws Exception {
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
            }
        });
    }
}
