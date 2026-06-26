package com.comixa.core.data.scanner.detector;

import com.comixa.core.data.scanner.FormatDetector;
import com.comixa.core.domain.model.ComicFormat;

import javax.inject.Inject;

/**
 * Детектор PDF файлов.
 * Написан на Java — демонстрирует смешение Kotlin и Java в одном модуле.
 *
 * Определяет PDF по расширению и MIME-типу.
 * Magic bytes (%PDF = 0x25 0x50 0x44 0x46) можно проверять опционально,
 * но для сканирования достаточно имени файла.
 */
public class PdfFormatDetector implements FormatDetector {

    // Magic bytes первых 4 байт любого PDF файла
    private static final byte[] PDF_MAGIC = { 0x25, 0x50, 0x44, 0x46 }; // %PDF

    @Inject
    public PdfFormatDetector() {}

    @Override
    public int getPriority() {
        return 20; // PDF проверяется раньше CBZ, нет конфликта расширений
    }

    @Override
    public String getDisplayName() {
        return "PDF";
    }

    @Override
    public boolean canHandle(String fileName, String mimeType) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf")
                || "application/pdf".equals(mimeType)
                || "application/x-pdf".equals(mimeType);
    }

    @Override
    public ComicFormat format() {
        return ComicFormat.PDF;
    }

    /**
     * Дополнительная проверка magic bytes.
     * Вызывается опционально когда есть доступ к потоку файла.
     * Стандартный PDF начинается с байт %PDF (0x25 0x50 0x44 0x46).
     */
    public static boolean hasPdfMagicBytes(byte[] header) {
        if (header == null || header.length < PDF_MAGIC.length) return false;
        for (int i = 0; i < PDF_MAGIC.length; i++) {
            if (header[i] != PDF_MAGIC[i]) return false;
        }
        return true;
    }
}
