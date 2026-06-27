package com.comixa.app

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import com.comixa.feature.reader.ComicPageKeyer
import com.comixa.feature.reader.PdfPageFetcher
import com.comixa.feature.reader.RarPageFetcher
import com.comixa.feature.reader.ZipPageFetcher
import dagger.hilt.android.HiltAndroidApp
import okio.Path.Companion.toOkioPath

@HiltAndroidApp
class ComixaApp : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("coil_covers").toOkioPath())
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
