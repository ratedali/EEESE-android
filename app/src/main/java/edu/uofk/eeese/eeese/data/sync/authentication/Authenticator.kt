/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.data.sync.authentication

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle


class Authenticator(context: Context) : AbstractAccountAuthenticator(context) {

    override fun getAuthTokenLabel(s: String?): String {
        throw UnsupportedOperationException("this is just a stub authenticator")
    }

    override fun confirmCredentials(authenticatorResponse: AccountAuthenticatorResponse?,
                                    account: Account?, bundle: Bundle?) = null

    override fun updateCredentials(authenticatorResponse: AccountAuthenticatorResponse?,
                                   account: Account?, s: String?, bundle: Bundle?): Bundle {
        throw UnsupportedOperationException("this is just a stub authenticator")
    }

    override fun getAuthToken(authenticatorResponse: AccountAuthenticatorResponse?,
                              account: Account?, s: String?, bundle: Bundle?): Bundle {
        throw UnsupportedOperationException("this is just a stub authenticator")
    }

    override fun hasFeatures(authenticatorResponse: AccountAuthenticatorResponse?,
                             account: Account?, strings: Array<out String>?): Bundle {
        throw UnsupportedOperationException("this is just a stub authenticator")
    }

    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle {
        throw UnsupportedOperationException("this is just a stub authenticator")
    }

    override fun addAccount(authenticatorResponse: AccountAuthenticatorResponse?,
                            s: String?, s1: String?, strings: Array<out String>?,
                            bundle: Bundle?) = null
}
