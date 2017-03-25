/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.sync

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import edu.uofk.eeese.eeese.data.DataContract.EventEntry
import edu.uofk.eeese.eeese.data.DataContract.ProjectEntry
import edu.uofk.eeese.eeese.data.DataUtils.Events
import edu.uofk.eeese.eeese.data.DataUtils.Projects
import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.backend.ApiWrapper
import edu.uofk.eeese.eeese.data.database.DatabaseHelper
import edu.uofk.eeese.eeese.util.FrameworkUtils.atLeastMarshmallow
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException

class SyncAdapter(context: Context,
                  private val backendClient: ApiWrapper,
                  private val dbHelper: DatabaseHelper,
                  autoInitialize: Boolean, allowParallelSyncs: Boolean) :
        AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {


    constructor(context: Context,
                backendClient: ApiWrapper,
                dbHelper: DatabaseHelper,
                autoInitialize: Boolean) :
            this(context, backendClient, dbHelper, autoInitialize, false)

    companion object {
        val TAG: String = SyncAdapter::class.java.name
    }

    private val resolver = context.contentResolver!!


    @SuppressLint("Recycle")
    override fun onPerformSync(account: Account, extra: Bundle,
                               authority: String,
                               provider: ContentProviderClient,
                               syncResult: SyncResult) {


        Log.d(TAG, "started sync")

        Log.d(TAG, "syncing projects")

        var projectOps = emptyList<ContentProviderOperation>()
        try {
            //Projects Sync
            val remoteProjects = backendClient.projects()
                    .flattenAsObservable { it }
                    .toMap { it.id }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()

            Log.d(TAG, "Got remote projects: ${remoteProjects.size} projects")

            val localProjects = Single
                    .just(dbHelper.readableDatabase)
                    .map { it.query(ProjectEntry.TABLE_NAME, null, null, null, null, null, null) }
                    .map { Projects.projects(it) }
                    .map { it.toList() }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()

            Log.d(TAG, "Got local projects: ${localProjects.size} projects")

            projectOps = projectOperations(localProjects, remoteProjects)

            Log.d(TAG, "finished projects calculations: ${projectOps.size} operations")

        } catch (e: SocketTimeoutException) {
            syncResult.stats.numIoExceptions++
            syncResult.fullSyncRequested = true
        } catch (e: IOException) {
            syncResult.stats.numIoExceptions++
            syncResult.databaseError = true
        } catch (ignored: Exception) {
            Log.e(TAG, "Unknown exception: $ignored")
        }

        //Events Sync
        Log.d(TAG, "syncing events")
        var eventOps = emptyList<ContentProviderOperation>()

        try {

            val remoteEvents = backendClient.events()
                    .flattenAsObservable { it }
                    .toMap { it.id }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()
            Log.d(TAG, "Got remote events: ${remoteEvents.size} events")


            val localEvents = Single
                    .just(dbHelper.readableDatabase)
                    .map { it.query(EventEntry.TABLE_NAME, null, null, null, null, null, null) }
                    .map { Events.events(it) }
                    .map { it.toList() }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()
            Log.d(TAG, "Got local events: ${localEvents.size} events")

            eventOps = eventOperations(localEvents, remoteEvents)

            Log.d(TAG, "finished projects calculations: ${eventOps.size} operations")
        } catch (e: SocketTimeoutException) {
            syncResult.stats.numIoExceptions++
            syncResult.fullSyncRequested = true

        } catch (e: IOException) {
            syncResult.stats.numIoExceptions++

        } catch (ignored: Exception) {
            Log.e(TAG, "Unknown exception: $ignored")
        }

        //Apply changes
        val operations = ArrayList(projectOps + eventOps)

        @SuppressLint("NewApi")
        if (atLeastMarshmallow) {
            syncResult.stats.numEntries = operations.count().toLong()
            syncResult.stats.numInserts = operations.count { it.isInsert }.toLong()
            syncResult.stats.numUpdates = operations.count { it.isUpdate }.toLong()
            syncResult.stats.numDeletes = operations.count { it.isDelete }.toLong()

            Log.d(TAG, "Total Number: ${syncResult.stats.numEntries}")
            Log.d(TAG, "Insertions: ${syncResult.stats.numInserts}")
            Log.d(TAG, "Updates: ${syncResult.stats.numUpdates}")
            Log.d(TAG, "Deletions: ${syncResult.stats.numDeletes}")
        }
        Log.d(TAG, "preforming updates")
        resolver.applyBatch(authority, operations)

        //Notify content observers
        if (projectOps.isNotEmpty()) {
            resolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
        }
        if (eventOps.isNotEmpty()) {
            resolver.notifyChange(EventEntry.CONTENT_URI, null, false)
        }
    }

    private fun projectOperations(local: List<Project>,
                                  remote: Map<String, Project>): List<ContentProviderOperation> =
            calculateOperations(uri = ProjectEntry.CONTENT_URI,
                    idColumnName = ProjectEntry.COLUMN_PROJECT_ID,
                    local = local, remote = remote,
                    getId = Project::id,
                    toContentValues = { Projects.values(it) })


    private fun eventOperations(local: List<Event>,
                                remote: Map<String, Event>): List<ContentProviderOperation> =
            calculateOperations(uri = EventEntry.CONTENT_URI,
                    idColumnName = EventEntry.COLUMN_EVENT_ID,
                    local = local, remote = remote,
                    getId = Event::id,
                    toContentValues = { Events.values(it) })

    private tailrec fun <Item, ID> calculateOperations(uri: Uri,
                                                       idColumnName: String,
                                                       local: List<Item>,
                                                       remote: Map<in ID, Item>,
                                                       getId: (Item) -> ID,
                                                       toContentValues: (Item) -> ContentValues,
                                                       operations: List<ContentProviderOperation>
                                                       = emptyList()):
            List<ContentProviderOperation> =
            if (local.isEmpty()) {
                // If there are no local items just add all the remote items
                operations + remote
                        .map { it.value }
                        .map { toContentValues(it) }
                        .map {
                            ContentProviderOperation.newInsert(uri)
                                    .withValues(it)
                                    .build()
                        }
            } else {
                // If there is a local item...
                val item = local.first()
                val id = getId(item)
                val newLocal = local.drop(1)

                if (id in remote) {
                    // and the same id exists in the remote list...
                    val remoteItem = remote[id]!!
                    val newRemote = remote - id
                    if (item == remoteItem) {
                        // and it was not changed, just skip it
                        calculateOperations(uri, idColumnName,
                                newLocal, newRemote, getId, toContentValues, operations)
                    } else {
                        // and it was changed, update it
                        val newOperations = operations + ContentProviderOperation
                                .newUpdate(uri)
                                .withSelection("$idColumnName = ?", arrayOf(id.toString()))
                                .withValues(toContentValues(remoteItem))
                                .build()
                        calculateOperations(uri, idColumnName,
                                newLocal, newRemote, getId, toContentValues, newOperations)
                    }
                } else {
                    // but it doesn't exist in the remote list, delete it
                    val newOperations = operations + ContentProviderOperation
                            .newDelete(uri)
                            .withSelection("$idColumnName = ?", arrayOf(id.toString()))
                            .build()
                    calculateOperations(uri, idColumnName,
                            newLocal, remote, getId, toContentValues, newOperations)
                }
            }

}
