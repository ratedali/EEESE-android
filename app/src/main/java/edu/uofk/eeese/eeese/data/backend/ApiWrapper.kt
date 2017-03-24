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

import android.text.TextUtils
import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.Project
import io.reactivex.Observable
import io.reactivex.Single

class ApiWrapper(private val api: BackendApi) {

    fun projects(): Single<List<Project>> = api.projects()
            .flatMapObservable({ Observable.fromIterable(it) })
            .map({ projectFromJSON(it) })
            .toList()


    fun project(id: String): Single<Project> = api.project(id).map({ projectFromJSON(it) })

    fun projects(category: Int): Single<List<Project>> =
            api.projects(ProjectsJSONContract.getJSONCategory(category))
                    .flatMapObservable({ Observable.fromIterable(it) })
                    .map({ projectFromJSON(it) })
                    .toList()


    fun event(id: String): Single<Event> = api.event(id).map({ eventFromJSON(it) })


    fun events(): Single<List<Event>> = api.events()
            .flatMapObservable({ Observable.fromIterable(it) })
            .map({ eventFromJSON(it) })
            .toList()


    private fun projectFromJSON(projectJSON: ProjectsJSONContract.ProjectJSON): Project {
        val category = ProjectsJSONContract.getCategory(projectJSON.category)
        val head = projectJSON.head ?: ""
        return Project.Builder(projectJSON.id, projectJSON.name, head, category)
                .withPrerequisites(projectJSON.prereq)
                .withDesc(projectJSON.desc)
                .build()
    }

    private fun eventFromJSON(eventJSON: EventsJSONContract.EventJSON): Event {
        val eventBuilder = Event.Builder(eventJSON.id, eventJSON.name)
        eventBuilder.description(eventJSON.desc)

        if (!TextUtils.isEmpty(eventJSON.location)) {
            val coords = eventJSON.location
                    .split(",")
            val (longitude,latitude) = coords
            eventBuilder.location(longitude, latitude)
        }
        if (eventJSON.imageuri != null) {
            eventBuilder.imageUri(eventJSON.imageuri)
        }
        if (eventJSON.start != null) {
            eventBuilder.startDate(eventJSON.start)
        }
        if (eventJSON.end != null) {
            eventBuilder.endDate(eventJSON.end)
        }
        return eventBuilder.build()
    }
}
