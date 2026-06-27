package com.comixa.app

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import com.comixa.feature.reader.ComicPageKeyer
import com.comixa.feature.reader.PdfPageFetcher
import com.comixa.feature.reader.RarPageFetcher
import com.comixa.feature.reader.ZipPageFetcher
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ComixaApp : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_covers"))
                    .maxSizeBytes(256L * 1024 * 1024)
                    .build()
            }
            .components {
                add(ComicPageKeyer())
                add(PdfPageFetcher.Factory())
                add(ZipPageFetcher.Factory())
                add(RarPageFetcher.Factory())
            }
            .build()
}
