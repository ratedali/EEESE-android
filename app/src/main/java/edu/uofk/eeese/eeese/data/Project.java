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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.uofk.eeese.eeese.util.ObjectUtils;

public class Project {

    @NonNull
    private String mId;
    @NonNull
    private String mName;
    @NonNull
    private String mDesc;
    @NonNull
    private String mProjectHead;

    private Project(@NonNull String id,
                    @NonNull String name,
                    @NonNull String projectHead,
                    @Nullable String desc) {
        mId = id;
        mName = name;
        mDesc = desc != null ? desc : "";
        mProjectHead = projectHead;
    }

    public static class Builder {
        @NonNull
        private String projectId;
        @NonNull
        private String projectName;
        @Nullable
        private String projectDesc;
        @NonNull
        private String projectHead;

        public Builder(@NonNull String id, @NonNull String name, @NonNull String head) {
            projectId = id;
            projectName = name;
            projectHead = head;
            projectDesc = null;
        }

        public Builder withDesc(@Nullable String desc) {
            projectDesc = desc;
            return this;
        }

        public Project build() {
            return new Project(
                    projectId,
                    projectName,
                    projectHead,
                    projectDesc);
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

    @Nullable
    public String getDesc() {
        return mDesc;
    }

    @NonNull
    public String getProjectHead() {
        return mProjectHead;
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs)
            return true;
        if (rhs == null || getClass() != rhs.getClass())
            return false;
        Project project = (Project) rhs;
        return ObjectUtils.equals(mId, project.mId)
                && ObjectUtils.equals(mName, project.mName)
                && ObjectUtils.equals(mProjectHead, project.mProjectHead)
                && ObjectUtils.equals(mDesc, project.mDesc);
    }

    @Override
    public String toString() {
        return "Project Name: " + getName();
    }
}
