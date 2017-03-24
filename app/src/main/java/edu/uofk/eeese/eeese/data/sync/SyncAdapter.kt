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
import android.os.Bundle
import android.util.Log
import edu.uofk.eeese.eeese.data.DataContract
import edu.uofk.eeese.eeese.data.DataContract.EventEntry
import edu.uofk.eeese.eeese.data.DataContract.ProjectEntry
import edu.uofk.eeese.eeese.data.DataUtils.Events
import edu.uofk.eeese.eeese.data.DataUtils.Projects
import edu.uofk.eeese.eeese.data.Event
import edu.uofk.eeese.eeese.data.Project
import edu.uofk.eeese.eeese.data.backend.ApiWrapper
import edu.uofk.eeese.eeese.data.database.DatabaseHelper
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
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
        val TAG = SyncAdapter::class.java.name
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
                    .onErrorResumeNext { emptyList<Project>().toSingle() }
                    .flattenAsObservable { it }
                    .toMap { it.id }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()

            Log.d(TAG, "Got remote projects")

            val localProjects = Single
                    .just(dbHelper.readableDatabase)
                    .map { it.query(ProjectEntry.TABLE_NAME, null, null, null, null, null, null) }
                    .map { Projects.projects(it) }
                    .map { it.toList() }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()

            Log.d(TAG, "Got local projects")

            projectOps = projectOperations(localProjects, remoteProjects, syncResult)
            Log.d(TAG, "Done with project changes")

        } catch (e: SocketTimeoutException) {
            syncResult.stats.numIoExceptions++
            syncResult.fullSyncRequested = true
        } catch (e: IOException) {
            syncResult.stats.numIoExceptions++
            syncResult.databaseError = true
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
            Log.d(TAG, "Got remote events")


            val localEvents = Single
                    .just(dbHelper.readableDatabase)
                    .map { it.query(EventEntry.TABLE_NAME, null, null, null, null, null, null) }
                    .map { Events.events(it) }
                    .map { it.toList() }
                    .subscribeOn(Schedulers.trampoline())
                    .blockingGet()
            Log.d(TAG, "Got local events")

            eventOps = eventOperations(localEvents, remoteEvents, syncResult)
            Log.d(TAG, "Done with changes")
        } catch (e: SocketTimeoutException) {
            syncResult.stats.numIoExceptions++
            syncResult.fullSyncRequested = true
        } catch (e: IOException) {
            syncResult.stats.numIoExceptions++
        }

        //Apply changes
        val operations = ArrayList(projectOps + eventOps)
        Log.d(TAG, "preforming updates")
        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations)

        //Notify content observers
        if (projectOps.isNotEmpty()) {
            resolver.notifyChange(ProjectEntry.CONTENT_URI, null, false)
        }
        if (eventOps.isNotEmpty()) {
            resolver.notifyChange(EventEntry.CONTENT_URI, null, false)
        }


    }

    private tailrec fun projectOperations(local: List<Project>,
                                          remote: Map<String, Project>,
                                          syncResult: SyncResult,
                                          operations: List<ContentProviderOperation> = emptyList()):
            List<ContentProviderOperation> =
            if (local.isEmpty()) {
                operations + remote
                        .map { it.value }
                        .map {
                            Log.d(TAG, "project added ${it.name}'")
                            Projects.values(it)
                        }
                        .map {
                            syncResult.stats.numInserts++
                            ContentProviderOperation
                                    .newInsert(ProjectEntry.CONTENT_URI)
                                    .withValues(it)
                                    .build()
                        }
            } else {
                val project = local.first()
                val newLocal = local.drop(1)
                val id = project.id
                if (id in remote) {
                    val remoteProject = remote[id]!!
                    val newRemote = remote - id
                    if (project == remoteProject) {
                        Log.d(TAG, "project unchanged ${project.name}")
                        projectOperations(newLocal, newRemote, syncResult, operations)
                    } else {
                        Log.d(TAG, "project updated ${remoteProject.name}")
                        syncResult.stats.numUpdates++
                        val newOperations = operations + ContentProviderOperation
                                .newUpdate(ProjectEntry.CONTENT_URI)
                                .withSelection(
                                        "${ProjectEntry.COLUMN_PROJECT_ID} = ?",
                                        arrayOf(project.id))
                                .withValues(Projects.values(remoteProject))
                                .build()
                        projectOperations(newLocal, newRemote, syncResult, newOperations)
                    }
                } else {
                    Log.d(TAG, "project removed ${project.name}")
                    syncResult.stats.numDeletes++
                    val newOperations = operations + ContentProviderOperation
                            .newDelete(ProjectEntry.CONTENT_URI)
                            .withSelection("${ProjectEntry.COLUMN_PROJECT_ID} = ?", arrayOf(id))
                            .build()
                    projectOperations(newLocal, remote, syncResult, newOperations)
                }
            }


    private tailrec fun eventOperations(local: List<Event>,
                                        remote: Map<String, Event>,
                                        syncResult: SyncResult,
                                        operations: List<ContentProviderOperation> = emptyList()):
            List<ContentProviderOperation> =
            if (local.isEmpty()) {
                operations + remote
                        .map { it.value }
                        .map {
                            Log.d(TAG, "event added ${it.name}'")
                            Events.values(it)
                        }
                        .map {
                            syncResult.stats.numInserts++
                            ContentProviderOperation.newInsert(EventEntry.CONTENT_URI)
                                    .withValues(it)
                                    .build()
                        }
            } else {
                val event = local.first()
                val id = event.id

                val newLocal = local.drop(1)
                if (id in remote) {
                    Log.d(TAG, "event unchanged ${event.name}")
                    val remoteEvent = remote[id]!!
                    val newRemote = remote - id
                    if (event == remoteEvent) {
                        eventOperations(newLocal, newRemote, syncResult, operations)
                    } else {
                        Log.d(TAG, "event updated ${remoteEvent.name}")
                        syncResult.stats.numUpdates++
                        val newOperations = operations + ContentProviderOperation
                                .newUpdate(EventEntry.CONTENT_URI)
                                .withSelection(
                                        "${EventEntry.COLUMN_EVENT_ID} = ?",
                                        arrayOf(event.id))
                                .withValues(Events.values(remoteEvent))
                                .build()
                        eventOperations(newLocal, newRemote, syncResult, newOperations)
                    }
                } else {
                    Log.d(TAG, "event removed ${event.name}")
                    syncResult.stats.numDeletes++
                    val newOperations = operations + ContentProviderOperation
                            .newDelete(EventEntry.CONTENT_URI)
                            .withSelection("${EventEntry.COLUMN_EVENT_ID} = ?", arrayOf(id))
                            .build()
                    eventOperations(newLocal, remote, syncResult, newOperations)
                }
            }
}
