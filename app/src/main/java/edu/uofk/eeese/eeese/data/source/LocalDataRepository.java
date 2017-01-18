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
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.database.DatabaseContract.ProjectEntry;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

@ApplicationScope
@Local
public class LocalDataRepository implements DataRepository {

    @NonNull
    private Context mContext;
    @NonNull
    private SQLiteOpenHelper mDbHelper;
    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;
    private final String[] PROJECTION = {
            ProjectEntry.COLUMN_PROJECT_ID,
            ProjectEntry.COLUMN_PROJECT_NAME,
            ProjectEntry.COLUMN_PROJECT_HEAD,
            ProjectEntry.COLUMN_PROJECT_DESC,
    };

    private final Function<Cursor, List<Project>> projectsMapper = new Function<Cursor, List<Project>>() {
        @Override
        public List<Project> apply(Cursor cursor) throws Exception {
            List<Project> projects = new LinkedList<>();
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(
                            cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_ID));
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_NAME));
                    String head = cursor.getString(
                            cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_HEAD));
                    String desc = cursor.getString(
                            cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_DESC));
                    projects.add(new Project.Builder(id, name, head).withDesc(desc).build());
                } while (cursor.moveToNext());
            }
            cursor.close();
            return projects;
        }
    };

    private final Function<Cursor, Project> singleProjectMapper = new Function<Cursor, Project>() {
        @Override
        public Project apply(Cursor cursor) throws Exception {
            if (!cursor.moveToFirst()) {
                throw new RuntimeException("No project exists");
            }
            String id = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_ID));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_NAME));
            String head = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_HEAD));
            String desc = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_DESC));
            return new Project.Builder(id, name, head).withDesc(desc).build();
        }
    };

    @Inject
    public LocalDataRepository(@NonNull Context context,
                               @NonNull SQLiteOpenHelper dbHelper,
                               @NonNull BaseSchedulerProvider schedulerProvider) {
        mContext = context;
        mDbHelper = dbHelper;
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    public Observable<String> getBasicInfo() {
        return Observable.just(mContext.getString(R.string.basic_info));
    }

    @Override
    public Observable<List<Project>> getProjects(boolean forceUpdate) {
        return Observable.fromCallable(new Callable<SQLiteDatabase>() {
            @Override
            public SQLiteDatabase call() throws Exception {
                return mDbHelper.getReadableDatabase();
            }
        }).map(new Function<SQLiteDatabase, Cursor>() {
            @Override
            public Cursor apply(SQLiteDatabase sqLiteDatabase) throws Exception {
                Cursor cursor = sqLiteDatabase.query(ProjectEntry.TABLE_NAME,
                        PROJECTION, null, null,
                        null, null, null);
                sqLiteDatabase.close();
                return cursor;
            }
        }).map(projectsMapper).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Observable<Project> getProject(final String projectId, boolean forceUpdate) {
        return Observable.fromCallable(new Callable<SQLiteDatabase>() {
            @Override
            public SQLiteDatabase call() throws Exception {
                return mDbHelper.getReadableDatabase();
            }
        }).map(new Function<SQLiteDatabase, Cursor>() {
            @Override
            public Cursor apply(SQLiteDatabase sqLiteDatabase) throws Exception {
                Cursor cursor = sqLiteDatabase.query(ProjectEntry.TABLE_NAME,
                        PROJECTION, ProjectEntry.COLUMN_PROJECT_ID + " = ?", new String[]{projectId},
                        null, null, null);
                sqLiteDatabase.close();
                return cursor;
            }
        }).map(singleProjectMapper).subscribeOn(mSchedulerProvider.io());
    }

}
