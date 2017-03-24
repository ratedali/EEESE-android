/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.backend

import android.net.Uri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.ProjectCategory
import org.joda.time.DateTime
import java.lang.reflect.Type

object ServerContract {
    object Projects {
        val CATEGORY_POWER = "power"
        val CATEGORY_TELECOM = "telecom"
        val CATEGORY_ELECTRONICS_CONTROL = "electronics & control"
        val CATEGORY_SOFTWARE = "software"

        data class JSON(var id: String?, var name: String?,
                        var desc: String?, var head: String?,
                        var prerequisites: List<String>, var category: String)

        fun category(category: ProjectCategory): String = when (category) {
            ProjectCategory.POWER -> CATEGORY_POWER
            ProjectCategory.SOFTWARE -> CATEGORY_SOFTWARE
            ProjectCategory.TELECOM -> CATEGORY_TELECOM
            ProjectCategory.ELECTRONICS_CONTROL -> CATEGORY_ELECTRONICS_CONTROL
        }


        fun category(jsonCategory: String): ProjectCategory =
                when (jsonCategory) {
                    CATEGORY_POWER -> ProjectCategory.POWER
                    CATEGORY_TELECOM -> ProjectCategory.TELECOM
                    CATEGORY_SOFTWARE -> ProjectCategory.SOFTWARE
                    CATEGORY_ELECTRONICS_CONTROL -> ProjectCategory.ELECTRONICS_CONTROL
                    else -> throw IllegalArgumentException("unknown category")
                }

        fun project(json: JSON): Project =
                if (json.id != null && json.name != null) {
                    Project(id = json.id!!, name = json.name!!,
                            desc = json.desc, category = category(json.category),
                            head = json.head, prerequisites = json.prerequisites)
                } else {
                    throw IllegalArgumentException("either the project id or name is null")
                }
    }

    object Events {
        data class JSON(var id: String?, var name: String?,
                        var desc: String?, var location: String?, var imageUri: String?,
                        var start: DateTime?, var end: DateTime?)

        class EventDateDeserializer : JsonDeserializer<DateTime> {
            @Throws(JsonParseException::class)
            override fun deserialize(json: JsonElement, typeOfT: Type,
                                     context: JsonDeserializationContext): DateTime =
                    DateTime.parse(json.asString)
        }

        fun event(json: JSON): Event =
                if (json.id != null && json.name != null) {

                    // Extract location form the server format "long,lat"
                    var longitude: String? = null
                    var latitude: String? = null
                    if (json.location != null) {
                        val location = json.location!!.split(",")
                        longitude = location.component1()
                        latitude = location.component2()
                    }

                    val imageUri =
                            if (json.imageUri != null) Uri.parse(json.imageUri)
                            else null
                    Event(id = json.id!!, name = json.name!!, desc = json.desc!!,
                            imageUri = imageUri, longitude = longitude, latitude = latitude,
                            start = json.start, end = json.end)
                } else {
                    throw IllegalArgumentException("either the event id or name is null")
                }

    }

}
