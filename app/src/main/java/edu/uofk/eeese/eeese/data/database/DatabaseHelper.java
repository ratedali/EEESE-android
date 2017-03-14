/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;

import edu.uofk.eeese.eeese.data.database.DatabaseContract.ProjectEntry;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;

@ApplicationScope
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "projects.db";
    private static final int DATABASE_VERSION = 1;

    @Inject
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_QUERY = "CREATE TABLE " + ProjectEntry.TABLE_NAME + " ( "
                + ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProjectEntry.COLUMN_PROJECT_ID + " TEXT UNIQUE ON CONFLICT REPLACE, "
                + ProjectEntry.COLUMN_PROJECT_NAME + " TEXT NOT NULL, "
                + ProjectEntry.COLUMN_PROJECT_HEAD + " TEXT NOT NULL, "
                + ProjectEntry.COLUMN_PROJECT_DESC + " TEXT, "
                + ProjectEntry.COLUMN_PROJECT_CATEGORY + " INTEGER, "
                + ProjectEntry.COLUMN_PROJECT_PREREQS + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + ProjectEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(DROP_TABLE_QUERY);
        onCreate(sqLiteDatabase);
    }
}
