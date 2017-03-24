/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.transitionseverywhere.TransitionManager
import edu.uofk.eeese.eeese.EEESEapp
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.util.ActivityUtils
import edu.uofk.eeese.eeese.util.OffsetItemDecorator
import edu.uofk.eeese.eeese.util.ViewUtils
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.events_content.*
import java.util.*
import javax.inject.Inject

class EventsActivity : AppCompatActivity(), EventsContract.View {


    private var eventLocationClicks: Disposable = Disposables.disposed()
    private var eventsAdapter: EventsAdapter = EventsAdapter()

    @Inject lateinit var eventsPresenter: EventsContract.Presenter

    private val vertical_offset =
            lazy { resources.getDimensionPixelSize(R.dimen.event_margin_vertical) }
    private val horizontal_offset =
            lazy { resources.getDimensionPixelSize(R.dimen.event_margin_horizontal) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        (application as EEESEapp)
                .appComponent
                .eventsComponent(EventsModule(this))
                .inject(this)

        ActivityUtils.setEnterTransition(this, R.transition.events)
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_toolbar)
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_toolbar)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.events_title)
        }


        swipe_refresh.setOnRefreshListener({
            swipe_refresh.isRefreshing = false
            this.reloadEvents()
        })
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)

        events_list.adapter = eventsAdapter
        events_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        events_list.addItemDecoration(
                OffsetItemDecorator(vertical_offset.value, horizontal_offset.value, 1))

        reload_button.setOnClickListener { reloadEvents() }

        setupDrawer(nav_view, drawer_layout)

        eventsPresenter.loadEvents(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ViewUtils.openDrawer(drawer_layout)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        eventsPresenter.subscribe()
        registerEventClicks()
    }

    override fun onPause() {
        super.onPause()
        eventsPresenter.unsubscribe()
        eventLocationClicks.dispose()
    }

    override fun showLoadingIndicator() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideLoadingIndicator() {
        swipe_refresh.isRefreshing = false
    }

    override fun showEvents(events: List<Event>) {
        eventsAdapter = EventsAdapter(events)
        events_list.swapAdapter(eventsAdapter, false)
        registerEventClicks()

        if (events_list.visibility != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(swipe_refresh)
        }
        events_list.visibility = View.VISIBLE
        error_view.visibility = View.GONE
    }

    override fun showNoEvents() {
        if (error_view.visibility != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(swipe_refresh)
        }
        events_list.visibility = View.GONE
        error_view.visibility = View.VISIBLE
    }

    override fun showConnectionError() {
        Snackbar.make(content_view, R.string.connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { reloadEvents() }
                .show()
    }

    override fun setPresenter(presenter: EventsContract.Presenter) {
        eventsPresenter = presenter
    }

    private fun registerEventClicks() {
        eventLocationClicks.dispose()
        eventLocationClicks = eventsAdapter.locationClicks()
                .subscribe { latLongPair ->
                    val locationIntent = Intent(Intent.ACTION_VIEW)
                    locationIntent.data = Uri.parse(
                            String.format(Locale.ENGLISH,
                                    "https://maps.google.com/maps?q=loc:%s,%s",
                                    latLongPair.first,
                                    latLongPair.second))
                    val resolve = packageManager.resolveActivity(locationIntent, 0)
                    if (resolve != null) {
                        startActivity(locationIntent)
                    } else {
                        Toast.makeText(this, R.string.no_location_app_available, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
    }

    private fun reloadEvents() {
        eventsPresenter.loadEvents(true)
    }

    private fun setupDrawer(navView: NavigationView,
                            drawer: DrawerLayout) {
        navView.setCheckedItem(R.id.nav_events)
        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer.closeDrawers()
            val targetActivity = ActivityUtils.getTargetActivity(it)
            if (targetActivity != null && targetActivity != this::class.java) {
                val intent = Intent(this, targetActivity)
                ActivityUtils.startActivityWithTransition(this, intent,
                        Pair<View, String>(appbar, getString(R.string.transitionname_toolbar)))
                supportFinishAfterTransition()
            }
            true
        }
    }
}
