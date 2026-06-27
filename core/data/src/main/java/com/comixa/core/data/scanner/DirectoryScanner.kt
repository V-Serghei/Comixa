package com.comixa.core.data.scanner

import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class DirectoryScanner @Inject constructor(
    private val detectors: Set<@JvmSuppressWildcards FormatDetector>,
) {
    private val sortedDetectors by lazy {
        detectors.sortedByDescending { it.priority }
    }

    fun scan(root: File = Environment.getExternalStorageDirectory()): Flow<ScannedFile> = flow {
        root.walkTopDown()
            .onEnter { dir ->
                // Skip hidden dirs and Android system dirs (data, obb) — unreadable anyway
                !dir.name.startsWith(".") && dir.name != "Android"
            }
            .filter { it.isFile }
            .forEach { file ->
                val detector = sortedDetectors.firstOrNull { it.canHandle(file.name, "") }
                    ?: return@forEach
                emit(
                    ScannedFile(
                        uri = Uri.fromFile(file),
                        fileName = file.name,
                        format = detector.format(),
                        sizeBytes = file.length(),
                    )
                )
            }
    }.flowOn(Dispatchers.IO)
}
