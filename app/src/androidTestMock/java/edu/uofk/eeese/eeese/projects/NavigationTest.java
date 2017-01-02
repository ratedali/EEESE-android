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

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.Injection;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.home.HomeActivity;
import io.reactivex.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    private DataRepository source =
            Injection.provideDataRepository(InstrumentationRegistry.getTargetContext());

    private static final List<Project> projects =
            Collections.singletonList(new Project("1", "Project 1", "desc"));

    private static final String basicInfo = "";

    @Rule
    public TestRule<ProjectsActivity> testRule =
            new TestRule<>(ProjectsActivity.class, source);

    @Before
    public void setUp() {
        Espresso.registerIdlingResources(Injection.provideCountingIdlingResource());
    }

    @Test
    public void homeItemLeadsToHomeActivity() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home));

        intended(hasComponent(hasClassName(HomeActivity.class.getName())));
    }

    @Test
    @Ignore("Not Implemented Yet")
    public void clickOnProjectOpensDetails() {
        onView(withId(R.id.projects_list))
                .perform(RecyclerViewActions.actionOnItem(
                        withText(projects.get(0).getName()),
                        click()));
        // TODO: 1/2/17 Enable after adding DetailsActivity
        //intended(hasComponent(hasClassName(DetailsActivity.class.getName())));
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(Injection.provideCountingIdlingResource());
    }


    private static class TestRule<T extends Activity> extends IntentsTestRule<T> {
        private DataRepository mSource;

        TestRule(Class<T> activityClass,
                 @NonNull DataRepository source) {
            super(activityClass);
            mSource = source;
        }

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            when(mSource.getBasicInfo()).thenReturn(Observable.just(basicInfo));
            when(mSource.getProjects(anyBoolean())).thenReturn(Observable.just(projects));
        }

    }

}
