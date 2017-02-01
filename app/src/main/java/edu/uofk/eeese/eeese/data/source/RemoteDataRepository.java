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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.categories.Remote;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

/*
 * Implementation note:
 * since the web developer want expose an API, a local copy of the projects will be used.
 *
 * Well, sorry for the misleading name.
 */
@ApplicationScope
@Remote
public class RemoteDataRepository implements BaseDataRepository {

    private final Context mContext;
    private final BaseSchedulerProvider mSchedulerProvider;
    private final int PROJECTS_FILE_ID = R.raw.projects;

    @Inject
    public RemoteDataRepository(@NonNull Context context,
                                @NonNull BaseSchedulerProvider schedulerProvider) {
        mContext = context;
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    public Completable insertProject(Project project) {
        throw new UnsupportedOperationException("insert shouldn't be called for a remote repo");
    }

    @Override
    public Completable insertProjects(List<Project> projects) {
        throw new UnsupportedOperationException("insert shouldn't be called for a remote repo");
    }

    @Override
    public Completable setProjects(List<Project> projects) {
        throw new UnsupportedOperationException("set shouldn't be called for a remote repo");
    }

    @Override
    public Completable clearProjects() {
        throw new UnsupportedOperationException("clear shouldn't be called for a remote repo");
    }

    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        // Parse the json file containing the projects data, instead of querying an API
        return
                Single.just(PROJECTS_FILE_ID)
                        .map(new Function<Integer, InputStream>() {
                            @Override
                            public InputStream apply(Integer projectsFileId) throws Exception {
                                return mContext.getResources().openRawResource(projectsFileId);
                            }
                        })
                        .map(new Function<InputStream, List<ProjectJSON>>() {
                            @Override
                            public List<ProjectJSON> apply(InputStream inputStream)
                                    throws Exception {

                                Gson gson = new GsonBuilder().create();
                                // Gson's way of reading a json list as a java List object
                                Type type = new TypeToken<List<ProjectJSON>>() {
                                }.getType();

                                return gson.fromJson(
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        inputStream,
                                                        Charset.forName("UTF8")
                                                )
                                        ),
                                        type
                                );
                            }
                        }).flatMap(new Function<List<ProjectJSON>, Single<List<Project>>>() {
                    @Override
                    public Single<List<Project>> apply(List<ProjectJSON> projectJSONs)
                            throws Exception {
                        return Observable.fromIterable(projectJSONs)
                                .map(new Function<ProjectJSON, Project>() {
                                    @Override
                                    public Project apply(ProjectJSON projectJSON) throws Exception {
                                        return new Project.Builder(
                                                projectJSON.name, //use the name as an ID
                                                projectJSON.name,
                                                projectJSON.head)
                                                .withDesc(projectJSON.desc).build();
                                    }
                                })
                                .toList();
                    }
                }).subscribeOn(mSchedulerProvider.io());

    }

    /**
     * Technically it should be possible to get a single project,
     * however, under the mentioned circumstances, its just inefficient and useless to do that
     */
    @Override
    public Single<Project> getProject(String projectId, boolean forceUpdate) {
        throw new UnsupportedOperationException("Cannot get a single project");
    }

    /**
     * This function cannot be implemented without an API, so just use
     * {@link LocalDataRepository}'s <code>getGalleryImageBitmap(int,int)</code> instead.
     */
    @Override
    public Single<Bitmap> getGalleryImageBitmap(int width, int height) {
        throw new UnsupportedOperationException("Cannot get a single project");
    }

    private static class ProjectJSON {
        /**
         * The model of the json file, some field may not be used
         */
        String name;
        String desc;
        String head;
        List<String> prereq;
        String category;
    }
}
