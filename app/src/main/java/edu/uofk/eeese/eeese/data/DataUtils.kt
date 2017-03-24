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

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.text.ParseException

object DataUtils {
    object Projects {

        fun values(project: Project): ContentValues {
            val values = ContentValues()
            values.put(DataContract.ProjectEntry.COLUMN_PROJECT_ID, project.id)
            values.put(DataContract.ProjectEntry.COLUMN_PROJECT_NAME, project.name)
            values.put(DataContract.ProjectEntry.COLUMN_PROJECT_HEAD, project.head)
            values.put(DataContract.ProjectEntry.COLUMN_PROJECT_DESC, project.desc)
            values.put(DataContract.ProjectEntry.COLUMN_PROJECT_CATEGORY, category(project.category))
            values.put(DataContract.ProjectEntry.COLUMN_PROJECT_PREREQS, prerequisites(project.prerequisites))
            return values
        }

        fun projects(cursor: Cursor): List<Project> {
            val projects = mutableListOf<Project>()
            if (cursor.moveToFirst()) {
                do {
                    projects.add(projectFromRow(cursor))
                } while (cursor.moveToNext())
            }

            cursor.close()
            return projects
        }

        fun project(cursor: Cursor): Project {
            if (!cursor.moveToFirst()) {
                throw RuntimeException("No project exists")
            }
            val project = projectFromRow(cursor)

            cursor.close()
            return project
        }


        /**
         * Reads project data from the current row in the cursor and returns it as a [Project].
         * The method expects the cursor to be pointing to an appropriate row,
         * and it does not close the cursor after it it reads the data

         * @param cursor the cursor to read the data from
         * *
         * @return a [Project] object representing the data read from the cursor row
         */
        // category will always be a legal value because its always saved as one
        private fun projectFromRow(cursor: Cursor): Project {
            val id = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.ProjectEntry.COLUMN_PROJECT_ID))
            val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.ProjectEntry.COLUMN_PROJECT_NAME))
            val head = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.ProjectEntry.COLUMN_PROJECT_HEAD))
            val desc = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.ProjectEntry.COLUMN_PROJECT_DESC))
            val category = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DataContract.ProjectEntry.COLUMN_PROJECT_CATEGORY))
            val prerequisites = prerequisites(cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.ProjectEntry.COLUMN_PROJECT_PREREQS)))
            return Project(id = id, name = name, head = head,
                    desc = desc, category = category(category),
                    prerequisites = prerequisites)
        }

        const val CATEGORY_SOFTWARE = 0
        const val CATEGORY_POWER = 1
        const val CATEGORY_TELECOM = 2
        const val CATEGORY_ELECTRONICS_CONTROL = 3

        fun category(category: ProjectCategory): Int = when (category) {
            ProjectCategory.SOFTWARE -> CATEGORY_SOFTWARE
            ProjectCategory.POWER -> CATEGORY_POWER
            ProjectCategory.TELECOM -> CATEGORY_TELECOM
            ProjectCategory.ELECTRONICS_CONTROL -> CATEGORY_ELECTRONICS_CONTROL
        }

        fun category(category: Int): ProjectCategory = when (category) {
            CATEGORY_SOFTWARE -> ProjectCategory.SOFTWARE
            CATEGORY_POWER -> ProjectCategory.POWER
            CATEGORY_TELECOM -> ProjectCategory.TELECOM
            CATEGORY_ELECTRONICS_CONTROL -> ProjectCategory.ELECTRONICS_CONTROL
            else -> throw IllegalArgumentException("unknown category $category")
        }

        /**
         * The database representation of the prerequisites list

         * @param prerequisites the prerequisites list
         * *
         * @return the database representation of the list
         */
        fun prerequisites(prerequisites: List<String>) = prerequisites.joinToString(",")

        /**
         * the prerequisites list represented by the database string

         * @param prerequisites the string stored in the database
         * *
         * @return the prerequisites list
         */
        fun prerequisites(prerequisites: String): List<String> = prerequisites.split(",")
    }

    object Events {
        fun values(event: Event): ContentValues {
            val values = ContentValues()
            values.put(DataContract.EventEntry.COLUMN_EVENT_ID, event.id)
            values.put(DataContract.EventEntry.COLUMN_EVENT_NAME, event.name)
            values.put(DataContract.EventEntry.COLUMN_EVENT_DESC, event.desc)
            values.put(DataContract.EventEntry.COLUMN_EVENT_LOCATION,
                    location(event.longitude, event.latitude))
            values.put(DataContract.EventEntry.COLUMN_EVENT_IMAGE_URI,
                    imageUri(event.imageUri))
            values.put(DataContract.EventEntry.COLUMN_EVENT_START_DATE,
                    date(event.start))
            values.put(DataContract.EventEntry.COLUMN_EVENT_END_DATE,
                    date(event.end))
            return values

        }

        fun events(cursor: Cursor): List<Event> {
            val events = mutableListOf<Event>()
            if (cursor.moveToFirst()) {
                do {
                    events.add(eventFromRow(cursor))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return events
        }

        fun event(cursor: Cursor): Event {
            if (!cursor.moveToFirst()) {
                throw RuntimeException("No Event Exist")
            }
            val event = eventFromRow(cursor)
            cursor.close()
            return event
        }

        /**
         * Reads project data from the current row in the cursor and returns it as a [Event].
         * The method expects the cursor to be pointing to an appropriate row,
         * and it does not close the cursor after it it reads the data

         * @param cursor the cursor to read the data from
         * *
         * @return a [Event] object representing the data read from the cursor row
         */
        private fun eventFromRow(cursor: Cursor): Event {
            val id = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_ID))
            val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_NAME))
            val desc = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_DESC))
            val location = cursor.getString(
                    cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_LOCATION))
            val (longitude, latitude) = location(location)
            val imageUri = imageUri(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_IMAGE_URI)))

            var start: DateTime? = null
            try {
                start = date(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_START_DATE)))
            } catch (ignored: ParseException) {
            }

            var end: DateTime? = null
            try {
                end = date(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(DataContract.EventEntry.COLUMN_EVENT_END_DATE)))
            } catch (ignored: ParseException) {
            }

            return Event(id = id, name = name, desc = desc,
                    imageUri = imageUri, longitude = longitude, latitude = latitude,
                    start = start, end = end)
        }

        /**
         * Get the database representation of the location specified by a longitude and a latitude value
         */
        private fun location(longitude: String?, latitude: String?): String =
                if (longitude != null && latitude != null
                        && !longitude.isEmpty()
                        && !latitude.isEmpty()) longitude + "," + latitude
                else ""

        /**
         * Get the longitude, latitude pair from the location represented by the database location
         */
        private fun location(location: String): Pair<String?, String?> =
                if (location.isNotEmpty()) {
                    val (longitude, latitude) = location.split(",")
                    Pair(longitude, latitude)
                } else {
                    Pair(null, null)
                }


        private fun imageUri(uri: Uri?): String = uri?.toString() ?: ""
        private fun imageUri(uri: String): Uri? =
                if (!uri.isEmpty()) {
                    Uri.parse(uri)
                } else {
                    null
                }


        private fun date(date: DateTime?): String =
                if (date != null) ISODateTimeFormat.dateTime().print(date)
                else ""

        @Throws(ParseException::class)
        private fun date(date: String): DateTime? =
                if (date.isNotEmpty()) ISODateTimeFormat.dateTime().parseDateTime(date)
                else null
    }
}
