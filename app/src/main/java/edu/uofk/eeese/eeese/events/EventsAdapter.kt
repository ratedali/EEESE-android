/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.events

import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.squareup.picasso.Picasso
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.Event
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.event_list_item.view.*
import org.joda.time.DateTimeZone
import java.text.DateFormat

class EventsAdapter constructor(events: List<Event>? = emptyList<Event>()) :
        RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private val events = events ?: ArrayList<Event>()
    private val locationClicksSubject = PublishSubject.create<Pair<String, String>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.name.text = event.name
        holder.desc.text = event.desc

        val dateFormat = DateFormat.getDateInstance()
        val timeFormat = DateFormat.getTimeInstance()

        if (event.startDate != null) {
            val start = event.startDate!!.withZone(DateTimeZone.getDefault()).toDate()
            holder.startDate.text = dateFormat.format(start)
            holder.startTime.visibility = View.VISIBLE
            holder.startTime.text = timeFormat.format(start)
        } else {
            holder.startDate.setText(R.string.unspecified)
            holder.startTime.visibility = View.INVISIBLE
        }

        if (event.endDate != null) {
            val end = event.endDate!!
                    .withZone(DateTimeZone.getDefault())
                    .toDate()
            holder.endDate.text = dateFormat.format(end)
            holder.endTime.visibility = View.VISIBLE
            holder.endTime.text = timeFormat.format(end)
        } else {
            holder.endDate.setText(R.string.unspecified)
            holder.endTime.visibility = View.INVISIBLE
        }


        holder.longitude = event.longitude
        holder.latitude = event.latitude

        if (holder.longitude != null && holder.latitude != null) {
            holder.locationButton.visibility = View.VISIBLE
            RxView.clicks(holder.locationButton)
                    .map { Pair<String, String>(holder.latitude, holder.longitude) }
                    .subscribe(locationClicksSubject)
        } else {
            holder.locationButton.visibility = View.GONE
        }

        if (event.imageUri != null) {
            holder.image.visibility = View.VISIBLE
            Picasso.with(holder.itemView.context)
                    .load(event.imageUri)
                    .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = events.size

    fun locationClicks(): Observable<Pair<String, String>> = locationClicksSubject


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.event_image
        val name: TextView = itemView.event_name
        val desc: TextView = itemView.event_desc

        val startDate: TextView = itemView.start_date
        val startTime: TextView = itemView.start_time
        val endDate: TextView = itemView.end_date

        val endTime: TextView = itemView.end_time
        val locationButton: Button = itemView.location_button

        var longitude: String? = null
        var latitude: String? = null
    }
}
