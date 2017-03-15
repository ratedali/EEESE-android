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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private final Project project = new Project.Builder("ID", "name", "head", Project.POWER).build();

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        source = new DataRepository(local, remote);

        when(local.setProjects(anyList())).thenReturn(Completable.complete());
        when(local.setProjects(anyList(), anyInt())).thenReturn(Completable.complete());
    }

    @After
    public void resetMocks() {
        reset(local, remote);
    }

    @Test
    public void whenInsertProjectCalled_thenOnlyUseLocal() {
        when(local.insertProject(any(Project.class))).thenReturn(Completable.complete());
        source.insertProject(project)
                .test()
                .assertComplete();
        verify(local).insertProject(project);
        verifyZeroInteractions(remote);
    }

    @Test
    public void whenInsertProjectsCalled_thenOnlyUseLocal() {
        when(local.insertProjects(anyList())).thenReturn(Completable.complete());

        List<Project> projects = Collections.emptyList();
        source.insertProjects(projects)
                .test()
                .assertComplete();

        verify(local).insertProjects(projects);
        verifyNoMoreInteractions(local);
        verifyZeroInteractions(remote);
    }

    @Test
    public void whenClearProjectsCalled_thenOnlyUseLocal() {
        when(local.clearProjects()).thenReturn(Completable.complete());

        source.clearProjects()
                .test()
                .assertComplete();

        verify(local).clearProjects();
        verifyNoMoreInteractions(local);
        verifyZeroInteractions(remote);
    }

    @Test
    public void whenGetProjectsForced_thenOnlyUseRemote() {
        final List<Project> projects = Collections.emptyList();
        when(remote.getProjects(true)).thenReturn(Single.just(projects));

        source.getProjects(true)
                .test()
                .assertValue(returnedProjects -> returnedProjects == projects)
                .assertComplete();

        verify(remote).getProjects(true);
        verify(local, never()).getProjects(anyBoolean());
        verify(local, never()).getProjectsWithCategory(anyBoolean(), anyInt());
        verify(local, never()).getProject(anyString(), anyBoolean());
    }

    @Test
    public void givenLocalContainsData_whenGetProjectsNotForces_thenUseLocal() {
        List<Project> localProjects = Collections.singletonList(project);
        when(
                local.getProjects(anyBoolean())
        ).thenReturn(
                Single.just(localProjects)
        );
        when(
                remote.getProjects(anyBoolean())
        ).thenReturn(
                Single.error(new Exception("The remote repo is not expected to be used"))
        );

        source.getProjects(false)
                .test()
                .assertValue(returnedProjects ->
                        returnedProjects == localProjects)
                .assertComplete();

        verify(local).getProjects(eq(false));
        verifyNoMoreInteractions(local);
    }

    @Test
    public void givenLocalDoesNotContainData_WhenGetProjectsNotForces_thenUseRemoteAndSaveToLocal() {
        List<Project> remoteProjects = Collections.singletonList(project);
        when(local.getProjects(anyBoolean())).thenReturn(Single.just(Collections.emptyList()));
        when(remote.getProjects(anyBoolean())).thenReturn(Single.just(remoteProjects));

        source.getProjects(false)
                .test()
                .assertValue(returnedProjects -> returnedProjects == remoteProjects)
                .assertComplete();

        verify(local).getProjects(eq(false));
        verify(local).setProjects(eq(remoteProjects));
        verifyNoMoreInteractions(local);
        verify(remote).getProjects(anyBoolean());
        verifyNoMoreInteractions(remote);
    }

    @Test
    public void whenGetProjectsCalledMultipleTimes_thenUseCache() {
        final List<Project> localProjects = Collections.singletonList(project);
        final List<Project> remoteProjects = Collections.singletonList(project);

        when(remote.getProjects(anyBoolean())).thenReturn(Single.just(remoteProjects));
        when(local.getProjects(anyBoolean())).thenReturn(Single.just(localProjects));

        source.getProjects(false)
                .flatMap(projects -> source.getProjects(false))
                .test()
                .assertValue(projects -> projects.size() == 1)
                .assertValue(projects -> {
                    return projects.get(0).equals(project);
                })
                .assertComplete();

        verify(local, atMost(1)).getProjects(anyBoolean());
        verify(remote, atMost(1)).getProjects(anyBoolean());
    }


    @Test
    public void whenGetProjectsWithCategoryForced_thenOnlyUseRemote() {
        final List<Project> remoteProjects = Collections.singletonList(project);
        when(remote.getProjectsWithCategory(eq(true), eq(project.getCategory())))
                .thenReturn(Single.just(remoteProjects));

        source.getProjectsWithCategory(true, project.getCategory())
                .test()
                .assertValue(returnedProjects -> returnedProjects == remoteProjects)
                .assertComplete();

        verify(remote).getProjectsWithCategory(true, project.getCategory());
        verifyNoMoreInteractions(remote);
        verify(local, never()).getProjects(anyBoolean());
        verify(local, never()).getProjectsWithCategory(anyBoolean(), anyInt());
        verify(local, never()).getProject(anyString(), anyBoolean());
    }

    @Test
    public void givenLocalContainsData_whenGetProjectsWithCategoryNotForced_thenUseLocal() {
        final List<Project> localProjects = Collections.singletonList(project);
        when(
                local.getProjectsWithCategory(anyBoolean(), eq(project.getCategory()))
        ).thenReturn(Single.just(localProjects));
        when(
                remote.getProjectsWithCategory(anyBoolean(), anyInt())
        ).thenReturn(
                Single.error(new Exception("The remote repo is not expected to be used"))
        );

        source.getProjectsWithCategory(false, project.getCategory())
                .test()
                .assertValue(returnedProjects -> returnedProjects == localProjects)
                .assertComplete();

        verify(local).getProjectsWithCategory(anyBoolean(), eq(project.getCategory()));
    }

    @Test
    public void whenGetProjectsWithCategoryCalledMultipleTimes_thenUseCache() {
        final List<Project> localProjects = Collections.singletonList(project);
        final List<Project> remoteProjects = Collections.singletonList(project);

        when(
                remote.getProjectsWithCategory(anyBoolean(), eq(project.getCategory()))
        ).thenReturn(Single.just(remoteProjects));

        when(
                local.getProjectsWithCategory(anyBoolean(), eq(project.getCategory()))
        ).thenReturn(Single.just(localProjects));

        source.getProjectsWithCategory(false, project.getCategory())
                .flatMap(projects -> source.getProjectsWithCategory(false, project.getCategory()))
                .test()
                .assertValue(projects -> projects.size() == 1)
                .assertValue(projects -> {
                    return projects.get(0).equals(project);
                })
                .assertComplete();

        verify(local, atMost(1))
                .getProjectsWithCategory(anyBoolean(), eq(project.getCategory()));
        verify(remote, atMost(1))
                .getProjectsWithCategory(anyBoolean(), eq(project.getCategory()));
    }


}
