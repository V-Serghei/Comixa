package com.comixa.core.data.scanner

data class ParsedTitle(
    val displayTitle: String,
    val seriesName: String,
    val issueNumber: Int?,
    val volumeNumber: Int?,
)

object ComicTitleParser {

    // Noise tags in parens: (Digital), (HQ), (DC), (Minutemen-Scanner)
    // KEEP year parens: (1963), (2014) — exactly 4 digits
    private val NOISE_PARENS = Regex("""\s*\((?!\d{4}\s*\))([^)]*)\)""")

    private val ISSUE_HASH = Regex("""#(\d+)""")
    private val ISSUE_CHAPTER = Regex("""(?i)\b(?:ch(?:apter)?|iss(?:ue)?)[.\s]+(\d+)""")
    private val VOLUME = Regex("""(?i)\b(?:vol(?:ume)?\.?\s*(\d+)|v(\d+))\b""")

    // Trailing bare number not in a year range (1800-2099): "Saga 001", "Batman 023"
    private val TRAILING_BARE = Regex("""(?:^|[\s_])(\d{1,4})\s*$""")

    fun parse(fileName: String): ParsedTitle {
        val raw = fileName.substringBeforeLast('.')
            .replace('_', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()

        val cleaned = NOISE_PARENS.replace(raw, "").trim()

        val volumeNumber = VOLUME.find(cleaned)?.let { m ->
            m.groupValues[1].toIntOrNull() ?: m.groupValues[2].toIntOrNull()
        }

        val issueNumber = ISSUE_HASH.find(cleaned)?.groupValues?.get(1)?.toIntOrNull()
            ?: ISSUE_CHAPTER.find(cleaned)?.groupValues?.get(1)?.toIntOrNull()
            ?: TRAILING_BARE.find(cleaned)?.groupValues?.get(1)
                ?.toIntOrNull()?.takeIf { it < 1800 || it > 2099 }

        val seriesName = buildSeriesName(cleaned)

        return ParsedTitle(
            displayTitle = cleaned,
            seriesName = seriesName.ifBlank { cleaned },
            issueNumber = issueNumber,
            volumeNumber = volumeNumber,
        )
    }

    private fun buildSeriesName(text: String): String =
        text
            .replace(ISSUE_HASH, "")
            .replace(ISSUE_CHAPTER, "")
            .replace(VOLUME, "")
            // Strip bare issue number that sits just before a year: "001 (1963)" → "(1963)"
            .replace(Regex("""\s+\d{1,4}(?=\s*\(\d{4}\))"""), "")
            // Strip trailing bare number
            .replace(TRAILING_BARE, "")
            // Clean trailing punctuation/separators
            .replace(Regex("""[-–—,;\s]+$"""), "")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
}
