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

import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.source.Repository
import edu.uofk.eeese.eeese.di.scopes.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class EventsPresenter @Inject
constructor(private val view: EventsContract.View,
            private val source: Repository<Event>) : EventsContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun loadEvents(forceUpdate: Boolean): Unit {
        if (forceUpdate) {
            view.showLoadingIndicator()
            val sync = source.sync().subscribe()
            subscriptions.add(sync)
        }

        val subscription =
                source.get()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                //On Next
                                {
                                    if (it.isNotEmpty()) {
                                        view.showEvents(it)
                                    } else {
                                        view.showNoEvents()
                                    }
                                    view.hideLoadingIndicator()
                                },
                                //OnError
                                {
                                    view.showConnectionError()
                                })
        subscriptions.add(subscription)
    }

    override fun subscribe(): Unit {}

    override fun unsubscribe(): Unit {
        subscriptions.clear()
    }
}
