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
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.categories.Cache;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.categories.Remote;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@ApplicationScope
@Cache
class DataRepository implements BaseDataRepository {

    private BaseDataRepository mLocalRepo;
    private BaseDataRepository mRemoteRepo;

    private Map<String, Project> mCache;
    private SparseBooleanArray mCacheDirty;

    @Inject
    DataRepository(@Local BaseDataRepository localRepo,
                   @Remote BaseDataRepository remoteRepo) {
        mLocalRepo = localRepo;
        mRemoteRepo = remoteRepo;
        mCache = new HashMap<>();
        mCacheDirty = new SparseBooleanArray();
        markCacheDirty();

    }

    @Override
    public Completable insertProject(Project project) {
        // set the cache of the inserted project category as invalid
        markCacheDirty(project.getCategory());
        return mLocalRepo.insertProject(project);
    }

    @Override
    public Completable insertProjects(List<Project> projects) {
        markCacheDirty(); // mark all the cache as dirty for simplicity
        return mLocalRepo.insertProjects(projects);
    }

    @Override
    public Completable setProjects(List<Project> projects) {
        markCacheDirty();
        return mLocalRepo.setProjects(projects);
    }

    @Override
    public Completable clearProjects() {
        mCache.clear();
        markCacheDirty();
        return mLocalRepo.clearProjects();
    }

    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        if (forceUpdate) {
            return getAndSaveRemoteProjects();
        } else if (checkCacheDirty()) {
            return mLocalRepo.getProjects(true).mergeWith(getAndSaveRemoteProjects())
                    .filter(projects -> !projects.isEmpty())
                    .firstOrError()
                    .doOnSuccess(projects -> {
                        cacheProjects(projects);
                        markCacheValid();
                    });
        } else {
            List<Project> projects = new ArrayList<>(mCache.values());
            return Single.just(projects);
        }
    }

    @Override
    public Single<List<Project>> getProjectsWithCategory(boolean forceUpdate, @Project.ProjectCategory final int category) {
        if (forceUpdate) {
            return getAndSaveRemoteProjects(category);

        } else if (checkCacheDirty(category)) {
            return getAndCacheLocalProjects(category)
                    .mergeWith(getAndSaveRemoteProjects(category))
                    .filter(projects -> !projects.isEmpty())
                    .firstOrError();
        } else {
            // filter the current cache
            return Observable.fromIterable(mCache.values())
                    .filter(project -> project.getCategory() == category)
                    .toList();
        }
    }

    @Override
    public Single<Project> getProject(String projectId, boolean forceUpdate) {
        // indicates if a sync is currently running
        Completable syncJob = Completable.complete();
        if (forceUpdate) {
            // if an update is forced, fetch from remote
            syncJob = Completable.fromSingle(getAndSaveRemoteProjects());
        } else if (checkCacheDirty()) {
            // if the cache is dirty but an update is not forced, fetched from local
            syncJob = Completable
                    .fromSingle(mLocalRepo.getProjects(true).mergeWith(getAndSaveRemoteProjects())
                            .firstOrError().doOnSuccess(projects -> {
                                cacheProjects(projects);
                                markCacheValid();
                            }));
        }
        // after the sync completes, return from cache
        return syncJob.andThen(Single.just(mCache.get(projectId)));
    }

    @Override
    public Single<Bitmap> getGalleryImageBitmap(int width, int height) {
        return mLocalRepo.getGalleryImageBitmap(width, height);
    }

    private Single<List<Project>> getAndSaveRemoteProjects() {
        return mRemoteRepo.getProjects(true)
                .doOnSuccess(projects -> mLocalRepo.setProjects(projects));
    }

    private Single<List<Project>> getAndSaveRemoteProjects(@Project.ProjectCategory final int category) {
        return mRemoteRepo.getProjectsWithCategory(true, category)
                .doOnSuccess(projects -> {
                    mLocalRepo.setProjects(projects);
                    cacheProjects(projects);
                    markCacheValid(category);
                });
    }

    private Single<List<Project>> getAndCacheLocalProjects(@Project.ProjectCategory final int category) {
        return mLocalRepo.getProjectsWithCategory(true, category)
                .doOnSuccess(projects -> {
                    cacheProjects(projects);
                    markCacheValid(category);
                });
    }

    private void cacheProjects(List<Project> projects) {
        for (Project project : projects) {
            mCache.put(project.getId(), project);
        }
    }

    private void markCacheDirty() {
        mCacheDirty.clear();
    }

    private void markCacheDirty(@Project.ProjectCategory int category) {
        mCacheDirty.delete(category);
    }

    private void markCacheValid() {
        for (int category : new int[]{
                Project.POWER,
                Project.SOFTWARE,
                Project.ELECTRONICS_CONTROL,
                Project.TELECOM
        }) {
            mCacheDirty.put(category, false);
        }
    }

    private void markCacheValid(@Project.ProjectCategory int category) {
        mCacheDirty.put(category, false);
    }


    private boolean checkCacheDirty() {
        return mCacheDirty.size() < Project.NUM_OF_CATEGORIES;
    }

    private boolean checkCacheDirty(@Project.ProjectCategory int category) {
        return mCacheDirty.get(category, true);
    }
}
