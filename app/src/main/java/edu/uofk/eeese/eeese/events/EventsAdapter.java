/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.events;

import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.transitionseverywhere.TransitionManager;

import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.data.Event;
import edu.uofk.eeese.eeese.util.ActivityUtils;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> mEvents;
    private boolean[] mExpanded;
    private PublishSubject<Pair<String, String>> mLocationClicks;

    public EventsAdapter(List<Event> events) {
        mEvents = events == null ? new ArrayList<>() : events;
        mExpanded = new boolean[mEvents.size()];
        Arrays.fill(mExpanded, false);
        mLocationClicks = PublishSubject.create();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mEvents.get(position);
        holder.name.setText(event.getName());
        holder.desc.setText(event.getDesc());

        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        if (event.getStartDate() != null) {
            Date start = event.getStartDate()
                    .withZone(DateTimeZone.getDefault())
                    .toDate();
            holder.startDate.setText(dateFormat.format(start));
            holder.startTime.setVisibility(VISIBLE);
            holder.startTime.setText(timeFormat.format(start));
        } else {
            holder.startDate.setText(R.string.unspecified);
            holder.startTime.setVisibility(View.INVISIBLE);
        }

        if (event.getEndDate() != null) {
            Date end = event.getEndDate()
                    .withZone(DateTimeZone.getDefault())
                    .toDate();
            holder.endDate.setText(dateFormat.format(end));
            holder.endTime.setVisibility(VISIBLE);
            holder.endTime.setText(timeFormat.format(end));
        } else {
            holder.endDate.setText(R.string.unspecified);
            holder.endTime.setVisibility(View.INVISIBLE);
        }


        holder.longitude = event.getLongitude();
        holder.latitude = event.getLatitude();

        if (holder.longitude != null && holder.latitude != null) {
            holder.locationButton.setVisibility(VISIBLE);
            holder.locationButton.setOnClickListener(view ->
                    mLocationClicks.onNext(new Pair<>(holder.longitude, holder.latitude))
            );
        } else {
            holder.locationButton.setVisibility(GONE);
        }
        if (event.getImageUri() != null) {
            holder.image.setVisibility(VISIBLE);
            Picasso.with(holder.itemView.getContext())
                    .load(event.getImageUri())
                    .into(holder.image);
        } else {
            holder.image.setVisibility(GONE);
        }

        if (mExpanded[position]) {
            holder.showExtraInfo();
        } else {
            holder.hideExtraInfo();
        }

        holder.itemView.setOnClickListener(view -> {
            int p = holder.getAdapterPosition();
            if (mExpanded[p]) {
                holder.hideExtraInfo();
                mExpanded[p] = false;
            } else {
                holder.showExtraInfo();
                mExpanded[p] = true;

            }

        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public Observable<Pair<String, String>> locationClicks() {
        return mLocationClicks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.event_image)
        public ImageView image;
        @BindView(R.id.event_name)
        public TextView name;
        @BindView(R.id.event_desc)
        public TextView desc;
        @BindView(R.id.extra_info)
        public View extraInfo;
        @BindView(R.id.start_date)
        public TextView startDate;
        @BindView(R.id.start_time)
        public TextView startTime;
        @BindView(R.id.end_date)
        public TextView endDate;
        @BindView(R.id.end_time)
        public TextView endTime;
        @BindView(R.id.location_button)
        public Button locationButton;

        public String longitude;
        public String latitude;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void showExtraInfo() {
            TransitionManager.beginDelayedTransition((ViewGroup) itemView);

            extraInfo.setVisibility(VISIBLE);
            if (ActivityUtils.atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
                itemView.setTranslationZ(4);
            }
        }

        public void hideExtraInfo() {
            TransitionManager.beginDelayedTransition((ViewGroup) itemView);
            extraInfo.setVisibility(GONE);
            if (ActivityUtils.atLeastApi(Build.VERSION_CODES.LOLLIPOP)) {
                itemView.setTranslationZ(0);
            }
        }
    }
}
