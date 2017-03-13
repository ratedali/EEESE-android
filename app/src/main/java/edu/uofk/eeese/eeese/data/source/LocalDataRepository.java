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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.database.DatabaseContract;
import edu.uofk.eeese.eeese.data.database.DatabaseContract.ProjectEntry;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Completable;
import io.reactivex.Single;

@ApplicationScope
@Local
public class LocalDataRepository implements BaseDataRepository {

    @NonNull
    private Context mContext;
    @DrawableRes
    private static final int[] mGalleryRes = {
            R.drawable.gallery_1,
            R.drawable.gallery_2,
            R.drawable.gallery_3,
            R.drawable.gallery_4,
            R.drawable.gallery_5,
            R.drawable.gallery_6,
            R.drawable.gallery_7
    };
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


    @Inject
    public LocalDataRepository(@NonNull Context context,
                               @NonNull SQLiteOpenHelper dbHelper,
                               @NonNull BaseSchedulerProvider schedulerProvider) {
        mContext = context;
        mDbHelper = dbHelper;
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    public Completable insertProject(final Project project) {

        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long id = db.insert(ProjectEntry.TABLE_NAME, null, projectValues(project));
            db.close();
            if (id < 0) {
                // The database returns a negative ID if the insertion failed
                // in that case, an error is indicated using an exception
                throw new IOException("Cannot insert a project to database");
            }
        });

    }

    @Override
    public Completable setProjects(final List<Project> projects) {
        return clearProjects()
                .andThen(Completable.fromAction(() -> insertProjects(projects)));
    }

    @Override
    public Completable insertProjects(final List<Project> projects) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (Project project : projects) {
                    ContentValues values = projectValues(project);
                    long id = db.insert(ProjectEntry.TABLE_NAME, null, values);
                    if (id < 0) {
                        // The database returns a negative ID if the insertion failed
                        // in that case, the transaction is aborted by throwing an exception
                        throw new IOException("Cannot insert projects to database");
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        });
    }

    @Override
    public Completable clearProjects() {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            // Delete all projects and close the database
            db.delete(ProjectEntry.TABLE_NAME, null, null);
            db.close();
        });
    }

    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        return Single.fromCallable(() -> mDbHelper.getReadableDatabase())
                .map(db -> {
                    // The cursor will be closed along with the database by the mapper
                    @SuppressLint("Recycle")
                    Cursor cursor = db.query(ProjectEntry.TABLE_NAME,
                            PROJECTION, null, null,
                            null, null, null);
                    return new Pair<>(cursor, db);
                })
                .map(LocalDataRepository::projects)
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<List<Project>> getProjectsWithCategory(boolean forceUpdate,
                                                         @Project.ProjectCategory
                                                         final int category) {
        return Single.fromCallable(() -> mDbHelper.getReadableDatabase())
                .map(db -> {
                    // The cursor will be closed along with the database by the mapper
                    @SuppressLint("Recycle")
                    Cursor cursor = db.query(ProjectEntry.TABLE_NAME,
                            PROJECTION,
                            ProjectEntry.COLUMN_PROJECT_CATEGORY + " = " + category, // filter using the category
                            null,
                            null, null, null);
                    return new Pair<>(cursor, db);
                })
                .map(LocalDataRepository::projects)
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<Project> getProject(final String projectId, boolean forceUpdate) {
        return Single.fromCallable(() -> mDbHelper.getReadableDatabase())
                .map(db -> {
                    // The cursor will be closed along with the database by the mapper
                    @SuppressLint("Recycle")
                    Cursor cursor = db.query(ProjectEntry.TABLE_NAME,
                            PROJECTION,
                            ProjectEntry.COLUMN_PROJECT_ID + " = ?",
                            new String[]{projectId},
                            null, null, null);
                    return new Pair<>(cursor, db);
                }).map(LocalDataRepository::project)
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<Bitmap> getGalleryImageBitmap(final int width, final int height) {
        @DrawableRes final int galleryRes = mGalleryRes[new Random().nextInt(mGalleryRes.length)];
        return Single.fromCallable(() -> {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), galleryRes, options);
            return options;
        }).map(options -> {
            options.inSampleSize = 1;
            if (options.outHeight > height || options.outWidth > width) {
                int halfHeight = options.outHeight / 2;
                int halfWidth = options.outWidth / 2;
                while ((halfHeight / options.inSampleSize) > height &&
                        (halfWidth / options.inSampleSize) > width) {
                    options.inSampleSize *= 2;
                }
            }
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(mContext.getResources(), galleryRes, options);
        });
    }

    private ContentValues projectValues(Project project) {
        ContentValues values = new ContentValues();
        values.put(ProjectEntry.COLUMN_PROJECT_ID, project.getId());
        values.put(ProjectEntry.COLUMN_PROJECT_NAME, project.getName());
        values.put(ProjectEntry.COLUMN_PROJECT_HEAD, project.getProjectHead());
        values.put(ProjectEntry.COLUMN_PROJECT_DESC, project.getDesc());
        values.put(ProjectEntry.COLUMN_PROJECT_CATEGORY, project.getCategory());
        values.put(ProjectEntry.COLUMN_PROJECT_PREREQS,
                DatabaseContract.databasePrerequistes(project.getPrerequisites()));
        return values;
    }

    /**
     * Reads project data from the current row in the cursor and returns it as a Project object
     * The method expects the cursor to be pointing to an appropriate row,
     * and it does not close the cursor after it it reads the data
     *
     * @param cursor the cursor to read the data from
     * @return a Project object representing the data read from the cursor row
     */
    // category will always be a legal value because its always saved as one
    @SuppressWarnings("WrongConstant")
    private static Project projectFromCursor(Cursor cursor) {
        String id = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_NAME));
        String head = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_HEAD));
        String desc = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_DESC));
        int category = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                        ProjectEntry.COLUMN_PROJECT_CATEGORY));
        List<String> prereqs = DatabaseContract.databasePrerequisitesToList(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                ProjectEntry.COLUMN_PROJECT_PREREQS)));
        return new Project
                .Builder(id, name, head, category)
                .withDesc(desc)
                .withPrerequisites(prereqs)
                .build();
    }

    private static List<Project> projects(Pair<Cursor, SQLiteDatabase> cursorDbPair) {
        Cursor cursor = cursorDbPair.first;
        SQLiteDatabase db = cursorDbPair.second;

        List<Project> projects = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                projects.add(projectFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return projects;
    }

    private static Project project(Pair<Cursor, SQLiteDatabase> cursorDbPair) {
        Cursor cursor = cursorDbPair.first;
        SQLiteDatabase db = cursorDbPair.second;

        if (!cursor.moveToFirst()) {
            throw new RuntimeException("No project exists");
        }
        Project project = projectFromCursor(cursor);

        cursor.close();
        db.close();
        return project;
    }
}
