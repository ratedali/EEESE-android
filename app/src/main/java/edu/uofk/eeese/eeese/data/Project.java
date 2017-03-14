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

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

import edu.uofk.eeese.eeese.util.ObjectUtils;

public class Project {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SOFTWARE, POWER, TELECOM, ELECTRONICS_CONTROL})
    public @interface ProjectCategory {
    }


    public static final int NUM_OF_CATEGORIES = 4;
    public static final int SOFTWARE = 0;
    public static final int POWER = 1;
    public static final int TELECOM = 2;
    public static final int ELECTRONICS_CONTROL = 3;

    @NonNull
    private String mId;
    @NonNull
    private String mName;
    @NonNull
    private String mDesc;
    @NonNull
    private String mProjectHead;
    @ProjectCategory
    private int mCategory;
    @NonNull
    private List<String> mPrerequisites;

    private Project(@NonNull String id,
                    @NonNull String name,
                    @NonNull String projectHead,
                    @Nullable String desc,
                    @ProjectCategory int category,
                    @Nullable List<String> prerequisites) {
        mId = id;
        mName = name;
        mDesc = desc != null ? desc : "";
        mProjectHead = projectHead;
        mCategory = category;
        mPrerequisites = prerequisites != null ? prerequisites : Collections.<String>emptyList();
    }

    public static class Builder {
        private String projectId;
        private String projectName;
        private String projectDesc;
        private String projectHead;
        @ProjectCategory
        private int projectCategory;
        private List<String> projectPrereqs;

        public Builder(@NonNull String id, @NonNull String name,
                       @NonNull String head, @ProjectCategory int category) {
            projectId = id;
            projectName = name;
            projectHead = head;
            projectDesc = null;
            projectCategory = category;
        }

        public Builder withDesc(@Nullable String desc) {
            projectDesc = desc;
            return this;
        }

        public Builder withPrerequisites(List<String> prerequisites) {
            projectPrereqs = prerequisites;
            return this;
        }

        public Project build() {
            return new Project(
                    projectId,
                    projectName,
                    projectHead,
                    projectDesc,
                    projectCategory,
                    projectPrereqs);
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

    public
    @ProjectCategory
    int getCategory() {
        return mCategory;
    }

    @NonNull
    public List<String> getPrerequisites() {
        return mPrerequisites;
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
                && ObjectUtils.equals(mDesc, project.mDesc)
                && ObjectUtils.equals(mCategory, project.mCategory)
                && ObjectUtils.equals(mPrerequisites, project.mPrerequisites);
    }

    @Override
    public String toString() {
        return "Project Name: " + getName();
    }
}
