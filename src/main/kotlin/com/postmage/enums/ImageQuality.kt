package com.postmage.enums

enum class ImageQuality(val ratio: Double, val quality: Int) {
    PERFORMANCE(ratio = 0.15, quality = 1),
    STANDARD(ratio = 0.40, quality = 2),
    HIGH(ratio = 0.60, quality = 3),
    SOURCE(ratio = 1.0, quality = 4);

    object QualityValues {
        val map: Map<Int, ImageQuality> = ImageQuality.values().associateBy { it.quality }
    }
}

fun getImageQuality(ratio: Int?): ImageQuality {
    return ImageQuality.QualityValues.map[ratio] ?: ImageQuality.SOURCE
}