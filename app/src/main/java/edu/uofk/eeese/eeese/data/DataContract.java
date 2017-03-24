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
import android.net.Uri;
import android.provider.BaseColumns;


public abstract class DataContract {

    public static final String CONTENT_AUTHORITY = "edu.uofk.eeese.eeese.provider";
    private static final Uri BASE_URI =
            new Uri.Builder()
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
        public static final String COLUMN_EVENT_IMAGE_URI = "imageUri";
        public static final String COLUMN_EVENT_LOCATION = "location";
        public static final String COLUMN_EVENT_START_DATE = "start";
        public static final String COLUMN_EVENT_END_DATE = "end";
    }
}
