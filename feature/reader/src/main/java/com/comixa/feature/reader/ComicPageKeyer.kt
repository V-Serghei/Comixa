package com.comixa.feature.reader

import coil3.key.Keyer
import coil3.request.Options
import com.comixa.core.domain.model.ComicPageKey

class ComicPageKeyer : Keyer<ComicPageKey> {
    override fun key(data: ComicPageKey, options: Options): String =
        "comic:${data.filePath}:${data.pageIndex}:${data.format.name}"
}
