/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.about


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import edu.uofk.eeese.eeese.R
import edu.uofk.eeese.eeese.di.scopes.ActivityScope
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

@ActivityScope
class AboutPresenter @Inject
constructor(private val context: Context,
            private val view: AboutContract.View) : AboutContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    private fun galleryBitmap(): Single<Uri> {
        return Single.just(
                listOf(R.drawable.gallery_1, R.drawable.gallery_2, R.drawable.gallery_3))
                .map {
                    val index = Random().nextInt(it.size)
                    it[index]
                }
                .map {
                    Uri.Builder()
                            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(context.resources.getResourcePackageName(it))
                            .appendPath(context.resources.getResourceTypeName(it))
                            .appendPath(context.resources.getResourceEntryName(it))
                            .build()
                }
    }

    override fun loadGalleryImage() {
        val subscription = galleryBitmap().subscribe(
                // On Success
                { view.showGalleryImage(it) },
                // On Error
                { })
        subscriptions.add(subscription)
    }

    override fun galleryCardClicked() {
        view.openGallery()
    }

    override fun subscribe() {
        view.setPresenter(this)
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }
}
