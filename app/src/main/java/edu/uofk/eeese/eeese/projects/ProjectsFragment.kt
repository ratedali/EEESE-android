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

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.transitionseverywhere.TransitionManager
import edu.uofk.eeese.eeese.EEESEapp
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.ProjectCategory
import edu.uofk.eeese.eeese.projects.ProjectsFragment.OnProjectSelectedListener
import edu.uofk.eeese.eeese.util.OffsetItemDecorator
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.fragment_projects.*
import javax.inject.Inject


/**
 * A fragment that displays a list of projects with a specific ProjectCategory
 * [OnProjectSelectedListener] interface to handle project clicks
 */
class ProjectsFragment : Fragment(), ProjectsContract.View {
    companion object {
        private val CATEGORY_KEY = "edu.uofk.eeese.eeese.ProjectsFragment.CATEGORY"

        private const val CATEGORY_SOFTWARE = 0
        private const val CATEGORY_POWER = 1
        private const val CATEGORY_TELECOM = 2
        private const val CATEGORY_ELECTRONICS_CONTROL = 3

        fun getInstance(category: ProjectCategory): ProjectsFragment {
            val fragment = ProjectsFragment()
            val arguments = Bundle()
            arguments.putInt(CATEGORY_KEY,
                    when (category) {
                        ProjectCategory.SOFTWARE -> CATEGORY_SOFTWARE
                        ProjectCategory.POWER -> CATEGORY_POWER
                        ProjectCategory.TELECOM -> CATEGORY_TELECOM
                        ProjectCategory.ELECTRONICS_CONTROL -> CATEGORY_ELECTRONICS_CONTROL
                    })
            fragment.arguments = arguments
            return fragment
        }

        private fun category(category: Int) = when (category) {
            CATEGORY_SOFTWARE -> ProjectCategory.SOFTWARE
            CATEGORY_POWER -> ProjectCategory.POWER
            CATEGORY_TELECOM -> ProjectCategory.TELECOM
            CATEGORY_ELECTRONICS_CONTROL -> ProjectCategory.ELECTRONICS_CONTROL
            else -> throw IllegalArgumentException("unknown category code '$category'")
        }
    }

    private lateinit var category: ProjectCategory

    private var projectClicks: Disposable = Disposables.disposed()
    private var selectedProject: View? = null

    lateinit private var projectsAdapter: ProjectsAdapter

    private var mListener: OnProjectSelectedListener? = null

    @Inject lateinit var projectsPresenter: ProjectsContract.Presenter

    /*
     * As always, the int read from the arguments bundle will always be
     * a legal value, because it is always set to a legal value in the first place
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            category = category(arguments.getInt(CATEGORY_KEY))
        }
        (activity.applicationContext as EEESEapp).appComponent
                .projectsComponent(ProjectsModule(this, category))
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_projects, container, false)

        // to identify the fragment with its category
        rootView.setTag(R.string.tag_projectslist, projectsPresenter.getCategory())
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh.setOnRefreshListener { reloadProjects() }
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)

        projectsAdapter = ProjectsAdapter()

        val numberOfColumns = resources.getInteger(R.integer.number_of_columns)
        projects_list.layoutManager = GridLayoutManager(context, numberOfColumns)
        projects_list.adapter = projectsAdapter
        projects_list.addItemDecoration(OffsetItemDecorator(
                resources.getDimensionPixelSize(R.dimen.project_margin_vertical),
                resources.getDimensionPixelSize(R.dimen.project_margin_horizontal),
                numberOfColumns))


        reload_button.setOnClickListener { reloadProjects() }
        projectsPresenter.loadProjects(false)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnProjectSelectedListener?) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnProjectSelectedListener")
        }
    }

    override fun onResume() {
        super.onResume()
        projectsPresenter.subscribe()
        registerProjectClicks()
    }

    override fun onPause() {
        super.onPause()
        projectsPresenter.unsubscribe()
        projectClicks.dispose()
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun showProjects(projects: List<Project>) {
        projectsAdapter = ProjectsAdapter(projects)
        projectClicks.dispose()
        registerProjectClicks()

        projects_list.swapAdapter(projectsAdapter, false)
        if (projects_list.visibility != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(swipe_refresh)
        }
        projects_list.visibility = View.VISIBLE
        error_view.visibility = View.GONE
    }

    override fun showProjectDetails(projectId: String) {
        mListener!!.onProjectSelected(projectId, selectedProject!!)
    }

    override fun showNoProjects() {
        if (error_view.visibility != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(swipe_refresh)
        }
        error_view.visibility = View.VISIBLE
        projects_list.visibility = View.GONE

    }

    override fun setLoadingIndicator(visibility: Boolean) {
        swipe_refresh.isRefreshing = visibility
    }

    override fun showConnectionError() {
        Snackbar.make(swipe_refresh, R.string.connection_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun setPresenter(presenter: ProjectsContract.Presenter) {
        projectsPresenter = presenter
    }

    fun reloadProjects() {
        projectsPresenter.loadProjects(true)
    }

    private fun registerProjectClicks() {
        projectClicks = projectsAdapter
                .projectClicks()
                .subscribe {
                    selectedProject = it.projectCard
                    projectsPresenter.openProjectDetails(it.project)
                }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     */
    interface OnProjectSelectedListener {
        fun onProjectSelected(projectId: String, projectView: View)
    }
}
