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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.Project
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.project_list_item.view.*

internal class ProjectsAdapter
constructor(private val projects: List<Project> = emptyList<Project>()) :
        RecyclerView.Adapter<ViewHolder>() {

    private val projectClicksSubject = PublishSubject.create<ProjectClick>()

    fun projectClicks(): Observable<ProjectClick> = projectClicksSubject


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.project_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]

        RxView.clicks(holder.card)
                .map { holder.adapterPosition }
                .map { clickPosition -> ProjectClick(projects[clickPosition], holder.card) }
                .subscribeWith(projectClicksSubject)

        holder.name.text = project.name
        holder.head.text = project.projectHead
    }

    override fun getItemCount(): Int {
        return projects.size
    }
}

data class ProjectClick(var project: Project, var projectCard: View)

class ViewHolder(var card: View) : RecyclerView.ViewHolder(card) {
    val name: TextView = itemView.project_name
    val head: TextView = itemView.project_head
}
