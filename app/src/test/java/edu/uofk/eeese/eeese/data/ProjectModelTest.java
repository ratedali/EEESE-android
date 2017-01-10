/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ProjectModelTest {

    @Mock
    private Uri project1Uri;
    @Mock
    private Uri differentUri;

    private Project project1;


    @Before
    public void setupProjects() {
        project1 = new Project
                .Builder("1", "Project 1", "Mr. Head")
                .withDesc("desc")
                .build();
    }

    @Test
    public void cannotEqualNull() {
        assertFalse(project1.equals(null));
    }

    @Test
    public void cannotEqualInstancesOfOtherClasses() {
        assertFalse(project1.equals(new Object()));
    }

    @Test
    public void alwaysEqualsItself() {
        assertTrue(project1.equals(project1));
    }

    @Test
    public void equalsProjectsWithTheSameFields() {
        Project theSameProject = new Project
                .Builder(project1.getId(), project1.getName(), project1.getProjectHead())
                .withDesc(project1.getDesc())
                .build();

        assertTrue(project1.equals(theSameProject));
    }

    @Test
    public void notEqualsIfDifferentId() {
        Project withDifferentId = new Project
                .Builder(project1.getId() + "_but_different",
                project1.getName(),
                project1.getProjectHead())
                .withDesc(project1.getDesc())
                .build();

        assertFalse(project1.equals(withDifferentId));
    }

    @Test
    public void notEqualsIfDifferentName() {
        Project withDifferentName = new Project
                .Builder(project1.getId(),
                project1.getName() + " But Different",
                project1.getProjectHead())
                .withDesc(project1.getDesc())
                .build();

        assertFalse(project1.equals(withDifferentName));
    }

    @Test
    public void notEqualsIfDifferentHead() {
        Project withDifferentHead = new Project
                .Builder(project1.getId(),
                project1.getName(),
                project1.getProjectHead() + " In The Box")
                .withDesc(project1.getDesc())
                .build();

        assertFalse(project1.equals(withDifferentHead));
    }

    @Test
    public void notEqualsIfDifferentDesc() {
        Project withDifferentDesc = new Project
                .Builder(project1.getId(), project1.getName(), project1.getProjectHead())
                .withDesc(project1.getDesc() + " But Not Exactly The Same!")
                .build();

        assertFalse(project1.equals(withDifferentDesc));
    }
}
