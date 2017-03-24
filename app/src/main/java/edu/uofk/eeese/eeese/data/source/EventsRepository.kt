/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.source

import android.content.Context
import com.squareup.sqlbrite.SqlBrite
import edu.uofk.eeese.eeese.data.DataContract.EventEntry
import edu.uofk.eeese.eeese.data.DataUtils.Events
import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.sync.SyncManager
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.Observable
import rx.schedulers.Schedulers

class EventsRepository(context: Context,
                       private val syncManager: SyncManager) : Repository<Event> {

    private val resolver = context.contentResolver
    private val sqlBrite = SqlBrite.Builder().build()
    private val briteResolver = sqlBrite.wrapContentProvider(resolver, Schedulers.io())

    override fun getOne(spec: Specification): Observable<out Event> {
        val (selection, selectionArgs) =
                (spec as ContentProviderSpecification).toSelectionQuery()
        return RxJavaInterop.toV2Observable(
                briteResolver.createQuery(EventEntry.CONTENT_URI, null,
                        selection, selectionArgs, null, false))
                .map { it.run()!! }
                .map { Events.event(it) }
    }

    override fun get(spec: Specification): Observable<out List<Event>> {
        val (selection, selectionArgs) =
                (spec as ContentProviderSpecification).toSelectionQuery()
        return RxJavaInterop.toV2Observable(
                briteResolver.createQuery(EventEntry.CONTENT_URI, null,
                        selection, selectionArgs, null, false))
                .map { it.run()!! }
                .map { Events.events(it) }

    }

    override fun get(): Observable<out List<Event>> =
            RxJavaInterop.toV2Observable(
                    briteResolver.createQuery(EventEntry.CONTENT_URI, null,
                            null, null, null, false))
                    .map { it.run()!! }
                    .map { Events.events(it) }

    override fun add(event: Event): Completable = Completable.fromAction {
        resolver.insert(EventEntry.CONTENT_URI, Events.values(event))
    }

    override fun addAll(events: Iterable<Event>): Completable = Completable.fromAction {
        val contentValues = events.map { Events.values(it) }.toTypedArray()
        resolver.bulkInsert(EventEntry.CONTENT_URI, contentValues)
    }

    override fun delete(spec: Specification): Completable {
        val (selection, selectionArgs) =
                (spec as ContentProviderSpecification).toSelectionQuery()
        return Completable.fromAction {
            resolver.delete(EventEntry.CONTENT_URI, selection, selectionArgs)
        }
    }

    override fun clear(): Completable = Completable.fromAction {
        resolver.delete(EventEntry.CONTENT_URI, null, null)
    }

    override fun sync(): Completable = Completable.fromAction { syncManager.syncNow() }

}
