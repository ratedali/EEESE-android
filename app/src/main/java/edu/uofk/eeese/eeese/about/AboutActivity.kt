/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.github.florent37.picassopalette.PicassoPalette
import com.squareup.picasso.Picasso
import edu.uofk.eeese.eeese.EEESEapp
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.sync.SyncManager
import edu.uofk.eeese.eeese.util.ActivityUtils
import edu.uofk.eeese.eeese.util.ViewUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.project_gallery_card.*
import javax.inject.Inject

class AboutActivity : AppCompatActivity(), AboutContract.View {


    companion object {
        private val TAG = AboutActivity::class.java.name
        private val PROJECT_GALLERY_URL = "http://eeese.uofk.edu#project"
    }

    @Inject lateinit var aboutPresenter: AboutContract.Presenter
    @Inject lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as EEESEapp)
                .appComponent
                .aboutComponent(AboutModule(this))
                .inject(this)

        syncManager.setupSync()

        ActivityUtils.setEnterTransition(this, R.transition.home_enter)
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_toolbar)
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_toolbar)

        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.app_name)
        }

        setupDrawer(nav_view, drawer_layout)

        aboutPresenter.loadGalleryImage()
    }

    override fun onResume() {
        super.onResume()
        aboutPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        aboutPresenter.unsubscribe()
    }

    override fun setPresenter(presenter: AboutContract.Presenter) {
        aboutPresenter = presenter
    }

    override fun openGallery() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PROJECT_GALLERY_URL))
        startActivity(intent)
    }

    override fun showGalleryImage(imageUri: Uri) {
        Picasso.with(this)
                .load(imageUri)
                .into(gallery_image,
                        PicassoPalette.with(imageUri.toString(), gallery_image)
                                .use(PicassoPalette.Profile.VIBRANT)
                                .intoBackground(gallery_card)
                                .intoTextColor(gallery_title,
                                        PicassoPalette.Swatch.TITLE_TEXT_COLOR)
                                .intoTextColor(gallery_body,
                                        PicassoPalette.Swatch.BODY_TEXT_COLOR))
    }

    private fun setupDrawer(navView: NavigationView,
                            drawer: DrawerLayout) {
        navView.setCheckedItem(R.id.nav_about)
        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer.closeDrawers()
            val targetActivity = ActivityUtils.getTargetActivity(it)
            if (targetActivity != null && targetActivity != this::class.java) {
                val intent = Intent(this, targetActivity)
                ActivityUtils.startActivityWithTransition(this, intent,
                        android.util.Pair<View, String>(appbar,
                                getString(R.string.transitionname_toolbar)))
            }
            true
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ViewUtils.openDrawer(drawer_layout)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun galleryCardClicked(view: View) {
        aboutPresenter.galleryCardClicked()
    }
}
