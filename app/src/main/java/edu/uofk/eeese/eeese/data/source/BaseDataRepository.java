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

import java.util.List;

import edu.uofk.eeese.eeese.data.Project;
import io.reactivex.Completable;
import io.reactivex.Observable;

public interface BaseDataRepository {

    Completable insertProject(Project project);

    Completable insertProjects(List<Project> projects);

    Completable setProjects(List<Project> projects);

    Completable clearProjects();

    Observable<List<Project>> getProjects(boolean forceUpdate);

    Observable<Project> getProject(String projectId, boolean forceUpdate);

    Observable<Bitmap> getGalleryImageBitmap(int width, int height);
}
