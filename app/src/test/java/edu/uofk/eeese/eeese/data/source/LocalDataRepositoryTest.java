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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.database.DatabaseContract.ProjectEntry;
import edu.uofk.eeese.eeese.util.TestUtils;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalDataRepositoryTest {
    @Mock
    private Context context;
    @Mock
    private SQLiteOpenHelper dbHelper;
    @Mock
    private BaseSchedulerProvider schedulerProvider;
    @InjectMocks
    private LocalDataRepository source;

    @Mock
    SQLiteDatabase database;
    @Mock
    Cursor cursor;

    @Before
    public void setupMocks() {
        // scheduler provider
        TestUtils.setupMockSchedulerProvider(schedulerProvider, Schedulers.trampoline());
        // database helper
        when(dbHelper.getReadableDatabase()).thenReturn(database);
        when(dbHelper.getWritableDatabase()).thenReturn(database);
        // database
        when(database.query(anyString(),
                nullable(String[].class), nullable(String.class), nullable(String[].class),
                nullable(String.class), nullable(String.class), nullable(String.class))
        ).thenReturn(cursor);
    }

    @Before
    public void setupSource() {
        source = new LocalDataRepository(context, dbHelper, schedulerProvider);
    }

    @After
    public void resetMocks() {
        reset(context, dbHelper, schedulerProvider);
    }

    @Test
    public void usesIOScheduler() {
        source.getProjects(false);

        verify(schedulerProvider, only()).io();
    }

    @Test
    public void getsProjectsFromDatabase() {
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.moveToNext()).thenReturn(false);

        int COLUMN_PROJECT_ID = 0;
        int COLUMN_PROJECT_NAME = 1;
        int COLUMN_PROJECT_HEAD = 2;
        int COLUMN_PROJECT_DESC = 3;
        when(cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_ID))
                .thenReturn(COLUMN_PROJECT_ID);
        when(cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_NAME))
                .thenReturn(COLUMN_PROJECT_NAME);
        when(cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_HEAD))
                .thenReturn(COLUMN_PROJECT_HEAD);
        when(cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_DESC))
                .thenReturn(COLUMN_PROJECT_DESC);

        String id = "ID";
        String name = "NAME";
        String head = "HEAD";
        String desc = "DESC";
        when(cursor.getString(COLUMN_PROJECT_ID)).thenReturn(id);
        when(cursor.getString(COLUMN_PROJECT_NAME)).thenReturn(name);
        when(cursor.getString(COLUMN_PROJECT_HEAD)).thenReturn(head);
        when(cursor.getString(COLUMN_PROJECT_DESC)).thenReturn(desc);

        Single<List<Project>> answer = source.getProjects(false);
        TestObserver<List<Project>> testObserver = answer.test();

        verify(dbHelper)
                .getReadableDatabase();
        verify(database)
                .query(eq(ProjectEntry.TABLE_NAME),
                        nullable(String[].class), nullable(String.class), nullable(String[].class),
                        nullable(String.class), nullable(String.class), nullable(String.class));

        // only one value returned
        testObserver.assertValue(projects -> projects.size() == 1);

        // the same values from the database
        Project project = testObserver.values().get(0).get(0);
        Assert.assertEquals(project.getId(), id);
        Assert.assertEquals(project.getName(), name);
        Assert.assertEquals(project.getProjectHead(), head);
        Assert.assertEquals(project.getDesc(), desc);
    }

    @Test
    public void returnsEmptyList_ifDatabaseIsEmpty() {
        when(cursor.moveToFirst()).thenReturn(false);

        Single<List<Project>> answer = source.getProjects(false);

        answer.test().assertValue(List::isEmpty);
    }

    @Test
    public void clearAlwaysCompletes() {
        // On the assumption that SQLiteDatabase.delete() never throws
        source.clearProjects().test().assertComplete();
    }

}
