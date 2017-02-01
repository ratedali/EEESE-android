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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.EEESEapp;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.util.ActivityUtils;
import edu.uofk.eeese.eeese.util.ViewUtils;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class AboutActivity extends AppCompatActivity implements AboutContract.View {

    // Navigation
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    public NavigationView navView;

    // ActionBar views
    @BindView(R.id.appbar)
    public AppBarLayout appBar;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindString(R.string.transitionname_toolbar)
    public String toolbarTransitionName;

    // Content
    @BindView(R.id.gallery_card)
    public CardView mGalleryCard;
    @BindView(R.id.gallery_image)
    public ImageView mGalleryImage;
    @BindView(R.id.gallery_title)
    public TextView mGalleryTitle;
    @BindView(R.id.gallery_body)
    public TextView mGalleryBody;

    private Bitmap mGalleryBitmap;

    // Control Objects
    @Inject
    public AboutContract.Presenter mPresenter;
    private boolean mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActivityUtils.setEnterTransition(this, R.transition.home_enter);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_toolbar);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_toolbar);

        ((EEESEapp) getApplication())
                .getAppComponent()
                .aboutComponent(new AboutModule(this))
                .inject(this);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }

        setupDrawer(navView, mDrawerLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGalleryBitmap != null) {
            mGalleryBitmap.recycle();
            mGalleryBitmap = null;
        }
        if (mExit)
            finish();
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
    public Single<android.support.v4.util.Pair<Integer, Integer>> getGalleryViewSize() {
        return Single.create(new SingleOnSubscribe<android.support.v4.util.Pair<Integer, Integer>>() {
            @Override
            public void subscribe(final SingleEmitter<android.support.v4.util.Pair<Integer, Integer>> e) throws Exception {
                mGalleryImage.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = mGalleryImage.getMeasuredWidth();
                        int height = mGalleryImage.getMeasuredHeight();
                        e.onSuccess(new android.support.v4.util.Pair<>(width, height));
                    }
                });
            }
        });
    }

    private void setupDrawer(@NonNull NavigationView navView,
                             @NonNull final DrawerLayout drawer) {
        navView.setCheckedItem(R.id.nav_home);
        final Activity source = this;
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawer.closeDrawers();
                Class<? extends Activity> targetActivity = ActivityUtils.getTargetActivity(item);
                if (targetActivity != null && !targetActivity.equals(source.getClass())) {
                    Intent intent = new Intent(source, targetActivity);
                    ActivityUtils.startActivityWithTransition(source, intent,
                            new Pair<View, String>(appBar, toolbarTransitionName));
                    mExit = true;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ViewUtils.openDrawer(mDrawerLayout);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
