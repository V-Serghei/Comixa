package com.comixa.feature.reader

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.comixa.core.domain.model.ComicFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PdfPageFetcher(
    private val options: Options,
    private val key: ComicPageKey,
) : Fetcher {

    class Factory : Fetcher.Factory<ComicPageKey> {
        override fun create(data: ComicPageKey, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (data.format == ComicFormat.PDF) PdfPageFetcher(options, data) else null
    }

    override suspend fun fetch(): FetchResult = withContext(Dispatchers.IO) {
        val uri = Uri.parse(key.filePath)
        val pfd: ParcelFileDescriptor = when (uri.scheme) {
            "file" -> ParcelFileDescriptor.open(File(uri.path!!), ParcelFileDescriptor.MODE_READ_ONLY)
            "content" -> checkNotNull(options.context.contentResolver.openFileDescriptor(uri, "r"))
            else -> error("Unsupported URI scheme: ${uri.scheme}")
        }

        val bitmap = pfd.use { fd ->
            PdfRenderer(fd).use { renderer ->
                renderer.openPage(key.pageIndex).use { page ->
                    val scale = RENDER_SCALE
                    val bmp = Bitmap.createBitmap(
                        (page.width * scale).toInt(),
                        (page.height * scale).toInt(),
                        Bitmap.Config.ARGB_8888,
                    )
                    bmp.eraseColor(Color.WHITE)
                    page.render(bmp, null, Matrix().apply { setScale(scale, scale) }, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bmp
                }
            }
        }

        ImageFetchResult(image = bitmap.asImage(), isSampled = false, dataSource = DataSource.DISK)
    }

    companion object {
        private const val RENDER_SCALE = 2.0f
    }
}
