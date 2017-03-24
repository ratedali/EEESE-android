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
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Bundle
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.data.DataContract
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope
import edu.uofk.eeese.eeese.util.FrameworkUtils.atLeastKitKat
import javax.inject.Inject

@ApplicationScope
class SyncManager @Inject constructor(private val context: Context) {
    companion object {
        val ACCOUNT_NAME = "edu.uofk.eeese.eeese.sync"
        val FLEX_TIME_IN_MINUTES = 20L
        val FLEX_TIME = FLEX_TIME_IN_MINUTES * 60
        val SYNC_INTERVAL_IN_HOURS = 6L
        val SYNC_INTERVAL = SYNC_INTERVAL_IN_HOURS * 60 * 60
    }

    private var account = Account(ACCOUNT_NAME, context.getString(R.string.sync_account_type))
    private val authority = context.getString(R.string.content_authority)


    fun setupSync(): Unit {
        val accountsManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        if (accountsManager.addAccountExplicitly(account, null, null)) {
            addPeriodicSync()
            ContentResolver.setSyncAutomatically(account, DataContract.CONTENT_AUTHORITY, true)
        } else {
            account = accountsManager.accounts.first()
        }
    }

    @SuppressLint("NewApi")
    fun addPeriodicSync(): Unit {
        if (atLeastKitKat) {
            val request = SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, FLEX_TIME)
                    .setSyncAdapter(account, authority)
                    .setExtras(Bundle.EMPTY)
                    .build()
            ContentResolver.requestSync(request)
        } else {
            ContentResolver.addPeriodicSync(account, DataContract.CONTENT_AUTHORITY,
                    Bundle.EMPTY, SYNC_INTERVAL)
        }
    }

    fun syncNow(): Unit {
        val options = Bundle()
        options.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        options.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        ContentResolver.requestSync(account, DataContract.CONTENT_AUTHORITY, options)
    }
}
