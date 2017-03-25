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

import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.ProjectCategory
import edu.uofk.eeese.eeese.data.source.ProjectWithCategory
import edu.uofk.eeese.eeese.data.source.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class ProjectsPresenter @Inject
constructor(private val source: Repository<Project>,
            private val view: ProjectsContract.View,
            private val category: ProjectCategory) : ProjectsContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun getCategory(): ProjectCategory {
        return category
    }

    override fun loadProjects(force: Boolean) {
        if (force) {
            view.setLoadingIndicator(true)
            val sync = source.sync().subscribe()
            subscriptions.add(sync)
        }
        val subscription = source.get(ProjectWithCategory(category))
                .subscribeOn(Schedulers.io())
                // sort by name
                .map({ projects ->
                    projects.toList().sortedBy { it.name }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach { view.setLoadingIndicator(false) }
                .subscribe(
                        // OnSuccess
                        {
                            if (it.isNotEmpty()) view.showProjects(it)
                            else view.showNoProjects()
                        },
                        // OnError
                        {
                            view.showConnectionError()
                        })
        subscriptions.add(subscription)
    }

    override fun openProjectDetails(project: Project) {
        view.showProjectDetails(project.id)
    }

    override fun subscribe() {}

    override fun unsubscribe() {
        subscriptions.clear()
    }
}
