/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.details

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.uofk.eeese.eeese.EEESEapp
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.details_content.*
import kotlinx.android.synthetic.main.prereq_list_item.view.*
import javax.inject.Inject


class DetailsActivity : AppCompatActivity(), DetailsContract.View {

    private var mProjectId: String? = null


    companion object {
        val PROJECT_ID_KEY = "edu.uofk.eeese.eeese.PROJECT_ID"
    }

    @Inject
    lateinit var mPresenter: DetailsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val intent = intent
        mProjectId = intent.getStringExtra(PROJECT_ID_KEY)

        ActivityUtils.setEnterTransition(this, R.transition.details)
        ActivityUtils.setSharedElementEnterTransition(this, R.transition.shared_projectcard)
        ActivityUtils.setSharedElementExitTransition(this, R.transition.shared_projectcard)

        (application as EEESEapp).appComponent
                .detailsComponent(DetailsModule(mProjectId, this))
                .inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prereq_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mPresenter.loadDetails(false)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.unsubscribe()
    }

    override fun showProjectInfo(project: Project) {
        project_name.text = project.name
        project_head.text = project.projectHead
        if (TextUtils.isEmpty(project.desc)) {
            project_desc.text = project.desc
        } else {
            desc_card.visibility = View.GONE
        }
        if (project.prerequisites.isNotEmpty()) {
            prereq_list.swapAdapter(PrereqAdapter(project.prerequisites), false)
        } else {
            prereq_card.visibility = View.GONE
        }
    }

    override fun showInvalidProject() {

    }

    override fun setPresenter(presenter: DetailsContract.Presenter) {
        mPresenter = presenter
    }

}
/**
 * Adapter for the prerequisites list, simply puts the prerequisite prereq in a TextView
 */
internal class PrereqAdapter(private val mPrerequisites: List<String>) :
        RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.prereq_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.prereq.text = mPrerequisites[position]
    }

    override fun getItemCount(): Int {
        return mPrerequisites.size
    }
}
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val prereq: TextView = itemView.prereq_textview
}
