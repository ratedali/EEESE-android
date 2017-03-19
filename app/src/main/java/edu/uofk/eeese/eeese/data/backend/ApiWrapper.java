/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.backend;

import java.util.List;

import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.Project;
import io.reactivex.Observable;
import io.reactivex.Single;

public class ApiWrapper {
    private final BackendApi mApi;

    public ApiWrapper(BackendApi api) {
        mApi = api;
    }

    public Single<List<Project>> projects() {
        return mApi.projects()
                .flatMapObservable(Observable::fromIterable)
                .map(ApiWrapper::projectFromJSON)
                .toList();
    }

    public Single<Project> project(String id) {
        return mApi.project(id)
                .map(ApiWrapper::projectFromJSON);
    }

    public Single<List<Project>> projects(@Project.ProjectCategory int category) {
        return mApi.projects(ProjectsJSONContract.getJSONCategory(category))
                .flatMapObservable(Observable::fromIterable)
                .map(ApiWrapper::projectFromJSON)
                .toList();
    }

    public Single<Event> event(String id) {
        return mApi.event(id)
                .map(ApiWrapper::eventFromJSON);
    }

    public Single<List<Event>> events() {
        return mApi.events()
                .flatMapObservable(Observable::fromIterable)
                .map(ApiWrapper::eventFromJSON)
                .toList();
    }

    private static Project projectFromJSON(ProjectsJSONContract.ProjectJSON projectJSON) {
        int category = ProjectsJSONContract.getCategory(projectJSON.category);
        String head = projectJSON.head == null ? "" : projectJSON.head;
        return new Project.Builder(projectJSON.id, projectJSON.name, head, category)
                .withPrerequisites(projectJSON.prereq)
                .withDesc(projectJSON.desc)
                .build();
    }

    private static Event eventFromJSON(EventsJSONContract.EventJSON eventJSON) {
        Event.Builder eventBuilder = new Event.Builder(eventJSON.id, eventJSON.name);
        eventBuilder.description(eventJSON.desc);

        if (eventJSON.location != null && !eventJSON.location.isEmpty()) {
            String[] coords = eventJSON.location.split(",");
            String longitude = coords[0];
            String latitude = coords[1];
            eventBuilder.location(longitude, latitude);
        }
        if (eventJSON.imageuri != null) {
            eventBuilder.imageUri(eventJSON.imageuri);
        }
        if (eventJSON.start != null) {
            eventBuilder.startDate(eventJSON.start);
        }
        if (eventJSON.end != null) {
            eventBuilder.endDate(eventJSON.end);
        }
        return eventBuilder.build();
    }
}
