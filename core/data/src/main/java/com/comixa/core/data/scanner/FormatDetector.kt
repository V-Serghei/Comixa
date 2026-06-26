package com.comixa.core.data.scanner

import com.comixa.core.domain.model.ComicFormat

/**
 * Стратегия распознавания формата файла.
 *
 * Интерфейс намеренно Java-совместимый (без suspend, без Flow) —
 * реализации могут быть написаны на Java.
 *
 * Чтобы добавить новый формат:
 * 1. Создать класс, реализующий FormatDetector
 * 2. Добавить @Binds @IntoSet в ScannerModule
 * Больше ничего менять не нужно.
 */
interface FormatDetector {

    /**
     * Приоритет детектора. Детекторы проверяются в порядке убывания.
     * Нужен для конфликтов: CBZ и ZIP оба — zip-архивы, CBZ должен
     * проверяться первым.
     */
    val priority: Int

    /** Читаемое имя для логов и UI */
    val displayName: String

    /**
     * Быстрая проверка по имени файла и MIME-типу.
     * Должна работать без чтения содержимого файла.
     */
    fun canHandle(fileName: String, mimeType: String): Boolean

    /** Формат, который этот детектор распознаёт */
    fun format(): ComicFormat
}
