/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.projects

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.MenuItem
import android.view.View
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.details.DetailsActivity
import edu.uofk.eeese.eeese.util.ActivityUtils
import edu.uofk.eeese.eeese.util.ViewUtils
import kotlinx.android.synthetic.main.activity_projects.*


class ProjectsActivity : AppCompatActivity(), ProjectsFragment.OnProjectSelectedListener {


    companion object {
        private val APPLICATION_FORM_URL = "https://bit.ly/Projects-Participation"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        ActivityUtils.setEnterTransition(this, R.transition.projects_enter)
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_projectcard)
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_projectcard)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.projects)

        val projects = listOf(
                ProjectsFragment.getInstance(edu.uofk.eeese.eeese.data.ProjectCategory.POWER),
                ProjectsFragment.getInstance(edu.uofk.eeese.eeese.data.ProjectCategory.TELECOM),
                ProjectsFragment.getInstance(edu.uofk.eeese.eeese.data.ProjectCategory.SOFTWARE),
                ProjectsFragment.getInstance(edu.uofk.eeese.eeese.data.ProjectCategory.ELECTRONICS_CONTROL))
        val categories = listOf(
                getString(R.string.power_category),
                getString(R.string.telecom_category),
                getString(R.string.software_category),
                getString(R.string.electronics_control_category))

        viewpager.adapter = ProjectsPagerAdapter(
                supportFragmentManager,
                projects,
                categories)
        tablayout.setupWithViewPager(viewpager)

        fab.setOnClickListener { participate() }

        setupDrawer(nav_view, drawer_layout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ViewUtils.openDrawer(drawer_layout)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStop() {
        super.onStop()
    }

    private fun setupDrawer(navView: NavigationView,
                            drawer: DrawerLayout) {
        navView.setCheckedItem(R.id.nav_projects)
        drawer.closeDrawers()
        val source = this
        navView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawer.closeDrawers()
            val targetActivity = ActivityUtils.getTargetActivity(item)
            if (targetActivity != null && targetActivity != source::class.java) {
                val intent = Intent(source, targetActivity)
                ActivityUtils.startActivityWithTransition(source, intent,
                        Pair<View, String>(appbar, getString(R.string.transitionname_toolbar)))
            }
            true
        }
    }

    override fun onProjectSelected(projectId: String, projectView: View) {
        ActivityUtils.setTransitionName(projectView, getString(R.string.transitionname_projectcard))
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(DetailsActivity.PROJECT_ID_KEY, projectId)
        ActivityUtils.startActivityWithTransition(this, intent,
                Pair<View, String>(appbar, getString(R.string.transitionname_toolbar)),
                Pair<View, String>(projectView, getString(R.string.transitionname_projectcard)))
    }

    fun participate() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APPLICATION_FORM_URL))
        startActivity(intent)
    }

}

internal class ProjectsPagerAdapter(fm: FragmentManager,
                                    projectsFragments: List<ProjectsFragment>?,
                                    categories: List<String>?) : FragmentPagerAdapter(fm) {

    private val projectsFragments = projectsFragments ?: emptyList()
    private val categories = categories ?: emptyList()

    override fun getItem(position: Int) = projectsFragments[position]
    override fun getCount() = projectsFragments.size
    override fun getPageTitle(position: Int) = categories[position]

}
