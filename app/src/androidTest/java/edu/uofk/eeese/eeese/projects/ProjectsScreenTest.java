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

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.AppModule;
import edu.uofk.eeese.eeese.DaggerTestAppComponent;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.TestAppComponent;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.util.TestRule;
import io.reactivex.Single;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SuppressWarnings("WrongConstant")
@RunWith(AndroidJUnit4.class)
public class ProjectsScreenTest {

    private TestAppComponent mAppComponent = DaggerTestAppComponent.builder()
            .appModule(new AppModule(InstrumentationRegistry.getTargetContext()))
            .build();

    @Rule
    public TestRule<ProjectsActivity> testRule =
            new TestRule<>(ProjectsActivity.class, false, false, mAppComponent);

    private BaseDataRepository source = mAppComponent.dataRepository();
    private IdlingResource idlingResource = mAppComponent.idlingResource();

    private final Matcher<View> targetRoot = withTagKey(
            R.string.tag_projectslist,
            Matchers.equalTo(Project.POWER));

    @Before
    public void registerIdlingResource() {
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @After
    public void resetSource() {
        reset(source);
    }

    @Test
    public void givenThatSourceReturnsAnEmptyProjectList_thenShowNoProjectsScreen() {
        when(source.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(Collections.emptyList()));

        testRule.launchActivity(null);

        onView(allOf(withId(R.id.error_view), isDescendantOfA(targetRoot)))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(allOf(withId(R.id.error_image), isDescendantOfA(targetRoot)))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(allOf(withText(R.string.no_project_available), isDescendantOfA(targetRoot)))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void givenThatSourceReturnsANonEmptyProjectList_thenShowTheProjectList() {
        Project project = new Project.Builder("1", "Project 1", "head", Project.POWER).build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        when(source.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(projects));

        testRule.launchActivity(null);

        onView(allOf(withId(R.id.projects_list), isDescendantOfA(targetRoot)))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(allOf(withId(R.id.project_name), isDescendantOfA(targetRoot)))
                .check(matches(withText(project.getName())));
    }
}
