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

import java.util.Date;

import edu.uofk.eeese.eeese.util.ObjectUtils;

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
    private String mLocation;
    @Nullable
    private Date mStartDate;
    @Nullable
    private Date mEndDate;

    private Event(String id,
                  String name,
                  String desc,
                  Uri imageUri,
                  String location,
                  Date startDate,
                  Date endDate) {
        mId = id;
        mName = name;
        mDesc = desc;
        mImageUri = imageUri;
        mLocation = location;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public static class Builder {

        private String eventId;
        private String eventName;
        private String eventDesc;
        private Uri imageUri;
        private String eventLocation;
        private Date eventStart;
        private Date eventEnd;
        public Builder(@NonNull String id, @NonNull String name) {
            eventId = id;
            eventName = name;
            eventDesc = "";
        }

        public Event build() {
            return new Event(eventId, eventName, eventDesc,
                    imageUri, eventLocation,
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

        public Builder imageUri(@NonNull String uri) {
            imageUri = Uri.parse(uri);
            return this;
        }

        public Builder location(@NonNull String longitude, @NonNull String latitude) {
            eventLocation = longitude + "," + latitude;
            return this;
        }

        public Builder startDate(@NonNull Date date) {
            eventStart = date;
            return this;
        }

        public Builder endDate(@NonNull Date date) {
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
        if (mLocation != null) {
            return mLocation.split(",")[0];
        }
        return null;
    }

    @Nullable
    public String getLatitude() {
        if (mLocation != null) {
            return mLocation.split(",")[1];
        }
        return null;
    }

    @Nullable
    public Date getStartDate() {
        return mStartDate;
    }

    @Nullable
    public Date getEndDate() {
        return mEndDate;
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs)
            return true;
        if (rhs == null || getClass() != rhs.getClass())
            return false;
        Event event = (Event) rhs;
        return ObjectUtils.equals(mId, event.mId) &&
                ObjectUtils.equals(mName, event.mName) &&
                ObjectUtils.equals(mDesc, event.mDesc) &&
                ObjectUtils.equals(mImageUri, event.mImageUri) &&
                ObjectUtils.equals(mLocation, event.mLocation) &&
                ObjectUtils.equals(mStartDate, event.mStartDate) &&
                ObjectUtils.equals(mEndDate, event.mEndDate);
    }
}
