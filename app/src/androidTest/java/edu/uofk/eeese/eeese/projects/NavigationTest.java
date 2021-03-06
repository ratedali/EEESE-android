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
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.AppModule;
import edu.uofk.eeese.eeese.DaggerTestAppComponent;
import edu.uofk.eeese.eeese.MockApiModule;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.TestAppComponent;
import edu.uofk.eeese.eeese.about.AboutActivity;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.BaseDataRepository;
import edu.uofk.eeese.eeese.details.DetailsActivity;
import edu.uofk.eeese.eeese.util.EspressoIdlingResourceModule;
import edu.uofk.eeese.eeese.util.TestRule;
import edu.uofk.eeese.eeese.util.schedulers.SchedulerProviderModule;
import io.reactivex.Single;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    private static TestAppComponent mAppComponent = DaggerTestAppComponent
            .builder()
            .appModule(new AppModule(InstrumentationRegistry.getTargetContext()))
            .schedulerProviderModule(new SchedulerProviderModule())
            .mockApiModule(new MockApiModule())
            .espressoIdlingResourceModule(new EspressoIdlingResourceModule())
            .build();

    @Rule
    public TestRule<ProjectsActivity> testRule =
            new TestRule<>(ProjectsActivity.class, false, false, mAppComponent);

    private BaseDataRepository source = mAppComponent.dataRepository();
    private IdlingResource idlingResource = mAppComponent.idlingResource();

    private static final Project project = new Project.Builder("1", "Project 1", "head 1", Project.POWER)
            .build();
    private static final List<Project> projects = Collections.singletonList(project);

    // empty bitmap
    private static final int[] colors = new int[]{0xf000};
    private static final Bitmap bitmap =
            Bitmap.createBitmap(colors, 1, 1, Bitmap.Config.ARGB_8888);

    @SuppressWarnings("WrongConstant")
    @Before
    public void setup() {
        when(source.getProjectsWithCategory(anyBoolean(), anyInt()))
                .thenReturn(Single.just(projects));

        when(source.getProject(eq(project.getId()), anyBoolean()))
                .thenReturn(Single.just(project));

        when(source.getGalleryImageBitmap(anyInt(), anyInt()))
                .thenReturn(Single.just(bitmap));

        testRule.launchActivity(null);
    }

    @Before
    public void registerIdlingResource() {
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @After
    public void resetMocks() {
        reset(source);
    }

    @Test
    public void whenHomeItemIsClicked_theOpenTheAboutActivity() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_about));

        intended(hasComponent(hasClassName(AboutActivity.class.getName())));
    }

    @Test
    public void whenAProjectIsClicked_thenOpenTheProjectDetailsActivity() {
        onView(allOf(withId(R.id.projects_list),
                isDescendantOfA(
                        withTagKey(R.string.tag_projectslist,
                                Matchers.equalTo(project.getCategory()))
                )
        )).perform(RecyclerViewActions.actionOnItemAtPosition(
                0,
                click()));

        intended(hasComponent(hasClassName(DetailsActivity.class.getName())));
    }

    @Test
    public void whenAProjectIsClicked_thenOpenThatProjectDetails() {
        onView(allOf(withId(R.id.projects_list),
                isDescendantOfA(
                        withTagKey(R.string.tag_projectslist,
                                Matchers.equalTo(project.getCategory()))
                )
        )).perform(RecyclerViewActions.actionOnItemAtPosition(
                0,
                click()));


        onView(withId(R.id.project_name)).check(matches(withText(project.getName())));
        onView(withId(R.id.project_head)).check(matches(withText(project.getProjectHead())));
        onView(withId(R.id.project_desc)).check(matches(withText(project.getDesc())));
    }
}
