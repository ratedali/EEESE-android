/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.backend

import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.ProjectCategory
import edu.uofk.eeese.eeese.data.backend.ServerContract.Events
import edu.uofk.eeese.eeese.data.backend.ServerContract.Projects
import io.reactivex.Observable
import io.reactivex.Single

class ApiWrapper(private val api: BackendApi) {

    fun projects(): Single<List<Project>> = api.projects()
            .flatMapObservable({ Observable.fromIterable(it) })
            .map({ Projects.project(it) })
            .toList()


    fun project(id: String): Single<Project> = api.project(id)
            .map({ ServerContract.Projects.project(it) })

    fun projects(category: ProjectCategory): Single<List<Project>> =
            api.projects(Projects.category(category))
                    .flatMapObservable({ Observable.fromIterable(it) })
                    .map({ Projects.project(it) })
                    .toList()


    fun event(id: String): Single<Event> = api.event(id)
            .map({ Events.event(it) })


    fun events(): Single<List<Event>> = api.events()
            .flatMapObservable({ Observable.fromIterable(it) })
            .map({ Events.event(it) })
            .toList()

}
