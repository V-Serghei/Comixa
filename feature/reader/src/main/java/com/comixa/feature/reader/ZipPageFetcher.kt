package com.comixa.feature.reader

import android.graphics.BitmapFactory
import android.net.Uri
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
import net.lingala.zip4j.ZipFile
import java.io.File

class ZipPageFetcher(
    private val options: Options,
    private val key: ComicPageKey,
) : Fetcher {

    class Factory : Fetcher.Factory<ComicPageKey> {
        override fun create(data: ComicPageKey, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (data.format == ComicFormat.CBZ || data.format == ComicFormat.ZIP) {
                ZipPageFetcher(options, data)
            } else null
    }

    override suspend fun fetch(): FetchResult = withContext(Dispatchers.IO) {
        val file = resolveFile()
        val imageBytes = ZipFile(file).use { zip ->
            val imageEntries = zip.fileHeaders
                .filter { !it.isDirectory && isImageFile(it.fileName) }
                .sortedWith(compareBy { it.fileName })
            val header = imageEntries.getOrNull(key.pageIndex)
                ?: error("Page ${key.pageIndex} not found in ${file.name}")
            zip.getInputStream(header).use { it.readBytes() }
        }

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: error("Failed to decode image at page ${key.pageIndex}")

        ImageFetchResult(image = bitmap.asImage(), isSampled = false, dataSource = DataSource.DISK)
    }

    private fun resolveFile(): File {
        val uri = Uri.parse(key.filePath)
        return when (uri.scheme) {
            "file" -> File(uri.path!!)
            "content" -> copyToCache(uri)
            else -> error("Unsupported URI scheme: ${uri.scheme}")
        }
    }

    private fun copyToCache(uri: Uri): File {
        val cacheFile = File(options.context.cacheDir, "zip_${uri.toString().hashCode()}")
        if (!cacheFile.exists()) {
            options.context.contentResolver.openInputStream(uri)!!.use { input ->
                cacheFile.outputStream().use { input.copyTo(it) }
            }
        }
        return cacheFile
    }

    private fun isImageFile(name: String): Boolean {
        val ext = name.substringAfterLast('.', "").lowercase()
        return ext in IMAGE_EXTENSIONS
    }

    companion object {
        private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "avif")
    }
}
