/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.projects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.util.TestUtils;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsPresenterTest {
    @Mock
    private BaseDataRepository source;
    @Mock
    private ProjectsContract.View view;
    @Mock
    private BaseSchedulerProvider schedulerProvider;

    @InjectMocks
    private ProjectsPresenter presenter;

    private List<Project> projects = Collections.singletonList(
            new Project.Builder("1", "Project 1", "Project 1 Head", Project.POWER)
                    .build());

    @Before
    public void setupSchedulerProvider() {
        TestUtils.setupMockSchedulerProvider(schedulerProvider, Schedulers.trampoline());
    }

    @After
    public void resetMocks() {
        reset(source, view, schedulerProvider);
    }

    @Test
    public void shouldCallNoProjectsOnEmptyList() {
        when(source.getProjects(anyBoolean()))
                .thenReturn(Single.just(Collections.<Project>emptyList()));
        presenter.loadProjects(anyBoolean());
        verify(view).showNoProjects();
    }

    @Test
    public void shouldCallShowProjectsWithNonEmptyList() {
        when(source.getProjects(anyBoolean()))
                .thenReturn(Single.just(projects));
        presenter.loadProjects(anyBoolean());
        verify(view).showProjects(projects);
    }

    @Test
    public void shouldCallConnectionErrorOnException() {
        when(source.getProjects(anyBoolean()))
                .thenReturn(Single.<List<Project>>error(new Exception()));
        presenter.loadProjects(anyBoolean());
        verify(view).showNoConnectionError();
    }
}
