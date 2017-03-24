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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class DataContract {
    private DataContract() {
    }

    public static final String CONTENT_AUTHORITY = "edu.uofk.eeese.eeese.provider";
    public static final Uri BASE_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(CONTENT_AUTHORITY)
            .build();

    public static abstract class ProjectEntry implements BaseColumns {

        public static final String CONTENT_PATH = "projects";
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(CONTENT_PATH)
                .build();
        public static final String ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.edu.uofk.eeese.eeese.project";
        public static final String ITEM_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.edu.uofk.eeese.eeese.project";


        public static final String TABLE_NAME = "projects";
        public static final String COLUMN_PROJECT_ID = "projectid";
        public static final String COLUMN_PROJECT_NAME = "name";
        public static final String COLUMN_PROJECT_HEAD = "head";
        public static final String COLUMN_PROJECT_DESC = "desc";
        public static final String COLUMN_PROJECT_CATEGORY = "category";
        public static final String COLUMN_PROJECT_PREREQS = "prereqs";

        /*
        Helper functions
         */

        public static ContentValues values(Project project) {
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

        public static List<Project> projects(Cursor cursor) {
            List<Project> projects = new LinkedList<>();
            if (cursor.moveToFirst()) {
                do {
                    projects.add(projectFromRow(cursor));
                } while (cursor.moveToNext());
            }

            cursor.close();
            return projects;
        }

        public static Project project(Cursor cursor) {
            if (!cursor.moveToFirst()) {
                throw new RuntimeException("No project exists");
            }
            Project project = projectFromRow(cursor);

            cursor.close();
            return project;
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
         * The database representation of the prerequisites list
         *
         * @param prereqs the prerequisites list
         * @return the database representation of the list
         */
        public static String dbPrereq(List<String> prereqs) {
            // using a comma separated list as the representation
            StringBuilder dbPrereqs = new StringBuilder();
            for (String item : prereqs) {
                dbPrereqs.append(item).append(",");
            }
            if (dbPrereqs.length() > 0) {
                // remove the trailing comma
                dbPrereqs.deleteCharAt(dbPrereqs.length() - 1);
            }
            return dbPrereqs.toString();
        }

        /**
         * the prerequisites list represented by the database string
         *
         * @param dbPrereqs the string stored in the database
         * @return the prerequisites list
         */
        public static List<String> prereqListFromDBPrereq(String dbPrereqs) {
            return Arrays.asList(dbPrereqs.split(","));
        }
    }

    public static abstract class EventEntry implements BaseColumns {

        public static final String CONTENT_PATH = "events";
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(CONTENT_PATH)
                .build();

        public static final String ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.edu.uofk.eeese.eeese.event";
        public static final String ITEM_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.edu.uofk.eeese.eeese.event";

        public static final String TABLE_NAME = "events";
        public static final String COLUMN_EVENT_ID = "eventid";
        public static final String COLUMN_EVENT_NAME = "name";
        public static final String COLUMN_EVENT_DESC = "desc";
        public static final String COLUMN_EVENT_IMAGE_URI = "imageuri";
        public static final String COLUMN_EVENT_LOCATION = "location";
        public static final String COLUMN_EVENT_START_DATE = "start";
        public static final String COLUMN_EVENT_END_DATE = "end";

        /*
        Helper functions
         */

        public static ContentValues values(Event event) {
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

        public static List<Event> events(Cursor cursor) {
            List<Event> events = new LinkedList<>();
            if (cursor.moveToFirst()) {
                do {
                    events.add(eventFromRow(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return events;
        }

        public static Event event(Cursor cursor) {
            if (!cursor.moveToFirst()) {
                throw new RuntimeException("No Event Exist");
            }
            Event event = eventFromRow(cursor);
            cursor.close();
            return event;
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

        /**
         * Get the database representation of the location specified by a longitude and a latitude value
         */
        private static String dbLocation(String longitude, String latitude) {
            if (longitude != null && latitude != null &&
                    !longitude.isEmpty() && !latitude.isEmpty()) {
                return longitude + "," + latitude;
            }
            return "";
        }

        /**
         * Get the longitude from the location represented by the database location
         */
        private static String longitudeFromDBLocation(String dbLocation) {
            if (!dbLocation.isEmpty()) {
                return dbLocation.split(",")[0];
            }
            return null;
        }

        /**
         * Get the latitude from the location represented by the database location
         */
        private static String latitudeFromDBLocation(String dbLocation) {
            if (!dbLocation.isEmpty()) {
                return dbLocation.split(",")[1];
            }
            return null;
        }

        private static String dbUri(Uri imageUri) {
            if (imageUri != null) {
                return imageUri.toString();
            }
            return "";
        }

        private static Uri imageUriFromDBUri(String dbUri) {
            if (!dbUri.isEmpty()) {
                return Uri.parse(dbUri);
            }
            return null;
        }

        private static String dbDate(DateTime date) {
            if (date == null) {
                return "";
            }
            return ISODateTimeFormat.dateTime()
                    .print(date);
        }

        private static DateTime dateFromDBDate(String dbDate) throws ParseException {
            if (dbDate.equals("")) {
                return null;
            }
            return ISODateTimeFormat.dateTime().parseDateTime(dbDate);
        }
    }

    public static abstract class ApplyInfo {
        public static final String CONTENT_PATH = "apply-info";
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(CONTENT_PATH)
                .build();

        public static final String PREFERENCE_NAME = "application-info";

        public static final String PROJECT_APPLICATION_START_KEY = "apply-start";
        public static final String PROJECT_APPLICATION_END_KEY = "apply-end";
        public static final String PARTICIPATION_START_KEY = "participation-start";
        public static final String PARTICIPATION_END_KEY = "participation-end";
    }
}
