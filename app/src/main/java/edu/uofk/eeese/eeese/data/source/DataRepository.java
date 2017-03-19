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
import android.util.SparseBooleanArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.categories.Cache;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.categories.Remote;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

@ApplicationScope
@Cache
class DataRepository implements BaseDataRepository {

    private BaseDataRepository mLocalRepo;
    private BaseDataRepository mRemoteRepo;

    private Map<String, Project> mProjectsCache;
    private SparseBooleanArray mProjectsCacheDirty;

    private Map<String, Event> mEventsCache;
    private boolean mEventsCacheDirty;

    @Inject
    DataRepository(@Local BaseDataRepository localRepo,
                   @Remote BaseDataRepository remoteRepo) {
        mLocalRepo = localRepo;
        mRemoteRepo = remoteRepo;

        mProjectsCache = new HashMap<>();
        mProjectsCacheDirty = new SparseBooleanArray();
        markProjectsCacheDirty();

        mEventsCache = new HashMap<>();
        markEventsCacheDirty();

    }

    @Override
    public Completable insertProject(Project project) {
        // set the cache of the inserted project category as invalid
        markProjectsCacheDirty(project.getCategory());
        return mLocalRepo.insertProject(project);
    }

    @Override
    public Completable insertProjects(List<Project> projects) {
        markProjectsCacheDirty(); // mark all the cache as dirty for simplicity
        return mLocalRepo.insertProjects(projects);
    }

    @Override
    public Completable setProjects(List<Project> projects) {
        markProjectsCacheDirty();
        return mLocalRepo.setProjects(projects);
    }

    @Override
    public Completable setProjects(List<Project> projects, @Project.ProjectCategory int category) {
        markProjectsCacheDirty(category);
        return mLocalRepo.setProjects(projects, category);
    }

    @Override
    public Completable clearProjects() {
        mProjectsCache.clear();
        markProjectsCacheDirty();
        return mLocalRepo.clearProjects();
    }

    @Override
    public Completable clearProjects(@Project.ProjectCategory int category) {
        Map<String, Project> projects = Observable
                .fromIterable(mProjectsCache.values())
                .filter(project -> project.getCategory() != category)
                .reduce(new HashMap<String, Project>(),
                        (map, project) -> {
                            map.put(project.getId(), project);
                            return map;
                        }
                )
                .blockingGet();
        mProjectsCache.clear();
        mProjectsCache.putAll(projects);
        return mLocalRepo.clearProjects(category);
    }

    @NonNull
    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        Single<List<Project>> cacheData = Observable.fromIterable(mProjectsCache.values()).toList();
        return fetchWithCache(forceUpdate, !checkProjectsCacheDirty(),
                // Remote Source
                getAndSaveRemoteProjects(),
                // Local Source
                mLocalRepo.getProjects(forceUpdate),
                // Cache Source
                cacheData,
                // Valid data check
                projects -> !projects.isEmpty(),
                // Cache Operation
                projects -> {
                    cacheProjects(projects);
                    markProjectsCacheValid();
                });
    }

    @NonNull
    @Override
    public Single<List<Project>> getProjectsWithCategory(boolean forceUpdate,
                                                         @Project.ProjectCategory int category) {
        Single<List<Project>> cacheData = Observable.fromIterable(mProjectsCache.values())
                .filter(project -> project.getCategory() == category)
                .toList();
        return fetchWithCache(forceUpdate, !checkProjectsCacheDirty(category),
                // Remote Source
                getAndSaveRemoteProjects(category),
                // Local Source
                mLocalRepo.getProjectsWithCategory(forceUpdate, category),
                // Cache Source
                cacheData,
                // Valid data check
                projects -> !projects.isEmpty(),
                // Cache Operation
                projects -> {
                    cacheProjects(projects);
                    markProjectsCacheValid(category);
                });
    }

    @Override
    public Single<Project> getProject(String projectId, boolean forceUpdate) {
        // indicates if a sync is currently running
        Single<List<Project>> cacheData = Observable.fromIterable(mProjectsCache.values()).toList();
        return fetchWithCache(forceUpdate, !checkProjectsCacheDirty(),
                // Remote Source
                getAndSaveRemoteProjects(),
                // Local Source
                mLocalRepo.getProjects(forceUpdate),
                // Cache Source
                cacheData,
                // Valid data check
                projects -> !projects.isEmpty(),
                // Cache Operation
                projects -> {
                    cacheProjects(projects);
                    markProjectsCacheValid();
                })
                .flattenAsObservable(projects -> projects)
                .filter(project -> project.getId().equals(projectId))
                .firstOrError();
    }

    @Override
    public Completable insertEvent(Event event) {
        markEventsCacheDirty();
        return mLocalRepo.insertEvent(event);
    }

    @Override
    public Completable insertEvents(List<Event> events) {
        markEventsCacheDirty();
        return mLocalRepo.insertEvents(events);
    }

    @Override
    public Completable setEvents(List<Event> events) {
        markEventsCacheDirty();
        return mLocalRepo.setEvents(events);
    }

    @Override
    public Completable clearEvents() {
        mEventsCache.clear();
        markEventsCacheDirty();
        return mLocalRepo.clearEvents();
    }

    @Override
    public Single<Event> getEvent(String eventId, boolean forceUpdate) {
        Single<List<Event>> cacheData = Observable.fromIterable(mEventsCache.values()).toList();
        return fetchWithCache(forceUpdate, !checkProjectsCacheDirty(),
                // Remote Source
                getAndSaveRemoteEvents(),
                // Local Source
                mLocalRepo.getEvents(forceUpdate),
                // Cache Source
                cacheData,
                // Valid data check
                events -> !events.isEmpty(),
                // Cache Operation
                events -> {
                    cacheEvents(events);
                    markEventsCacheValid();
                })
                .flattenAsObservable(events -> events)
                .filter(event -> event.getId().equals(eventId))
                .firstOrError();
    }

    @Override
    public Single<List<Event>> getEvents(boolean forceUpdate) {
        Single<List<Event>> cacheData = Observable.fromIterable(mEventsCache.values()).toList();
        return fetchWithCache(forceUpdate, !checkProjectsCacheDirty(),
                // Remote Source
                getAndSaveRemoteEvents(),
                // Local Source
                mLocalRepo.getEvents(forceUpdate),
                // Cache Source
                cacheData,
                // Valid data check
                events -> !events.isEmpty(),
                // Cache Operation
                events -> {
                    cacheEvents(events);
                    markEventsCacheValid();
                });
    }

    @Override
    public Single<Bitmap> getGalleryImageBitmap(int width, int height) {
        return mLocalRepo.getGalleryImageBitmap(width, height);
    }

    /*
     * Helpers
     */

    private Single<List<Project>> getAndSaveRemoteProjects() {
        return mRemoteRepo.getProjects(true)
                .doOnSuccess(projects ->
                        mLocalRepo.setProjects(projects).subscribe());
    }

    private Single<List<Project>> getAndSaveRemoteProjects(@Project.ProjectCategory int category) {
        return mRemoteRepo.getProjectsWithCategory(true, category)
                .doOnSuccess(projects ->
                        mLocalRepo.setProjects(projects, category).subscribe());
    }

    private Single<List<Event>> getAndSaveRemoteEvents() {
        return mRemoteRepo.getEvents(true)
                .doOnSuccess(events -> mLocalRepo.setEvents(events).subscribe());
    }

    @SafeVarargs
    private final <T> Single<T> firstAvailable(Predicate<T> isValid,
                                               Single<T>... sources) {
        Observable<Single<T>> singles =
                Observable.fromArray(sources);
        return Single.concat(singles)
                .filter(isValid)
                .firstOrError();
    }

    private <T> Single<T> fetchWithCache(boolean update, boolean useCache,
                                         Single<T> remoteSource,
                                         Single<T> localSrouce,
                                         Single<T> cacheSource,
                                         Predicate<T> isValid,
                                         Consumer<T> cacheOnLoad) {
        if (update) {
            return remoteSource
                    .doOnSuccess(cacheOnLoad);

        } else if (!useCache) {
            return firstAvailable(isValid, localSrouce, remoteSource)
                    .doOnSuccess(cacheOnLoad);
        } else {
            // filter the current cache
            return cacheSource;
        }
    }

    /*
     * Projects caching
     */

    private void cacheProjects(List<Project> projects) {
        for (Project project : projects) {
            mProjectsCache.put(project.getId(), project);
        }
    }

    private void markProjectsCacheDirty() {
        mProjectsCacheDirty.clear();
    }

    private void markProjectsCacheDirty(@Project.ProjectCategory int category) {
        mProjectsCacheDirty.delete(category);
    }

    private void markProjectsCacheValid() {
        for (int category : new int[]{
                Project.POWER,
                Project.SOFTWARE,
                Project.ELECTRONICS_CONTROL,
                Project.TELECOM
        }) {
            mProjectsCacheDirty.put(category, false);
        }
    }

    private void markProjectsCacheValid(@Project.ProjectCategory int category) {
        mProjectsCacheDirty.put(category, false);
    }


    private boolean checkProjectsCacheDirty() {
        return mProjectsCacheDirty.size() < Project.NUM_OF_CATEGORIES;
    }

    private boolean checkProjectsCacheDirty(@Project.ProjectCategory int category) {
        return mProjectsCacheDirty.get(category, true);
    }

    /*
     * Events Caching
     */

    private void cacheEvents(List<Event> events) {
        mEventsCache.putAll(
                Observable.fromIterable(events)
                        .toMap(Event::getId)
                        .blockingGet());
    }

    private void markEventsCacheDirty() {
        mEventsCacheDirty = true;
    }

    private void markEventsCacheValid() {
        mEventsCacheDirty = false;
    }

    private boolean checkEventsCacheDirty() {
        return mEventsCacheDirty;
    }
}
