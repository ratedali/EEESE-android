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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.categories.Remote;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("WrongConstant")
public class DataRepositoryTest {
    @Local
    @Mock
    private BaseDataRepository local;

    @Remote
    @Mock
    private BaseDataRepository remote;

    private DataRepository source;
    private final Project project = new Project.Builder("id", "name", "head", Project.POWER).build();

    @Before
    public void setupSource() {
        MockitoAnnotations.initMocks(this);
        source = new DataRepository(local, remote);
    }

    @After
    public void resetMocks() {
        reset(local, remote);
    }

    @Test
    public void singleInsertIsOnlyLocal() {
        when(local.insertProject(any(Project.class))).thenReturn(Completable.complete());
        source.insertProject(project)
                .test()
                .assertComplete();
        verify(local).insertProject(project);
        verifyZeroInteractions(remote);
    }

    @Test
    public void multipleInsertIsOnlyLocal() {
        when(local.insertProjects(anyListOf(Project.class))).thenReturn(Completable.complete());
        List<Project> projects = Collections.emptyList();
        source.insertProjects(projects)
                .test()
                .assertComplete();
        verify(local).insertProjects(projects);
        verifyZeroInteractions(remote);
    }

    @Test
    public void clearIsOnlyLocal() {
        when(local.clearProjects()).thenReturn(Completable.complete());
        source.clearProjects()
                .test()
                .assertComplete();
        verify(local).clearProjects();
        verifyZeroInteractions(remote);
    }

    @Test
    public void useRemote_whenForced() {
        final List<Project> projects = Collections.emptyList();
        when(remote.getProjects(true)).thenReturn(Single.just(projects));
        source.getProjects(true)
                .test()
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> returnedProjects) throws Exception {
                        return returnedProjects == projects;
                    }
                }).assertComplete();
        verify(remote).getProjects(true);
        verify(local, never()).getProjects(anyBoolean());
        verify(local, never()).getProjectsWithCategory(anyBoolean(), anyInt());
        verify(local, never()).getProject(anyString(), anyBoolean());
    }

    @Test
    public void useBoth_whenNotForced() {
        final List<Project> localProjects = Collections.singletonList(project);
        final List<Project> remoteProjects = Collections.singletonList(project);
        when(remote.getProjects(anyBoolean())).thenReturn(Single.just(remoteProjects));
        when(local.getProjects(anyBoolean())).thenReturn(Single.just(localProjects));

        source.getProjects(false)
                .test()
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> returnedProjects) throws Exception {
                        return returnedProjects == localProjects ||
                                returnedProjects == remoteProjects;
                    }
                }).assertComplete();

        verify(remote).getProjects(anyBoolean());
        verify(local).getProjects(anyBoolean());
    }

    @Test
    public void useCache_onConsecutiveCalls() {
        final List<Project> localProjects = Collections.singletonList(project);
        final List<Project> remoteProjects = Collections.singletonList(project);

        when(remote.getProjects(anyBoolean())).thenReturn(Single.just(remoteProjects));
        when(local.getProjects(anyBoolean())).thenReturn(Single.just(localProjects));

        source.getProjects(false)
                .flatMap(new Function<List<Project>, Single<List<Project>>>() {
                    @Override
                    public Single<List<Project>> apply(List<Project> projects) throws Exception {
                        return source.getProjects(false);
                    }
                })
                .test()
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> projects) throws Exception {
                        return projects.size() == 1;
                    }
                })
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> projects) throws Exception {
                        return projects.get(0).equals(project);
                    }
                })
                .assertComplete();

        verify(local, times(1)).getProjects(anyBoolean());
        verify(remote, times(1)).getProjects(anyBoolean());
    }


    @Test
    public void useRemoteForCategory_whenForced() {
        final List<Project> projects = Collections.emptyList();
        when(remote.getProjectsWithCategory(eq(true), anyInt())).thenReturn(Single.just(projects));

        source.getProjectsWithCategory(true, Project.POWER)
                .test()
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> returnedProjects) throws Exception {
                        return returnedProjects == projects;
                    }
                }).assertComplete();

        verify(remote).getProjectsWithCategory(true, Project.POWER);
        verify(local, never()).getProjects(anyBoolean());
        verify(local, never()).getProjectsWithCategory(anyBoolean(), anyInt());
        verify(local, never()).getProject(anyString(), anyBoolean());
    }

    @Test
    public void useBothForCategory_whenNotForced() {
        final List<Project> localProjects = Collections.singletonList(project);
        final List<Project> remoteProjects = Collections.singletonList(project);
        when(remote.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(remoteProjects));
        when(local.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(localProjects));

        source.getProjectsWithCategory(false, Project.POWER)
                .test()
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> returnedProjects) throws Exception {
                        return returnedProjects == localProjects ||
                                returnedProjects == remoteProjects;
                    }
                }).assertComplete();

        verify(remote).getProjectsWithCategory(anyBoolean(), eq(Project.POWER));
        verify(local).getProjectsWithCategory(anyBoolean(), eq(Project.POWER));
    }

    @Test
    public void useCacheForCategory_onConsecutiveCalls() {
        final List<Project> localProjects = Collections.singletonList(project);
        final List<Project> remoteProjects = Collections.singletonList(project);

        when(remote.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(remoteProjects));
        when(local.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(localProjects));

        source.getProjectsWithCategory(false, project.getCategory())
                .flatMap(new Function<List<Project>, Single<List<Project>>>() {
                    @Override
                    public Single<List<Project>> apply(List<Project> projects) throws Exception {
                        return source.getProjectsWithCategory(false, project.getCategory());
                    }
                })
                .test()
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> projects) throws Exception {
                        return projects.size() == 1;
                    }
                })
                .assertValue(new Predicate<List<Project>>() {
                    @Override
                    public boolean test(List<Project> projects) throws Exception {
                        return projects.get(0).equals(project);
                    }
                })
                .assertComplete();

        verify(local, times(1)).getProjectsWithCategory(anyBoolean(), eq(project.getCategory()));
        verify(remote, times(1)).getProjectsWithCategory(anyBoolean(), eq(project.getCategory()));
    }


}
