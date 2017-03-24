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

import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.source.ProjectWithId
import edu.uofk.eeese.eeese.data.source.ProjectsRepository
import edu.uofk.eeese.eeese.data.source.Repository
import edu.uofk.eeese.eeese.di.scopes.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityScope
class DetailsPresenter @Inject
constructor(private val source: Repository<Project>,
            private val view: DetailsContract.View,
            private val projectId: String) : DetailsContract.Presenter {

    private val mSubscriptions: CompositeDisposable = CompositeDisposable()

    init {
        view.setPresenter(this)
    }

    override fun loadDetails(force: Boolean) {
        val subscription = source.getOne(ProjectWithId(projectId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onSuccess
                        { view.showProjectInfo(it) },
                        // onError
                        { view.showInvalidProject() })
        mSubscriptions.add(subscription)
    }

    override fun applyForProject() {
        //TODO apply for the project
    }

    override fun subscribe() {}

    override fun unsubscribe() {
        mSubscriptions.clear()
    }
}
