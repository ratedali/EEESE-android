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

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import edu.uofk.eeese.eeese.util.Utils;

public class Event {
    @NonNull
    private String mId;
    @NonNull
    private String mName;
    @NonNull
    private String mDesc;
    @Nullable
    private Uri mImageUri;
    @Nullable
    private String mLongitude;
    @Nullable
    private String mLatitude;
    @Nullable
    private DateTime mStartDate;
    @Nullable
    private DateTime mEndDate;

    private Event(String id,
                  String name,
                  String desc,
                  Uri imageUri,
                  String longitude,
                  String latitude,
                  DateTime startDate,
                  DateTime endDate) {
        mId = id;
        mName = name;
        mDesc = desc;
        mImageUri = imageUri;
        mLongitude = longitude;
        mLatitude = latitude;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public static class Builder {

        private String eventId;
        private String eventName;
        private String eventDesc;
        private Uri imageUri;
        private String eventLongitude;
        private String eventLatitude;
        private DateTime eventStart;
        private DateTime eventEnd;
        public Builder(@NonNull String id, @NonNull String name) {
            eventId = id;
            eventName = name;
            eventDesc = "";
        }

        public Event build() {
            return new Event(eventId, eventName, eventDesc,
                    imageUri, eventLongitude, eventLatitude,
                    eventStart, eventEnd);
        }

        public Builder description(@NonNull String desc) {
            eventDesc = desc;
            return this;
        }

        public Builder imageUri(@Nullable Uri uri) {
            imageUri = uri;
            return this;
        }

        public Builder imageUri(@Nullable String uri) {
            if (uri != null) {
                imageUri = Uri.parse(uri);
            }
            return this;
        }

        public Builder location(@Nullable String longitude, @Nullable String latitude) {
            if (longitude != null && latitude != null) {
                eventLongitude = longitude;
                eventLatitude = latitude;
            }
            return this;
        }

        public Builder startDate(@Nullable DateTime date) {
            eventStart = date;
            return this;
        }

        public Builder endDate(@Nullable DateTime date) {
            eventEnd = date;
            return this;
        }

    }
    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getDesc() {
        return mDesc;
    }

    @Nullable
    public Uri getImageUri() {
        return mImageUri;
    }

    @Nullable
    public String getLongitude() {
        return mLongitude;
    }

    @Nullable
    public String getLatitude() {
        return mLatitude;
    }

    @Nullable
    public DateTime getStartDate() {
        return mStartDate;
    }

    @Nullable
    public DateTime getEndDate() {
        return mEndDate;
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs)
            return true;
        if (rhs == null || getClass() != rhs.getClass())
            return false;
        Event event = (Event) rhs;
        return Utils.equals(getId(), event.getId()) &&
                Utils.equals(getName(), event.getName()) &&
                Utils.equals(getDesc(), event.getDesc()) &&
                Utils.equals(getImageUri(), event.getImageUri()) &&
                Utils.equals(getLongitude(), event.getLongitude()) &&
                Utils.equals(getLatitude(), event.getLatitude()) &&
                Utils.equals(getStartDate(), event.getStartDate()) &&
                Utils.equals(getEndDate(), event.getEndDate());
    }
}
