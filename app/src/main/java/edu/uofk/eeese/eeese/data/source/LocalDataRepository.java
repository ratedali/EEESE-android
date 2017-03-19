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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.data.Project;
import edu.uofk.eeese.eeese.data.database.DatabaseContract.EventEntry;
import edu.uofk.eeese.eeese.data.database.DatabaseContract.ProjectEntry;
import edu.uofk.eeese.eeese.di.categories.Local;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposables;

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
            db.insertOrThrow(ProjectEntry.TABLE_NAME, null, projectValues(project));
        }).subscribeOn(mSchedulerProvider.io());

    }

    @Override
    public Completable insertProjects(final List<Project> projects) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (Project project : projects) {
                    ContentValues values = projectValues(project);
                    db.insertOrThrow(ProjectEntry.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }).subscribeOn(mSchedulerProvider.io());
    }


    @Override
    public Completable setProjects(final List<Project> projects) {
        return clearProjects()
                .andThen(Completable.fromAction(() -> insertProjects(projects)))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable setProjects(List<Project> projects, @Project.ProjectCategory int category) {
        return clearProjects(category)
                .andThen(insertProjects(projects))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable clearProjects() {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(ProjectEntry.TABLE_NAME, null, null);
        }).subscribeOn(mSchedulerProvider.io());
    }


    @Override
    public Completable clearProjects(@Project.ProjectCategory int category) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(ProjectEntry.TABLE_NAME,
                    ProjectEntry.COLUMN_PROJECT_CATEGORY + " = ?",
                    new String[]{String.valueOf(category)});
        }).subscribeOn(mSchedulerProvider.io());

    }

    @NonNull
    @Override
    public Single<List<Project>> getProjects(boolean forceUpdate) {
        return Single.fromCallable(mDbHelper::getReadableDatabase)
                .map(db -> {
                    // The cursor will be closed by the mapper
                    return db.query(ProjectEntry.TABLE_NAME,
                            null,
                            null, null,
                            null, null, null);
                })
                .map(LocalDataRepository::projects)
                .subscribeOn(mSchedulerProvider.io());
    }

    @NonNull
    @Override
    public Single<List<Project>> getProjectsWithCategory(boolean forceUpdate,
                                                         @Project.ProjectCategory
                                                         final int category) {
        return Single.fromCallable(() -> mDbHelper.getReadableDatabase())
                .map(db -> {
                    // The cursor will be closed by the mapper
                    return db.query(ProjectEntry.TABLE_NAME,
                            null,
                            ProjectEntry.COLUMN_PROJECT_CATEGORY + " = ?", // filter using the category
                            new String[]{String.valueOf(category)},
                            null, null, null);
                })
                .map(LocalDataRepository::projects)
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<Project> getProject(final String projectId, boolean forceUpdate) {
        return Single.fromCallable(() -> mDbHelper.getReadableDatabase())
                .map(db -> {
                    // The cursor will be closed by the mapper
                    return db.query(ProjectEntry.TABLE_NAME,
                            null,
                            ProjectEntry.COLUMN_PROJECT_ID + " = ?",
                            new String[]{projectId},
                            null, null, null);
                }).map(LocalDataRepository::project)
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable insertEvent(Event event) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.insertOrThrow(EventEntry.TABLE_NAME, null, eventValues(event));
        }).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable insertEvents(List<Event> events) {
        return Completable.create(emitter -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            emitter.setDisposable(Disposables.fromAction(db::endTransaction));
            db.beginTransaction();
            for (Event event : events) {
                db.insertOrThrow(EventEntry.TABLE_NAME, null, eventValues(event));
            }
            db.setTransactionSuccessful();
            emitter.onComplete();
        }).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable setEvents(List<Event> events) {
        return clearEvents()
                .andThen(Completable.defer(() -> insertEvents(events)))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable clearEvents() {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(EventEntry.TABLE_NAME, null, null);
        }).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<Event> getEvent(String eventId, boolean forceUpdate) {
        return Single.fromCallable(mDbHelper::getReadableDatabase)
                .map(db -> db.query(EventEntry.TABLE_NAME, null,
                        EventEntry.COLUMN_EVENT_ID + " = ?",
                        new String[]{eventId},
                        null, null, null))
                .map(LocalDataRepository::event)
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<List<Event>> getEvents(boolean forceUpdate) {
        return Single.fromCallable(mDbHelper::getReadableDatabase)
                .map(db -> db.query(EventEntry.TABLE_NAME, null,
                        null, null,
                        null, null, null))
                .map(LocalDataRepository::events)
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
                ProjectEntry.dbPrereq(project.getPrerequisites()));
        return values;
    }

    private ContentValues eventValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_EVENT_ID, event.getId());
        values.put(EventEntry.COLUMN_EVENT_NAME, event.getName());
        values.put(EventEntry.COLUMN_EVENT_DESC, event.getDesc());
        values.put(EventEntry.COLUMN_EVENT_LOCATION,
                EventEntry.dbLocation(event.getLongitude(), event.getLatitude()));
        values.put(EventEntry.COLUMN_EVENT_IMAGE_URI,
                EventEntry.dbUri(event.getImageUri()));
        values.put(EventEntry.COLUMN_EVENT_START_DATE,
                EventEntry.dbDate(event.getStartDate()));
        values.put(EventEntry.COLUMN_EVENT_END_DATE,
                EventEntry.dbDate(event.getEndDate()));
        return values;

    }

    /**
     * Reads project data from the current row in the cursor and returns it as a {@link Project}.
     * The method expects the cursor to be pointing to an appropriate row,
     * and it does not close the cursor after it it reads the data
     *
     * @param cursor the cursor to read the data from
     * @return a {@link Project} object representing the data read from the cursor row
     */
    // category will always be a legal value because its always saved as one
    @SuppressWarnings("WrongConstant")
    private static Project projectFromRow(Cursor cursor) {
        String id = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_NAME));
        String head = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_HEAD));
        String desc = cursor.getString(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_DESC));
        int category = cursor.getInt(
                cursor.getColumnIndexOrThrow(ProjectEntry.COLUMN_PROJECT_CATEGORY));
        List<String> prereqs = ProjectEntry.prereqListFromDBPrereq(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                ProjectEntry.COLUMN_PROJECT_PREREQS)));
        return new Project
                .Builder(id, name, head, category)
                .withDesc(desc)
                .withPrerequisites(prereqs)
                .build();
    }

    /**
     * Reads project data from the current row in the cursor and returns it as a {@link Event}.
     * The method expects the cursor to be pointing to an appropriate row,
     * and it does not close the cursor after it it reads the data
     *
     * @param cursor the cursor to read the data from
     * @return a {@link Event} object representing the data read from the cursor row
     */
    private static Event eventFromRow(Cursor cursor) {
        String id = cursor.getString(
                cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_NAME));
        String desc = cursor.getString(
                cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_DESC));
        String location = cursor.getString(
                cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_LOCATION));
        String longitude = EventEntry.longitudeFromDBLocation(location);
        String latitude = EventEntry.latitudeFromDBLocation(location);
        Uri imageUri = EventEntry.imageUriFromDBUri(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_IMAGE_URI)));

        DateTime startDate = null;
        try {
            startDate = EventEntry.dateFromDBDate(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_START_DATE)));
        } catch (ParseException ignored) {
        }

        DateTime endDate = null;
        try {
            endDate = EventEntry.dateFromDBDate(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(EventEntry.COLUMN_EVENT_END_DATE)));
        } catch (ParseException ignored) {
        }

        return new Event.Builder(id, name)
                .description(desc)
                .location(longitude, latitude)
                .imageUri(imageUri)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private static List<Project> projects(Cursor cursor) {
        List<Project> projects = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                projects.add(projectFromRow(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return projects;
    }

    private static Project project(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            throw new RuntimeException("No project exists");
        }
        Project project = projectFromRow(cursor);

        cursor.close();
        return project;
    }

    private static List<Event> events(Cursor cursor) {
        List<Event> events = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                events.add(eventFromRow(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }

    private static Event event(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            throw new RuntimeException("No Event Exist");
        }
        Event event = eventFromRow(cursor);
        cursor.close();
        return event;
    }
}
