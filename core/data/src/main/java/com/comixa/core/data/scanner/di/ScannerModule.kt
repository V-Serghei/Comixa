package com.comixa.core.data.scanner.di

import com.comixa.core.data.scanner.FormatDetector
import com.comixa.core.data.scanner.detector.CbzFormatDetector
import com.comixa.core.data.scanner.detector.PdfFormatDetector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Регистрирует детекторы форматов через Hilt multibindings.
 *
 * Чтобы добавить новый формат (CBR, EPUB и т.д.):
 * 1. Создать класс, реализующий FormatDetector (Kotlin или Java)
 * 2. Добавить сюда одну @Binds @IntoSet строчку
 * FileScanner подхватит его автоматически.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerModule {

    @Binds
    @IntoSet
    abstract fun bindCbzDetector(impl: CbzFormatDetector): FormatDetector

    @Binds
    @IntoSet
    abstract fun bindPdfDetector(impl: PdfFormatDetector): FormatDetector

    // Будущие форматы — просто раскомментировать:
    // @Binds @IntoSet abstract fun bindCbrDetector(impl: CbrFormatDetector): FormatDetector
    // @Binds @IntoSet abstract fun bindEpubDetector(impl: EpubFormatDetector): FormatDetector
}
