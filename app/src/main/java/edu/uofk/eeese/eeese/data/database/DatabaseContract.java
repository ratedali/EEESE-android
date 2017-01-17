package edu.uofk.eeese.eeese.data.database;

import android.provider.BaseColumns;

public final class DatabaseContract {
    private DatabaseContract() {
    }

    public static abstract class ProjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "projects";
        public static final String COLUMN_PROJECT_ID = "projectid";
        public static final String COLUMN_PROJECT_NAME = "name";
        public static final String COLUMN_PROJECT_HEAD = "head";
        public static final String COLUMN_PROJECT_DESC = "desc";
    }

}
