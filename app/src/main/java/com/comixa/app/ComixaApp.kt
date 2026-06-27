package com.comixa.app

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.comixa.feature.reader.PdfPageFetcher
import com.comixa.feature.reader.RarPageFetcher
import com.comixa.feature.reader.ZipPageFetcher
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ComixaApp : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components {
                add(PdfPageFetcher.Factory())
                add(ZipPageFetcher.Factory())
                add(RarPageFetcher.Factory())
            }
            .build()
}
