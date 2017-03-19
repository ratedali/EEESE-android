/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.EEESEapp;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.util.ActivityUtils;
import edu.uofk.eeese.eeese.util.OffsetItemDecorator;
import edu.uofk.eeese.eeese.util.ViewUtils;

public class EventsActivity extends AppCompatActivity implements EventsContract.View {


    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    public NavigationView mNavView;

    @BindView(R.id.content_view)
    public View mContentView;
    @BindView(R.id.appbar)
    public AppBarLayout mAppBar;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.events_list)
    public RecyclerView mEventsList;

    @BindString(R.string.transitionname_toolbar)
    public String toolbarTransitionName;
    @Inject
    public EventsContract.Presenter mPresenter;

    @BindDimen(R.dimen.event_margin_vertical)
    public int vertical_offset;
    @BindDimen(R.dimen.event_margin_horizontal)
    public int horizontal_offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ((EEESEapp) getApplication()).getAppComponent()
                .eventsComponent(new EventsModule(this))
                .inject(this);

        ActivityUtils.setEnterTransition(this, R.transition.events);
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_toolbar);
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_toolbar);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.events_title);
        }


        mSwipeRefreshLayout.setOnRefreshListener(this::reloadEvents);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mEventsList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mEventsList.addItemDecoration(
                new OffsetItemDecorator(vertical_offset, horizontal_offset, 1));

        setupDrawer(mNavView, mDrawerLayout);
        mPresenter.loadEvents(false);
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
    public void showLoadingIndicator() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingIndicator() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showEvents(List<Event> events) {
        mEventsList.swapAdapter(new EventsAdapter(events), false);
    }

    @Override
    public void showNoEvents() {
        // TODO: 3/15/17 Show no events screen
    }

    @Override
    public void showConnectionError() {
        Snackbar.make(mContentView, R.string.connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, view -> reloadEvents())
                .show();
    }

    @Override
    public void setPresenter(@NonNull EventsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void reloadEvents() {
        mPresenter.loadEvents(true);
    }

    private void setupDrawer(@NonNull NavigationView navView,
                             @NonNull final DrawerLayout drawer) {
        navView.setCheckedItem(R.id.nav_events);
        final Activity source = this;
        navView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            drawer.closeDrawers();
            Class<? extends Activity> targetActivity = ActivityUtils.getTargetActivity(item);
            if (targetActivity != null && !targetActivity.equals(source.getClass())) {
                Intent intent = new Intent(source, targetActivity);
                ActivityUtils.startActivityWithTransition(source, intent,
                        new Pair<>(mAppBar, toolbarTransitionName));
                supportFinishAfterTransition();
            }
            return true;
        });
    }
}
