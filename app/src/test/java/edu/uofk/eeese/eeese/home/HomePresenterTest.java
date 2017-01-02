/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.home;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.uofk.eeese.eeese.data.source.DataRepository;
import edu.uofk.eeese.eeese.util.schedulers.ImmediateSchedulerProvider;
import io.reactivex.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomePresenterTest {

    @Mock
    private DataRepository source;
    @Mock
    private HomeContract.View view;

    private HomePresenter presenter;

    @Before
    public void setupPresenter() {
        MockitoAnnotations.initMocks(this);
        presenter = new HomePresenter(source, view, ImmediateSchedulerProvider.getInstance());
    }

    @Test
    public void subscriptionShowsTheInfoString() {
        String basicInfo = "basic info";
        when(source.getBasicInfo()).thenReturn(Observable.just(basicInfo));
        presenter.subscribe();
        verify(view).showInfo(basicInfo);
    }

    @Test
    public void shouldCallShowErrorWhenSourceError() {
        when(source.getBasicInfo()).thenReturn(Observable.<String>error(new Exception()));
        presenter.subscribe();
        verify(view).showLoadingError();
    }
}
