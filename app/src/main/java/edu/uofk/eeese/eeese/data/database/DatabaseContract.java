package edu.uofk.eeese.eeese.data.database;

import android.provider.BaseColumns;

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
    }

    /**
     * The database representation of the prerequisites list
     *
     * @param prereqs the prerequisites list
     * @return the database representation of the list
     */
    public static String databasePrerequistes(List<String> prereqs) {
        // using a comma separated list as the representation
        StringBuilder dbPrereqs = new StringBuilder();
        for (String item : prereqs) {
            dbPrereqs.append(dbPrereqs).append(",");
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
    public static List<String> databasePrerequisitesToList(String dbPrereqs) {
        return Arrays.asList(dbPrereqs.split(","));
    }
}
