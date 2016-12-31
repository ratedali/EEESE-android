/*
 * Copyright 2016 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.source;


import android.content.Context;
import android.support.annotation.NonNull;

import edu.uofk.eeese.eeese.R;
import io.reactivex.Observable;

public class FakeDataRepository implements DataRepository {

    private static FakeDataRepository sInstance = null;

    private String basicInfo;


    // Prevent direct instantiation
    private FakeDataRepository(@NonNull Context context) {
        basicInfo = context.getString(R.string.basic_info);
    }

    public static FakeDataRepository getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new FakeDataRepository(context);
        }
        return sInstance;
    }

    @Override
    public Observable<String> getBasicInfo() {
        return Observable.just(basicInfo);
    }
}
