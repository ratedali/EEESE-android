/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.Injection;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.util.ActivityUtils;
import edu.uofk.eeese.eeese.util.ViewUtils;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    // Navigation
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    public NavigationView navView;

    // ActionBar views
    @BindView(R.id.appbar)
    public AppBarLayout appBar;
    @BindView(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindString(R.string.transitionname_toolbar)
    public String toolbarTransitionName;
    @BindString(R.string.app_name)
    public String titleText;

    // Content
    @BindView(R.id.basic_info_textview)
    public TextView basicInfoTextView;

    private boolean mExit;

    private HomeContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActivityUtils.setEnterTransition(this, R.transition.home_enter);
        ActivityUtils.setExitTransition(this, R.transition.home_exit);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_toolbar);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_toolbar);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_expandedtoolbar);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                @DrawableRes int navDrawable;
                String title;
                boolean isCollapsed = Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange();
                if (isCollapsed) {
                    title = titleText;
                    navDrawable = R.drawable.ic_menu;
                } else {
                    title = "";
                    navDrawable = R.drawable.ic_menu_expandedtoolbar;
                }

                collapsingToolbar.setTitle(title);
                if (actionBar != null) {
                    actionBar.setHomeAsUpIndicator(navDrawable);
                }
            }
        });

        mPresenter = Injection.provideHomePresenter(this, this);

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
        if (mExit)
            finish();
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

    @Override
    public void showInfo(String basicInfo) {
        basicInfoTextView.setText(basicInfo);
    }

    @Override
    public void showLoadingError() {
        // TODO: 12/31/16 Show an error message
    }

    @Override
    public void setPresenter(@NonNull HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
