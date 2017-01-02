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

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.Injection;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.R2;
import edu.uofk.eeese.eeese.util.ViewUtils;

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener {

    @BindView(R2.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R2.id.nav_view)
    public NavigationView navView;

    private HomeContract.Presenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        HomeContract.View homeView = (HomeContract.View) getSupportFragmentManager().findFragmentById(R.id.home_fragment);
        if (homeView != null) {
            homePresenter = Injection.provideHomePresenter(this, homeView);
        }

        ViewUtils.setupDrawerListener(navView, mDrawerLayout, this);

    }

    @Override
    public void openDrawer() {
        ViewUtils.openDrawer(mDrawerLayout);
    }
}
