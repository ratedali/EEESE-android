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

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.backend.ApiWrapper;
import edu.uofk.eeese.eeese.data.backend.BackendApi;
import edu.uofk.eeese.eeese.di.categories.Remote;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Completable;
import io.reactivex.Single;

/*
 * Implementation note:
 * since the web developer won't expose an API, a local copy of the projects will be used.
 *
 * Well, sorry for the misleading name.
 */
@ApplicationScope
@Remote
class RemoteDataRepository implements BaseDataRepository {

    private final Context mContext;
    private final BaseSchedulerProvider mSchedulerProvider;
    private final ApiWrapper mApi;

    @Inject
    RemoteDataRepository(@NonNull Context context,
                         @NonNull BaseSchedulerProvider schedulerProvider,
                         @NonNull BackendApi backendApi) {
        mContext = context;
        mSchedulerProvider = schedulerProvider;
        mApi = new ApiWrapper(backendApi);
    }

    @Override
    public Completable insertProject(Project project) {
        // Cannot insert, so fail immediately
        return Completable.error(
                new UnsupportedOperationException("cannot insert to a remote repository")
        );
    }

    @Override
    public Completable insertProjects(List<Project> projects) {
        // Cannot insert, so fail immediately
        return Completable.error(
                new UnsupportedOperationException("cannot insert to a remote repository")
        );
    }

    @Override
    public Completable setProjects(List<Project> projects) {
        return Completable.error(
                new UnsupportedOperationException("cannot set projects for a remote repository")
        );
    }

    @Override
    public Completable setProjects(List<Project> projects, @Project.ProjectCategory int category) {
        return Completable.error(
                new UnsupportedOperationException("cannot set projects for a remote repository")
        );
    }

    @Override
    public Completable clearProjects() {
        return Completable.error(
                new UnsupportedOperationException("cannot clear projects for a remote repository")
        );
    }

    @Override
    public Completable clearProjects(@Project.ProjectCategory int category) {
        return Completable.error(
                new UnsupportedOperationException("cannot clear projects for a remote repository")
        );
    }

    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        return mApi.projects()
                .subscribeOn(mSchedulerProvider.io());

    }

    @Override
    public Single<List<Project>> getProjectsWithCategory(boolean forceUpdate, @Project.ProjectCategory final int category) {
        return mApi.projects(category)
                .subscribeOn(mSchedulerProvider.io());
    }


    @Override
    public Single<Project> getProject(String projectId, boolean forceUpdate) {
        return mApi.project(projectId)
                .subscribeOn(mSchedulerProvider.io());
    }

    /**
     * This function cannot be implemented without an API, so just use
     * {@link LocalDataRepository}'s <code>getGalleryImageBitmap(int,int)</code> instead.
     */
    @Override
    public Single<Bitmap> getGalleryImageBitmap(int width, int height) {
        return Single.error(
                new UnsupportedOperationException(
                        "cannot get a gallery image for this implementation"));
    }
}
