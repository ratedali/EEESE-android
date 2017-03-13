/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.backend;

import java.util.List;

import edu.uofk.eeese.eeese.data.Project;

public final class JSONContract {
    private JSONContract() {
    }

    static class ProjectJSON {
        /**
         * The model of the json file
         */
        public String id;
        public String name;
        public String desc;
        public String head;
        public List<String> prereq;
        public String category;
    }

    private abstract class CategoryJSON {
        public static final String POWER = "power";
        public static final String TELECOM = "telecom";
        public static final String ELECTRONICS_CONTROL = "electronics & control";
        public static final String SOFTWARE = "software";
    }


    public static String getJsonCategory(@Project.ProjectCategory int category) {
        switch (category) {
            case Project.POWER:
                return CategoryJSON.POWER;
            case Project.TELECOM:
                return CategoryJSON.TELECOM;
            case Project.SOFTWARE:
                return CategoryJSON.SOFTWARE;
            case Project.ELECTRONICS_CONTROL:
                return CategoryJSON.ELECTRONICS_CONTROL;
        }
        return null;
    }

    @Project.ProjectCategory
    public static int getCategory(String jsonCategory) {
        @Project.ProjectCategory int category = Project.POWER;
        switch (jsonCategory) {
            case CategoryJSON.POWER:
                category = Project.POWER;
                break;
            case CategoryJSON.TELECOM:
                category = Project.TELECOM;
                break;
            case CategoryJSON.SOFTWARE:
                category = Project.SOFTWARE;
                break;
            case CategoryJSON.ELECTRONICS_CONTROL:
                category = Project.ELECTRONICS_CONTROL;
                break;
        }
        return category;
    }

}
