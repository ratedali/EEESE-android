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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

@ApplicationScope
@Cache
public class DataRepository implements BaseDataRepository {

    private BaseDataRepository mLocalRepo;
    private BaseDataRepository mRemoteRepo;

    private Map<String, Project> mCache;
    private boolean mCacheDirty;

    @Inject
    public DataRepository(@Local BaseDataRepository localRepo,
                          @Remote BaseDataRepository remoteRepo) {
        mLocalRepo = localRepo;
        mRemoteRepo = remoteRepo;
        mCache = new HashMap<>();
        mCacheDirty = true;
    }

    @Override
    public Completable insertProject(Project project) {
        mCacheDirty = true;
        return mLocalRepo.insertProject(project);
    }

    @Override
    public Completable insertProjects(List<Project> projects) {
        mCacheDirty = true;
        return mLocalRepo.insertProjects(projects);
    }

    @Override
    public Completable setProjects(List<Project> projects) {
        return mLocalRepo.setProjects(projects);
    }

    @Override
    public Completable clearProjects() {
        mCache.clear();
        mCacheDirty = true;
        return mLocalRepo.clearProjects();
    }

    @Override
    public Observable<List<Project>> getProjects(boolean forceUpdate) {
        if (forceUpdate) {
            return getAndSaveRemoteProjects();
        } else if (mCacheDirty) {
            return getAndCacheLocalProjects().filter(new Predicate<List<Project>>() {
                @Override
                public boolean test(List<Project> projects) throws Exception {
                    return !projects.isEmpty();
                }
            }).ambWith(
                    getAndSaveRemoteProjects()
            );
        } else {
            List<Project> projects = new ArrayList<>(mCache.values());
            return Observable.just(projects);
        }
    }

    @Override
    public Observable<Project> getProject(String projectId, boolean forceUpdate) {
        // indicates if a sync is currently running
        Completable syncJob = Completable.complete();
        if (forceUpdate) {
            // if an update is forced, fetch from remote
            syncJob = Completable.fromObservable(getAndSaveRemoteProjects());
        } else if (mCacheDirty) {
            // if the cache is dirty but an update is not forced, fetched from local
            syncJob = Completable.fromObservable(
                    getAndCacheLocalProjects().ambWith(getAndSaveRemoteProjects())
            );
        }
        // after the sync completes, return from cache
        return syncJob.andThen(Observable.just(mCache.get(projectId)));
    }

    @Override
    public Observable<Bitmap> getGalleryImageBitmap(int width, int height) {
        return mLocalRepo.getGalleryImageBitmap(width, height);
    }

    private Observable<List<Project>> getAndSaveRemoteProjects() {
        return mRemoteRepo.getProjects(true)
                .doOnNext(new Consumer<List<Project>>() {
                    @Override
                    public void accept(List<Project> projects) throws Exception {
                        mLocalRepo.setProjects(projects);
                        cacheProjects(projects);
                        mCacheDirty = false;
                    }
                });
    }

    private Observable<List<Project>> getAndCacheLocalProjects() {
        return mLocalRepo.getProjects(true)
                .doOnNext(new Consumer<List<Project>>() {
                    @Override
                    public void accept(List<Project> projects) throws Exception {
                        cacheProjects(projects);
                        mCacheDirty = false;
                    }
                });
    }

    private void cacheProjects(List<Project> projects) {
        for (Project project : projects) {
            mCache.put(project.getId(), project);
        }
    }
}
