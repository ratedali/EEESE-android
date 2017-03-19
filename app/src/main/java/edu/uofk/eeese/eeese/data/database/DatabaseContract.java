package edu.uofk.eeese.eeese.data.database;

import android.net.Uri;
import android.provider.BaseColumns;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public final class DatabaseContract {
    private DatabaseContract() {
    }

    public static abstract class ProjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "projects";
        public static final String COLUMN_PROJECT_ID = "projectid";
        public static final String COLUMN_PROJECT_NAME = "name";
        public static final String COLUMN_PROJECT_HEAD = "head";
        public static final String COLUMN_PROJECT_DESC = "desc";
        public static final String COLUMN_PROJECT_CATEGORY = "category";
        public static final String COLUMN_PROJECT_PREREQS = "prereqs";

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
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_EVENT_ID = "eventid";
        public static final String COLUMN_EVENT_NAME = "name";
        public static final String COLUMN_EVENT_DESC = "desc";
        public static final String COLUMN_EVENT_IMAGE_URI = "imageuri";
        public static final String COLUMN_EVENT_LOCATION = "location";
        public static final String COLUMN_EVENT_START_DATE = "start";
        public static final String COLUMN_EVENT_END_DATE = "end";

        /**
         * Get the database representation of the location specified by a longitude and a latitude value
         */
        public static String dbLocation(String longitude, String latitude) {
            if (longitude != null && latitude != null &&
                    !longitude.isEmpty() && !latitude.isEmpty()) {
                return longitude + "," + latitude;
            }
            return "";
        }

        /**
         * Get the longitude from the location represented by the database location
         */
        public static String longitudeFromDBLocation(String dbLocation) {
            if (!dbLocation.isEmpty()) {
                return dbLocation.split(",")[0];
            }
            return null;
        }

        /**
         * Get the latitude from the location represented by the database location
         */
        public static String latitudeFromDBLocation(String dbLocation) {
            if (!dbLocation.isEmpty()) {
                return dbLocation.split(",")[1];
            }
            return null;
        }

        public static String dbUri(Uri imageUri) {
            if (imageUri != null) {
                return imageUri.toString();
            }
            return "";
        }

        public static Uri imageUriFromDBUri(String dbUri) {
            if (!dbUri.isEmpty()) {
                return Uri.parse(dbUri);
            }
            return null;
        }

        public static String dbDate(DateTime date) {
            if (date == null) {
                return "";
            }
            return ISODateTimeFormat.dateTime()
                    .print(date);
        }

        public static DateTime dateFromDBDate(String dbDate) throws ParseException {
            if (dbDate.equals("")) {
                return null;
            }
            return ISODateTimeFormat.dateTime().parseDateTime(dbDate);
        }
    }
}
