/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.source;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.Project;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface BaseDataRepository {

    /*
     * Projects Section
     */

    /**
     * Add a project to the repository
     *
     * @param project the project to be added
     */
    Completable insertProject(Project project);

    /**
     * Add a list of projects to the repository
     * @param projects the projects to be added
     */
    Completable insertProjects(List<Project> projects);

    /**
     * Set the projects of the repository to a new list of projects.
     * this is equivalent to removing all projects, and then adding the given projects.
     * @param projects the new list of projects
     */
    Completable setProjects(List<Project> projects);

    /**
     * Set the projects with a category of the repository to a new list of projects.
     * <p>
     * This is equivalent to removing all the projects with the category, and then adding the given
     * projects.
     *
     * @param projects the new list of projects
     * @param category the category to set
     */
    Completable setProjects(List<Project> projects, @Project.ProjectCategory int category);

    /**
     * Remove all the projects from the repository
     */
    Completable clearProjects();

    /**
     * Remove all the projects with a category from that repository
     * @param category the category of the projects to be removed
     */
    Completable clearProjects(@Project.ProjectCategory int category);

    /**
     * Get a specific projects from the repository.
     * The returned {@link Single} will emit the {@link Project} when it is retrieved.
     * If the project does not exist it will emit an error.
     *
     * @param projectId   the id of the project
     * @param forceUpdate if the repository supports updating its data,
     *                    this specifies whether to force the update or not.
     */
    Single<Project> getProject(String projectId, boolean forceUpdate);

    /**
     * Get a list of all projects stored in the repository.
     * The returned {@link Single} will emit the list of {@link Project}s when they are retrieved,
     * if no projects exist it will emit an empty list
     * @param forceUpdate if the repository supports updating its data,
     *                    this specifies whether to force the update or not.
     */
    @NonNull
    Single<List<Project>> getProjects(boolean forceUpdate);

    /**
     * Get a list of all projects with a category stored in the repository.
     * The returned single will emit the list of projects when they are retrieved,
     * if no projects exist it will emit an empty list.
     * @param forceUpdate if the repository supports updating its data,
     *                    this specifies whether to force the update or not.
     * @param category the category of the projects returned
     */
    @NonNull
    Single<List<Project>> getProjectsWithCategory(boolean forceUpdate,
                                                  @Project.ProjectCategory int category);

    /**
     * Get an image from the projects gallery
     * @param width the wanted width of the image
     * @param height the wanted height of the image
     */
    Single<Bitmap> getGalleryImageBitmap(int width, int height);

    /*
     * Events Section
     */

    /**
     * Add an event to the repository.
     *
     * @param event the event to be added
     */
    Completable insertEvent(Event event);

    /**
     * Add a list of events to the repository
     *
     * @param events the events to be added
     */
    Completable insertEvents(List<Event> events);

    /**
     * Set the events of the repository to a new list of events
     *
     * @param events the new list of events
     */
    Completable setEvents(List<Event> events);

    /**
     * Remove all events from the repository.
     */
    Completable clearEvents();

    /**
     * Get a specific event from the repository.
     * The returned {@link Single} will emit the {@link Event} once it is retrieved.
     * If the event does not exist it will emit an error.
     *
     * @param eventId     the id of the event
     * @param forceUpdate if the repository supports updating its data,
     *                    this specifies whether to force the update or not.
     */
    Single<Event> getEvent(String eventId, boolean forceUpdate);

    /**
     * Get a list of all events stored in the repository.
     * The returned {@link Single} will emit the list of {@link Event}s once it is retrieved.
     * If no events exist, the single will emit an empty list
     *
     * @param forceUpdate if the repository supports updating its data,
     *                    this specifies whether to force the update or not.
     */
    Single<List<Event>> getEvents(boolean forceUpdate);

}
