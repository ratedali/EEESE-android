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

import android.graphics.Bitmap;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import edu.uofk.eeese.eeese.DaggerTestAppComponent;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.TestAppComponent;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.details.DetailsActivity;
import edu.uofk.eeese.eeese.home.HomeActivity;
import edu.uofk.eeese.eeese.util.TestRule;
import io.reactivex.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    private static TestAppComponent mAppComponent = DaggerTestAppComponent.create();

    @Rule
    public TestRule<ProjectsActivity> testRule =
            new TestRule<>(ProjectsActivity.class, false, false, mAppComponent);

    private static BaseDataRepository source = mAppComponent.dataRepository();
    private IdlingResource idlingResource = mAppComponent.idlingResource();

    private static final List<Project> projects =
            Arrays.asList(
                    new Project.Builder("1", "Project 1", "head 1").build(),
                    new Project.Builder("2", "Project 2", "head 2").withDesc("desc 2").build()
            );
    private static Bitmap mockBitmap;

    @BeforeClass
    public static void setupMocks() {
        mockBitmap = Mockito.mock(Bitmap.class);
        when(source.getProjects(anyBoolean()))
                .thenReturn(Observable.just(projects));
        when(source.getGalleryImageBitmap(anyInt(), anyInt()))
                .thenReturn(Observable.just(mockBitmap));
        for (Project project : projects) {
            when(source.getProject(eq(project.getId()), anyBoolean()))
                    .thenReturn(Observable.just(project));
        }
    }

    @Before
    public void setupTest() {
        Espresso.registerIdlingResources(idlingResource);
        testRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void OpensHome_onHomeItemClick() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home));

        intended(hasComponent(hasClassName(HomeActivity.class.getName())));
    }

    @Test
    public void OpensDetails_onProjectClick() {
        onView(withId(R.id.projects_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0,
                        click()));
        intended(hasComponent(hasClassName(DetailsActivity.class.getName())));
    }

    @Test
    public void OpensCorrectProjectDetails_onProjectClick() {
        int index = 1;
        Project target = projects.get(index);

        onView(withId(R.id.projects_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        index,
                        click()));

        onView(withId(R.id.project_name)).check(matches(withText(target.getName())));
        onView(withId(R.id.project_head)).check(matches(withText(target.getProjectHead())));
        onView(withId(R.id.project_desc)).check(matches(withText(target.getDesc())));
    }

}
