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
import edu.uofk.eeese.eeese.data.DataContract.ProjectEntry
import edu.uofk.eeese.eeese.data.DataUtils.Projects
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.sync.SyncManager
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers as V2Schedulers
import rx.schedulers.Schedulers as V1Schedulers

class ProjectsRepository(context: Context,
                         private val syncManager: SyncManager) :
        Repository<Project> {

    private val resolver = context.contentResolver
    private val sqlBrite = SqlBrite.Builder().build()
    private val briteResolver = sqlBrite.wrapContentProvider(resolver, V1Schedulers.io())

    override fun getOne(spec: Specification): Observable<Project> {
        val (selection, selectionArgs) =
                (spec as ContentProviderSpecification).toSelectionQuery()
        return RxJavaInterop.toV2Observable(
                briteResolver.createQuery(ProjectEntry.CONTENT_URI, null,
                        selection, selectionArgs, null, false))
                .map { it.run()!! }
                .map { Projects.project(it) }
    }

    override fun get(spec: Specification): Observable<List<Project>> {
        val (selection, selectionArgs) =
                (spec as ContentProviderSpecification).toSelectionQuery()
        return RxJavaInterop.toV2Observable(
                briteResolver.createQuery(ProjectEntry.CONTENT_URI, null,
                        selection, selectionArgs, null, false))
                .map { it.run()!! }
                .map { Projects.projects(it) }
    }

    override fun get(): Observable<List<Project>> = RxJavaInterop.toV2Observable(
            briteResolver.createQuery(ProjectEntry.CONTENT_URI, null, null, null, null, false))
            .map { it.run()!! }
            .map { Projects.projects(it) }

    override fun add(event: Project): Completable = Completable.fromAction {
        resolver.insert(ProjectEntry.CONTENT_URI, Projects.values(event))
    }

    override fun addAll(events: Iterable<Project>): Completable = Completable.fromAction {
        val contentValues = events.map { Projects.values(it) }.toTypedArray()
        resolver.bulkInsert(ProjectEntry.CONTENT_URI, contentValues)
    }

    override fun delete(spec: Specification): Completable {
        val (selection, selectionArgs) =
                (spec as ContentProviderSpecification).toSelectionQuery()
        return Completable.fromAction {
            resolver.delete(ProjectEntry.CONTENT_URI, selection, selectionArgs)
        }
    }

    override fun clear(): Completable = Completable.fromAction {
        resolver.delete(ProjectEntry.CONTENT_URI, null, null)
    }

    override fun sync(): Completable = Completable.fromAction { syncManager.syncNow() }
}
