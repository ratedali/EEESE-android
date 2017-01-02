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
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.Injection;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.DataRepository;
import io.reactivex.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ProjectsScreenTest {


    @Rule
    public ActivityTestRule<ProjectsActivity> testRule =
            new ActivityTestRule<>(ProjectsActivity.class, false, false);

    private DataRepository source;

    @Before
    public void setUp() {
        source = Injection.provideDataRepository(InstrumentationRegistry.getTargetContext());
        Espresso.registerIdlingResources(Injection.provideCountingIdlingResource());
    }

    @Test
    public void emptyListShowsNoProjectsScreen() {
        when(source.getProjects(anyBoolean())).thenReturn(Observable.just(Collections.<Project>emptyList()));

        testRule.launchActivity(null);

        onView(withId(R.id.error_view)).check(matches(isDisplayed()));
        onView(withId(R.id.error_image)).check(matches(isDisplayed()));
        onView(withText(R.string.no_project_available)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyListHidesProjectsList() {
        when(source.getProjects(anyBoolean())).thenReturn(Observable.just(Collections.<Project>emptyList()));

        testRule.launchActivity(null);

        onView(withId(R.id.projects_list)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void shouldShowListOfProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("1", "Project 1", "desc"));
        when(source.getProjects(anyBoolean())).thenReturn(Observable.just(projects));

        testRule.launchActivity(null);

        onView(withId(R.id.projects_list)).check(matches(isDisplayed()));
        onView(withId(R.id.project_name)).check(matches(withText(projects.get(0).getName())));
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(Injection.provideCountingIdlingResource());
    }
}
