/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.about;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.di.scopes.ActivityScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@ActivityScope
public class AboutPresenter implements AboutContract.Presenter {

    private DataRepository mSource;
    private AboutContract.View mView;
    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeDisposable mSubscriptions;

    @Inject
    public AboutPresenter(@NonNull DataRepository source,
                          @NonNull AboutContract.View view,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        mSource = source;
        mView = view;
        mSchedulerProvider = schedulerProvider;
        mSubscriptions = new CompositeDisposable();
    }


    private Observable<Bitmap> galleryBitmap() {
        return mView.getGalleryViewSize().subscribeOn(mSchedulerProvider.ui())
                .flatMap(new Function<Pair<Integer, Integer>, Observable<Bitmap>>() {
                    @Override
                    public Observable<Bitmap> apply(Pair<Integer, Integer> widthHeightPair) throws Exception {
                        int width = widthHeightPair.first;
                        int height = widthHeightPair.second;
                        return mSource.getGalleryImageBitmap(width, height);
                    }
                }).subscribeOn(mSchedulerProvider.io());
    }

    private Observable<Pair<Bitmap, Palette>> galleryBitmapWithPalette() {
        return galleryBitmap()
                .map(new Function<Bitmap, Pair<Bitmap, Palette>>() {
                    @Override
                    public Pair<Bitmap, Palette> apply(Bitmap bitmap) throws Exception {
                        return new Pair<>(
                                bitmap,
                                Palette
                                        .from(bitmap)
                                        .generate()
                        );
                    }
                }).subscribeOn(
                        mSchedulerProvider.computation()
                );

    }

    @Override
    public void showProjectsGallery() {

    }

    @Override
    public void subscribe() {
        mView.setPresenter(this);
        // TODO: 1/21/17 Add error handling if the image or the swatch cannot be loaded
        Disposable subscription =
                galleryBitmapWithPalette()
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(
                                // onNext, show the image on the view
                                new Consumer<Pair<Bitmap, Palette>>() {
                                    @Override
                                    public void accept(Pair<Bitmap, Palette> bitmapSwatchPair) throws Exception {
                                        Bitmap bitmap = bitmapSwatchPair.first;
                                        Palette palette = bitmapSwatchPair.second;
                                        mView.showGalleryImage(bitmap, palette);
                                    }
                                }
                        );
        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
