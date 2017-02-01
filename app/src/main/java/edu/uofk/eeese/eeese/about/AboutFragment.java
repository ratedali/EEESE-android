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
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.EEESEapp;
import edu.uofk.eeese.eeese.R;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment implements AboutContract.View {

    @BindView(R.id.gallery_card)
    public CardView mGalleryCard;
    @BindView(R.id.gallery_image)
    public ImageView mGalleryImage;
    @BindView(R.id.gallery_title)
    public TextView mGalleryTitle;
    @BindView(R.id.gallery_body)
    public TextView mGalleryBody;

    @Inject
    public AboutContract.Presenter mPresenter;

    private Bitmap mGalleryBitmap;

    public AboutFragment() {
    }

    @NonNull
    public static AboutFragment getInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((EEESEapp) getActivity().getApplication())
                .getAppComponent()
                .aboutComponent(new AboutModule(this))
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGalleryBitmap != null) {
            mGalleryBitmap.recycle();
            mGalleryBitmap = null;
        }
    }

    @Override
    public void setPresenter(@NonNull AboutContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void openGallery() {
        // TODO: 1/21/17 show gallery webview
    }

    @SuppressWarnings("deprecation")
    @Override
    public void showGalleryImage(@Nullable Bitmap imageBitmap, @NonNull Palette palette) {
        if (mGalleryBitmap != null) {
            mGalleryBitmap.recycle();
        }
        mGalleryBitmap = imageBitmap;
        mGalleryImage.setImageBitmap(mGalleryBitmap);

        Palette.Swatch swatch = palette.getLightVibrantSwatch();
        @ColorInt int backgroundColor = getResources().getColor(android.R.color.white);
        @ColorInt int titleColor = getResources().getColor(R.color.colorPrimaryText);
        @ColorInt int bodyColor = getResources().getColor(R.color.colorSecondaryText);
        if (swatch != null) {
            backgroundColor = ColorUtils.setAlphaComponent(swatch.getRgb(), 255);
            titleColor = ColorUtils.setAlphaComponent(swatch.getTitleTextColor(), 255);
            bodyColor = ColorUtils.setAlphaComponent(swatch.getBodyTextColor(), 255);
        }

        mGalleryCard.setCardBackgroundColor(backgroundColor);
        mGalleryTitle.setTextColor(titleColor);
        mGalleryBody.setTextColor(bodyColor);
    }

    @Override
    public Single<Pair<Integer, Integer>> getGalleryViewSize() {
        return Single.create(new SingleOnSubscribe<Pair<Integer, Integer>>() {
            @Override
            public void subscribe(final SingleEmitter<Pair<Integer, Integer>> e) throws Exception {
                mGalleryImage.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = mGalleryImage.getMeasuredWidth();
                        int height = mGalleryImage.getMeasuredHeight();
                        e.onSuccess(new Pair<>(width, height));
                    }
                });
            }
        });
    }
}
