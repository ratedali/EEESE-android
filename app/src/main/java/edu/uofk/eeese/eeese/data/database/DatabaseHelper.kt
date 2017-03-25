/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import edu.uofk.eeese.eeese.data.DataContract
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class DatabaseHelper @Inject
constructor(context: Context) : SQLiteOpenHelper(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION) {


    companion object {
        private const val DATABASE_NAME = "eeese.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PROJECTS_TABLE_QUERY =
                "CREATE TABLE ${DataContract.ProjectEntry.TABLE_NAME} " +
                        "(" +
                        "${DataContract.ProjectEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${DataContract.ProjectEntry.COLUMN_PROJECT_ID} " +
                        "TEXT UNIQUE ON CONFLICT REPLACE, " +
                        "${DataContract.ProjectEntry.COLUMN_PROJECT_NAME} TEXT NOT NULL, " +
                        "${DataContract.ProjectEntry.COLUMN_PROJECT_HEAD} TEXT, " +
                        "${DataContract.ProjectEntry.COLUMN_PROJECT_DESC} TEXT, " +
                        "${DataContract.ProjectEntry.COLUMN_PROJECT_CATEGORY} INTEGER, " +
                        "${DataContract.ProjectEntry.COLUMN_PROJECT_PREREQS} TEXT" +
                        ")"

        val CREATE_EVENTS_TABLE_QUERY =
                "CREATE TABLE ${DataContract.EventEntry.TABLE_NAME}" +
                        "( " +
                        "${DataContract.EventEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_ID} " +
                        "TEXT UNIQUE ON CONFLICT REPLACE, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_NAME} TEXT NOT NULL, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_DESC} TEXT, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_IMAGE_URI} TEXT, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_LOCATION} TEXT, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_START_DATE} TEXT, " +
                        "${DataContract.EventEntry.COLUMN_EVENT_END_DATE} TEXT" +
                        ")"

        db.execSQL(CREATE_PROJECTS_TABLE_QUERY)
        db.execSQL(CREATE_EVENTS_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        val DROP_PROJECTS_TABLE_QUERY =
                "DROP TABLE IF EXISTS ${DataContract.ProjectEntry.TABLE_NAME}"
        val DROP_EVENTS_TABLE_QUERY =
                "DROP TABLE IF EXISTS ${DataContract.EventEntry.TABLE_NAME}"
        db.execSQL(DROP_PROJECTS_TABLE_QUERY)
        db.execSQL(DROP_EVENTS_TABLE_QUERY)
        onCreate(db)
    }
}
