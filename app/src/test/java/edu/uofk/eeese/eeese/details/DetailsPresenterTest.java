/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.details;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.util.TestUtils;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {
    @Mock
    private BaseDataRepository source;
    @Mock
    private DetailsContract.View view;
    @Mock
    private BaseSchedulerProvider schedulerProvider;

    private String projectId = "";

    private Project project;

    private DetailsPresenter presenter;

    @Before
    public void setupSchedulerProvider() {
        TestUtils.setupMockSchedulerProvider(schedulerProvider, Schedulers.trampoline());
    }

    @Before
    public void setupProject() {
        project = new Project.Builder(projectId, "Project", "Head", Project.POWER).build();
    }

    @Before
    public void setupPresenter() {
        presenter = new DetailsPresenter(source, view, schedulerProvider, projectId);
    }

    @After
    public void resetMocks() {
        reset(source, view, schedulerProvider);
    }

    @Test
    public void showProjectInfo_ifProjectExists() {
        when(source.getProject(eq(projectId), anyBoolean()))
                .thenReturn(Single.just(project));

        presenter.loadDetails(true);

        verify(view).showProjectInfo(project);
    }


    @Test
    public void showInvalidProject_ifProjectDoesNotExists() {
        when(source.getProject(eq(projectId), anyBoolean()))
                .thenReturn(Single.<Project>error(new Exception()));
        presenter.loadDetails(true);
        verify(view).showInvalidProject();

    }

}
