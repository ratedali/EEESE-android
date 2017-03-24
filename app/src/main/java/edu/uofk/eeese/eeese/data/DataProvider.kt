/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import edu.uofk.eeese.eeese.data.DataContract.EventEntry
import edu.uofk.eeese.eeese.data.DataContract.ProjectEntry
import edu.uofk.eeese.eeese.data.database.DatabaseHelper

class DataProvider : ContentProvider() {

    private val PROJECT = 100
    private val PROJECTS = 101
    private val EVENT = 200
    private val EVENTS = 201

    private val dbHelper = lazy { DatabaseHelper(context) }
    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        matcher.addURI(DataContract.CONTENT_AUTHORITY, ProjectEntry.CONTENT_PATH, PROJECTS)
        matcher.addURI(DataContract.CONTENT_AUTHORITY, ProjectEntry.CONTENT_PATH + "/#", PROJECT)
        matcher.addURI(DataContract.CONTENT_AUTHORITY, EventEntry.CONTENT_PATH, EVENTS)
        matcher.addURI(DataContract.CONTENT_AUTHORITY, EventEntry.CONTENT_PATH + "/#", EVENT)
    }

    override fun onCreate(): Boolean = true

    override fun insert(uri: Uri?, values: ContentValues?): Uri = when (matcher.match(uri)) {
        PROJECTS -> {
            val id = dbHelper.value.writableDatabase
                    .insertOrThrow(ProjectEntry.TABLE_NAME, null, values)
            context.contentResolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
            ContentUris.withAppendedId(ProjectEntry.CONTENT_URI, id)
        }
        EVENTS -> {
            val id = dbHelper.value.writableDatabase.insertOrThrow(EventEntry.TABLE_NAME, null, values)
            context.contentResolver.notifyChange(EventEntry.CONTENT_URI, null, false)
            ContentUris.withAppendedId(EventEntry.CONTENT_URI, id)
        }
        else -> throw UnsupportedOperationException("Unknown URI")
    }

    override fun bulkInsert(uri: Uri?, values: Array<out ContentValues>?): Int =
            when (matcher.match(uri)) {
                PROJECTS -> {
                    val db = dbHelper.value.writableDatabase
                    db.beginTransaction()
                    try {
                        val n = values?.fold(0) { acc, values ->
                            db.insertOrThrow(ProjectEntry.TABLE_NAME, null, values)
                            acc + 1
                        } ?: 0
                        db.setTransactionSuccessful()
                        context.contentResolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
                        n
                    } finally {
                        db.endTransaction()
                    }
                }
                EVENTS -> {
                    val db = dbHelper.value.writableDatabase
                    db.beginTransaction()
                    try {
                        val n = values?.fold(0) { acc, values ->
                            db.insertOrThrow(EventEntry.TABLE_NAME, null, values)
                            acc + 1
                        } ?: 0
                        db.setTransactionSuccessful()
                        context.contentResolver.notifyChange(EventEntry.CONTENT_URI, null, false)
                        n
                    } finally {
                        db.endTransaction()
                    }
                }
                else -> throw UnsupportedOperationException("unknown URI")
            }

    @SuppressLint("Recycle")
    override fun query(uri: Uri?, projection: Array<out String>?,
                       selection: String?, selectionArgs: Array<out String>?,
                       orderBy: String?): Cursor = when (matcher.match(uri)) {
        PROJECTS -> dbHelper.value
                .readableDatabase
                .query(ProjectEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, orderBy)
        PROJECT -> {
            val id = ContentUris.parseId(uri)
            dbHelper.value
                    .readableDatabase
                    .query(ProjectEntry.TABLE_NAME, projection,
                            ProjectEntry._ID + " = ?", arrayOf(id.toString()),
                            null, null, orderBy)
        }
        EVENTS -> dbHelper.value
                .readableDatabase
                .query(EventEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, orderBy)
        EVENT -> {
            val id = ContentUris.parseId(uri)
            dbHelper.value
                    .readableDatabase
                    .query(EventEntry.TABLE_NAME, projection,
                            EventEntry._ID + " = ?", arrayOf(id.toString()),
                            null, null, orderBy)
        }
        else -> throw UnsupportedOperationException("Unknown URI")
    }

    override fun update(uri: Uri?, values: ContentValues?,
                        selection: String?, selectionArgs: Array<out String>?): Int =
            when (matcher.match(uri)) {
                PROJECTS -> {
                    val changes = dbHelper.value
                            .writableDatabase
                            .update(ProjectEntry.TABLE_NAME, values, selection, selectionArgs)
                    context.contentResolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
                    changes
                }

                PROJECT -> {
                    val id = ContentUris.parseId(uri)
                    val changes = dbHelper.value
                            .writableDatabase
                            .update(ProjectEntry.TABLE_NAME, values,
                                    "${ProjectEntry._ID} = ?", arrayOf(id.toString()))
                    context.contentResolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
                    changes
                }
                EVENTS -> {
                    val changes = dbHelper.value
                            .writableDatabase
                            .update(EventEntry.TABLE_NAME, values, selection, selectionArgs)
                    context.contentResolver.notifyChange(EventEntry.CONTENT_URI, null, false)
                    changes
                }
                EVENT -> {
                    val id = ContentUris.parseId(uri)
                    val changes = dbHelper.value
                            .writableDatabase
                            .update(EventEntry.TABLE_NAME, values,
                                    "${ProjectEntry._ID} = ?", arrayOf(id.toString()))
                    context.contentResolver.notifyChange(EventEntry.CONTENT_URI, null, false)
                    changes
                }
                else -> throw UnsupportedOperationException("Unknown URI")
            }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int =
            when (matcher.match(uri)) {
                PROJECTS -> {
                    val deletions = dbHelper.value
                            .writableDatabase
                            .delete(ProjectEntry.TABLE_NAME, selection, selectionArgs)
                    context.contentResolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
                    deletions
                }
                PROJECT -> {
                    val id = ContentUris.parseId(uri)
                    val deletions = dbHelper.value
                            .writableDatabase
                            .delete(ProjectEntry.TABLE_NAME,
                                    ProjectEntry._ID + " = ?", arrayOf(id.toString()))
                    context.contentResolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
                    deletions

                }
                EVENTS -> {
                    val deletions = dbHelper.value
                            .writableDatabase
                            .delete(EventEntry.TABLE_NAME, selection, selectionArgs)
                    context.contentResolver.notifyChange(EventEntry.CONTENT_URI, null, false)
                    deletions
                }
                EVENT -> {
                    val id = ContentUris.parseId(uri)
                    val deletions = dbHelper.value
                            .writableDatabase
                            .delete(EventEntry.TABLE_NAME,
                                    EventEntry._ID + " = ?", arrayOf(id.toString()))
                    context.contentResolver.notifyChange(EventEntry.CONTENT_URI, null, false)
                    deletions
                }
                else -> throw UnsupportedOperationException("Unknown URI")
            }

    override fun getType(uri: Uri?): String = when (matcher.match(uri)) {
        PROJECTS -> ProjectEntry.ITEM_DIR_TYPE
        PROJECT -> ProjectEntry.ITEM_TYPE
        EVENTS -> EventEntry.ITEM_DIR_TYPE
        EVENT -> EventEntry.ITEM_TYPE
        else -> throw UnsupportedOperationException("Unknown URI")
    }
}
