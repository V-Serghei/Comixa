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
import com.github.junrar.Archive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class RarPageFetcher(
    private val options: Options,
    private val key: ComicPageKey,
) : Fetcher {

    class Factory : Fetcher.Factory<ComicPageKey> {
        override fun create(data: ComicPageKey, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (data.format == ComicFormat.CBR) RarPageFetcher(options, data) else null
    }

    override suspend fun fetch(): FetchResult = withContext(Dispatchers.IO) {
        val file = resolveFile()
        val imageBytes = Archive(file).use { archive ->
            val imageHeaders = archive.fileHeaders
                .filter { !it.isDirectory && isImageFile(it.fileName) }
                .sortedBy { it.fileName }
            val header = imageHeaders.getOrNull(key.pageIndex)
                ?: error("Page ${key.pageIndex} not found in ${file.name}")
            val out = ByteArrayOutputStream()
            archive.extractFile(header, out)
            out.toByteArray()
        }

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: error("Failed to decode RAR image at page ${key.pageIndex}")

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
        val cacheFile = File(options.context.cacheDir, "rar_${uri.toString().hashCode()}")
        if (!cacheFile.exists()) {
            options.context.contentResolver.openInputStream(uri)!!.use { input ->
                cacheFile.outputStream().use { input.copyTo(it) }
            }
        }
        return cacheFile
    }

    private fun isImageFile(name: String): Boolean {
        val ext = name.substringAfterLast('.', "").lowercase()
        return ext in setOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "avif")
    }
}
